package dinowolf.database.h2;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.features.FeaturesDatabase;
import dinowolf.features.Feature;
import dinowolf.features.FeatureHashSet;
import dinowolf.features.FeatureImpl;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesHashMap;
import dinowolf.features.FeaturesMap;

public class FeaturesDatabaseH2 implements FeaturesDatabase {
	private static final Logger l = LoggerFactory.getLogger(FeaturesDatabaseH2.class);
	private String connectionString;
	private String username;
	private String password;

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			l.error("Error loading JDBC driver", e);
		}
	}
	
	public FeaturesDatabaseH2(File location, String user, String password) {
		this(location, "dinowolf", user, password, "MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO");
	}

	public FeaturesDatabaseH2(File location, String dbname, String user, String password) {
		this(location, dbname, user, password, "MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO");
	}
	public FeaturesDatabaseH2(File location, String dbname, String user, String password, String options) {
		this.connectionString = "jdbc:h2:file:" + location.getAbsolutePath() + "/" + dbname + ";" + options;
		this.username = user;
		this.password = password;
		
		try (Connection conn = getConnection()){
			conn.setAutoCommit(false);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR_FEATURE);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Connection getConnection() throws IOException {
		Connection conn;
		try {
			conn = DriverManager.getConnection(connectionString, username, password);
		} catch (SQLException e) {
			throw new IOException(e);
		}
		return conn;
	}

	public FeaturesDatabaseH2(File location) {
		this(location, "dinowolf", "dinowolf");
	}

	@Override
	public FeatureSet getFeatures() throws IOException {

		try (Connection conn = getConnection();
				ResultSet rs = conn.createStatement().executeQuery(H2Queries.SELECT_ALL_FEATURES)) {
			FeatureHashSet set = new FeatureHashSet();
			while (rs.next()) {
				set.add(new FeatureImpl(rs.getString(3), rs.getString(4), H2Queries.toFeatureLevel(rs.getInt(5)),
						rs.getBoolean(6)));
			}
			return set;
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	@Override
	public FeaturesMap getFeatures(String bundleId) throws IOException {
		int dbId = getBundleIdByName(bundleId);
		try (Connection conn = getConnection();
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_FEATURES_OF_BUNDLE)) {
			st.setInt(1, dbId);
			ResultSet rs = st.executeQuery();
			FeaturesMap map = new FeaturesHashMap();
			while (rs.next()) {
				String portpair = rs.getString(1);
				if (!map.containsKey(portpair)) {
					map.put(portpair, new FeatureHashSet());
				}
				map.get(portpair).add(new FeatureImpl(rs.getString(3), rs.getString(4),
						H2Queries.toFeatureLevel(rs.getInt(5)), rs.getBoolean(6)));
			}
			return map;
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public void put(String bundle, FeaturesMap featureMap) throws IOException {
		try (Connection conn = getConnection()) {
			try {
				conn.setAutoCommit(false);
				try (PreparedStatement deleteBundle = conn.prepareStatement(H2Queries.DELETE_BUNDLE)){
					deleteBundle.setString(1, bundle);
				}
				int bundleDbId = insertBundle(bundle, conn);
				ResultSet generated;
				try (PreparedStatement insertPortPair = conn.prepareStatement(H2Queries.INSERT_PORTPAIR,
						Statement.RETURN_GENERATED_KEYS);
						PreparedStatement featureExists = conn.prepareStatement(H2Queries.SELECT_FEATURE_EXISTS);
						PreparedStatement insertFeature = conn.prepareStatement(H2Queries.INSERT_FEATURE,
								Statement.RETURN_GENERATED_KEYS);
						PreparedStatement insertPortPairFeature = conn
								.prepareStatement(H2Queries.INSERT_PORTPAIR_FEATURE,
										Statement.RETURN_GENERATED_KEYS)) {
					for(Entry<String,FeatureSet> entry: featureMap.entrySet()){
						// Portpair
						insertPortPair.setInt(1, bundleDbId);
						insertPortPair.setString(2, entry.getKey());
						insertPortPair.execute();
						generated = insertPortPair.getGeneratedKeys();
						generated.next();
						int portPairId = generated.getInt(1);
						
						for(Feature f:entry.getValue()){
							// Feature
							int featureId = 0;
							featureExists.setInt(1, f.hashCode());
							ResultSet r = featureExists.executeQuery();
							if(r.next()){
								// Get it
							}else{
								insertFeature.setInt(1, f.hashCode());
								insertFeature.setString(2, f.getName());
								insertFeature.setCharacterStream(3, new StringReader(f.getValue()));
								insertFeature.setInt(4, H2Queries.toInt(f.getLevel()));
								insertFeature.setBoolean(5, f.isTokenizable());
								insertFeature.execute();
								r = insertFeature.getGeneratedKeys();
							}
							featureId = r.getInt(1);
							
							// Insert port pair
							insertPortPairFeature.setInt(1, portPairId);
							insertPortPairFeature.setInt(2, featureId);
							insertPortPairFeature.execute();
							
						}
					}
				} catch (SQLException e) {
					throw new IOException(e);
				}
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
			}
		} catch (SQLException e1) {
			throw new IOException(e1);
		}
	}

	private int insertBundle(String bundle, Connection conn) throws IOException {
		int bundleDbId = getBundleIdByName(bundle);
		if (bundleDbId == 0) {
			try (PreparedStatement stm = conn.prepareStatement(H2Queries.INSERT_BUNDLE,
					Statement.RETURN_GENERATED_KEYS)) {
				stm.setString(1, bundle);
				stm.execute();
				ResultSet generated = stm.getGeneratedKeys();
				generated.next();
				bundleDbId = generated.getInt(1);
			} catch (SQLException e) {
				throw new IOException(e);
			}
		}
		return 0;
	}

	private int getBundleIdByName(String name) throws IOException {
		try (Connection conn = getConnection();
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_BUNDLE_ID_BY_NAME)) {
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
