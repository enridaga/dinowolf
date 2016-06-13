package dinowolf.database.h2;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;
import dinowolf.database.features.FeaturesDatabase;
import dinowolf.features.Feature;
import dinowolf.features.FeatureDepth;
import dinowolf.features.FeatureHashSet;
import dinowolf.features.FeatureImpl;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesHashMap;
import dinowolf.features.FeaturesMap;

public class FeaturesDatabaseH2 extends H2Connected implements FeaturesDatabase {
	static private final Logger l = LoggerFactory.getLogger(FeaturesDatabaseH2.class);
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			l.error("Error loading JDBC driver", e);
		}
	}

	public FeaturesDatabaseH2(File location, String user, String password) {
		super(location, user, password);
	}

	public FeaturesDatabaseH2(File location, String database, String user, String password) {
		super(location, database, user, password);
	}

	@Override
	protected void setup() {
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR_FEATURE);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public FeaturesDatabaseH2(File location) {
		super(location);
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
			l.error("SQL Exception", e.getMessage());
			throw new IOException(e);
		}
	}

	@Override
	public FeaturesMap getFeatures(String bundleId, WorkflowBundle container) throws IOException {
		int dbId = getBundleIdByName(bundleId);
		FromToCollector co = new FromToCollector();
		Map<String, FromTo> ft = co.getMap(bundleId, container);
		try (Connection conn = getConnection();
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_FEATURES_OF_BUNDLE)) {
			st.setInt(1, dbId);
			ResultSet rs = st.executeQuery();
			FeaturesMap map = new FeaturesHashMap();
			while (rs.next()) {
				String portpairId = rs.getString(1);
				FromTo key = ft.get(portpairId);
				if (key == null) {
					throw new IOException("Cannot find port pair in bundle! Id was " + portpairId);
				}
				if (!map.containsKey(key)) {
					map.put(key, new FeatureHashSet());
				}
				map.get(key).add(new FeatureH2(rs.getInt(7),rs.getString(3), rs.getString(4),
						H2Queries.toFeatureLevel(rs.getInt(5)), rs.getBoolean(6)));
			}
			return map;
		} catch (SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new IOException(e);
		}

	}

	@Override
	public void put(String bundle, FeaturesMap featureMap) throws IOException {
		try (Connection conn = getConnection()) {
			try {
				conn.setAutoCommit(false);
				try (PreparedStatement deleteBundle = conn.prepareStatement(H2Queries.DELETE_BUNDLE)) {
					deleteBundle.setString(1, bundle);
					int rows = deleteBundle.executeUpdate();
					l.trace("{} rows updated", rows);
				}
				int bundleDbId = insertBundle(bundle, conn);
				l.debug("inserted bundle db id: {}", bundleDbId);
				ResultSet generated;
				try (PreparedStatement insertPortPair = conn.prepareStatement(H2Queries.INSERT_PORTPAIR,
						Statement.RETURN_GENERATED_KEYS);
						PreparedStatement featureExists = conn.prepareStatement(H2Queries.SELECT_FEATURE_EXISTS);
						PreparedStatement insertFeature = conn.prepareStatement(H2Queries.INSERT_FEATURE,
								Statement.RETURN_GENERATED_KEYS);
						PreparedStatement insertPortPairFeature = conn
								.prepareStatement(H2Queries.INSERT_PORTPAIR_FEATURE, Statement.RETURN_GENERATED_KEYS)) {
					for (Entry<FromTo, FeatureSet> entry : featureMap.entrySet()) {
						// Portpair
						insertPortPair.setInt(1, bundleDbId);
						l.trace("port pair: {}", entry.getKey().getId());
						insertPortPair.setString(2, entry.getKey().getId());
						insertPortPair.execute();
						generated = insertPortPair.getGeneratedKeys();
						generated.next();
						int portPairId = generated.getInt(1);
						for (Feature f : entry.getValue()) {
							// Feature
							int featureId = 0;
							featureExists.setInt(1, f.hashCode());
							ResultSet r = featureExists.executeQuery();
							if (r.next()) {
								// Get it
							} else {
								insertFeature.setInt(1, f.hashCode());
								insertFeature.setString(2, f.getName());
								insertFeature.setCharacterStream(3, new StringReader(f.getValue()));
								insertFeature.setInt(4, H2Queries.toInt(f.getLevel()));
								insertFeature.setBoolean(5, f.isTokenizable());
								insertFeature.execute();
								r = insertFeature.getGeneratedKeys();
								r.next();
							}
							featureId = r.getInt(1);
							// Insert port pair
							insertPortPairFeature.setInt(1, portPairId);
							insertPortPairFeature.setInt(2, featureId);
							insertPortPairFeature.execute();
						}
					}
				}
				conn.commit();
			} catch (Exception e) {
				l.error("Execution failed", e);
				conn.rollback();
				throw new IOException(e);
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e1) {
			l.error("SQL Exception", e1.getMessage());
			throw new IOException(e1);
		}
	}

	private int insertBundle(String bundle, Connection conn) throws IOException {
		l.trace("insertBundle {} ", bundle);
		int bundleDbId = getBundleIdByName(bundle, conn);
		if (bundleDbId == 0) {
			l.trace("insertBundle - bundle does not exists, go insert {} ", bundle);
			try (PreparedStatement stm = conn.prepareStatement(H2Queries.INSERT_BUNDLE,
					Statement.RETURN_GENERATED_KEYS)) {
				stm.setString(1, bundle);
				stm.execute();
				ResultSet generated = stm.getGeneratedKeys();
				if(!generated.next()){
					throw new IOException("Generated key is missing");
				}
				bundleDbId = generated.getInt(1); // FIXME
			} catch (SQLException e) {
				l.error("SQL Exception", e.getMessage());
				throw new IOException(e);
			}
		}
		l.trace("insertBundle - generated db id {} ", bundleDbId);
		return bundleDbId;
	}

	private int getBundleIdByName(String name, Connection conn) throws IOException {
		try (PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_BUNDLE_ID_BY_NAME)) {
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new IOException(e);
		}
	}
	
	public int getBundleIdByName(String name) throws IOException {
		try (Connection conn = getConnection()){
				return getBundleIdByName(name, conn);
		} catch (SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new IOException(e);
		}
	}

	@Override
	public FeatureSet getFeatures(String bundleId, String portPair) throws IOException {
		l.trace("{} {}", bundleId, portPair);
		int dbId = getBundleIdByName(bundleId);
		try (Connection conn = getConnection();
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_FEATURES_OF_PORTPAIR)) {
			st.setInt(1, dbId);
			st.setString(2, portPair);
			ResultSet rs = st.executeQuery();
			FeatureSet set = new FeatureHashSet();
			while (rs.next()) {
				set.add(new FeatureH2(rs.getInt(7), rs.getString(3), rs.getString(4), H2Queries.toFeatureLevel(rs.getInt(5)),
						rs.getBoolean(6)));
			}
			l.trace("features: {}", set);
			return set;
		} catch (SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new IOException(e);
		}
	}

	public static class FeatureH2 extends FeatureImpl {
		private int dbId;
		public FeatureH2(int dbId, String string, String string2, FeatureDepth featureLevel, boolean boolean1) {
			super(string, string2, featureLevel, boolean1);
			this.dbId = dbId;
		}

		public int getDbId(){
			return dbId;
		}
	}
}
