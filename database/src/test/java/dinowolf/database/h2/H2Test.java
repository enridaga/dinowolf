package dinowolf.database.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.features.Feature;
import dinowolf.features.FeatureImpl;
import dinowolf.features.FeatureLevel;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class H2Test {

	private static final Logger l = LoggerFactory.getLogger(H2Test.class);

	@Rule
	public TestName name = new TestName();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void before() throws IOException {
		l.debug(">>> {} <<<", name.getMethodName());
		connectionUrl = "jdbc:h2:file:" + testFolder.newFolder().getAbsolutePath() + "/H2Test";
		l.debug("Connection url: {}", connectionUrl);
	}

	private String connectionUrl;
	private String user = "user";
	private String pwd = "pwd234556";

	@Test
	public void test1_connection() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		boolean success = conn.createStatement().execute("CREATE TABLE TEST (ID INT PRIMARY KEY, NAME VARCHAR(255) ) ");
		Assert.assertFalse(success);
		success = conn.createStatement().execute("INSERT INTO TEST (ID,NAME) VALUES (0,'ENRICO')");
		Assert.assertFalse(success);
		java.sql.ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM TEST");
		Assert.assertTrue(rs.next());
		l.debug("Result {} {}", rs.getInt(1), rs.getString(2));
		rs.close();
		conn.close();
	}

	@Test
	public void test2_createTables() throws ClassNotFoundException, SQLException {

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		boolean success = conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR_FEATURE);
		Assert.assertFalse(success); // no result set

		// EXECUTING TWICE SHOULD NOT DO ANY HARM
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR_FEATURE);

		conn.close();
	}

	@Test
	public void test3_insertBundle() throws ClassNotFoundException, SQLException {
		test2_createTables();

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		PreparedStatement stm = conn.prepareStatement(H2Queries.INSERT_BUNDLE, Statement.RETURN_GENERATED_KEYS);
		stm.setString(1, "Filename");
		stm.execute();
		ResultSet generated = stm.getGeneratedKeys();
		generated.next();
		int dbId = generated.getInt(1);
		l.debug("Generated key: {}", dbId);
		stm.setString(1, "Filename2");
		stm.execute();
		generated = stm.getGeneratedKeys();
		generated.next();
		dbId = generated.getInt(1);
		l.debug("Generated key: {}", dbId);
		stm.setString(1, "Filename3");
		stm.execute();
		generated = stm.getGeneratedKeys();
		generated.next();
		dbId = generated.getInt(1);
		l.debug("Generated key: {}", dbId);

		// This second MUST throw an exception because of the unique key of the
		// filename
		Exception e = null;
		try {
			stm.setString(1, "Filename");
			stm.execute(); // this burns an id!
		} catch (Exception ex) {
			e = ex;
		}
		Assert.assertNotNull(e);
		conn.close();
	}

	@Test
	public void test4_inserts() throws ClassNotFoundException, SQLException {
		test3_insertBundle();

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		PreparedStatement stm = conn.prepareStatement(H2Queries.INSERT_BUNDLE, Statement.RETURN_GENERATED_KEYS);
		stm.setString(1, "AnotherFilename");
		stm.execute();
		ResultSet generated = stm.getGeneratedKeys();
		generated.next();
		int bundleId = generated.getInt(1);
		l.debug("Generated bundle db id: {}", bundleId);

		stm = conn.prepareStatement(H2Queries.INSERT_PORTPAIR, Statement.RETURN_GENERATED_KEYS);
		stm.setInt(1, bundleId);
		stm.setString(2, "Filename/Portpair1/form:bob/to:claire");
		stm.execute();
		generated = stm.getGeneratedKeys();
		generated.next();
		int portpairId = generated.getInt(1);
		l.debug("Generated portpair db id: {}", portpairId);

		// This second MUST throw an exception because of the unique key of the
		// portname
		Exception e = null;
		try {
			stm.setInt(1, bundleId);
			stm.setString(2, "Filename/Portpair1/form:bob/to:claire");
			stm.execute();
		} catch (Exception ex) {
			e = ex;
		}
		Assert.assertNotNull(e);

		// FEATURE
		stm = conn.prepareStatement(H2Queries.INSERT_FEATURE, Statement.RETURN_GENERATED_KEYS);
		l.info("hash: {}",H2Queries.hashCode("1234567890"));
		stm.setString(1, H2Queries.hashCode("1234567890"));
		stm.setString(2, "MyTestFeature");
		stm.setString(3, "a value");
		stm.setInt(4, H2Queries.toInt(FeatureLevel.Workflow));
		stm.setInt(5, 1);
		stm.execute();
		generated = stm.getGeneratedKeys();
		generated.next();
		int featureId = generated.getInt(1);
		l.debug("Generated feature db id: {}", featureId);

		// This second MUST throw an exception because of the unique key of the
		// feature hashcode
		e = null;
		try {
			stm.setString(1, H2Queries.hashCode("1234567890"));
			stm.setString(2, "MyTestFeature");
			stm.setString(3, "a value");
			stm.setInt(4, H2Queries.toInt(FeatureLevel.Workflow));
			stm.setInt(5, 1);
			stm.execute();
		} catch (Exception ex) {
			e = ex;
		}
		Assert.assertNotNull(e);

		stm = conn.prepareStatement(H2Queries.INSERT_FEATURE, Statement.RETURN_GENERATED_KEYS);
		stm.setInt(1, 1234567891);
		stm.setString(2, "MyTestFeature2");
		stm.setString(3, "a value");
		stm.setInt(4, H2Queries.toInt(FeatureLevel.Workflow));
		stm.setInt(5, 1);
		stm.execute();
		generated = stm.getGeneratedKeys();
		generated.next();
		featureId = generated.getInt(1);
		l.debug("Generated feature db id: {}", featureId);

		// PORTPAIR_FEATURE
		stm = conn.prepareStatement(H2Queries.INSERT_PORTPAIR_FEATURE);
		stm.setInt(1, portpairId);
		stm.setInt(2, featureId);
		stm.execute();

		// This second MUST throw an exception because of the unique key of the
		// portname
		e = null;
		try {
			stm.setInt(1, portpairId);
			stm.setInt(2, featureId);
			stm.execute();
		} catch (Exception ex) {
			e = ex;
		}
		Assert.assertNotNull(e);

		conn.close();
	}

	@Test
	public void test5_selectAllFeatures() throws ClassNotFoundException, SQLException {
		test4_inserts();

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);
		ResultSet rs = conn.createStatement().executeQuery(H2Queries.SELECT_ALL_FEATURES);
		while (rs.next()) {
			l.debug("{} {} {} {} {} {}", new Object[] { rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
					rs.getInt(5), rs.getBoolean(6) });
		}
	}

	@Test
	public void test6_featuresHashCheck() throws Exception {
		test2_createTables();

		List<Feature> fff = new ArrayList<Feature>();
		fff.add(new FeatureImpl("A1", "a val 1", FeatureLevel.Processor));
		fff.add(new FeatureImpl("A2", "a val 2", FeatureLevel.Workflow, true));
		fff.add(new FeatureImpl("A3", "a val 3", FeatureLevel.FromToPorts, false));

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		PreparedStatement stm = conn.prepareStatement(H2Queries.INSERT_FEATURE, Statement.RETURN_GENERATED_KEYS);
		Map<Integer, Feature> dbIdMap = new HashMap<Integer, Feature>();
		for (Feature f : fff) {
			stm.setInt(1, f.hashCode());
			stm.setString(2, f.getName());
			stm.setString(3, f.getValue());
			stm.setInt(4, H2Queries.toInt(f.getLevel()));
			stm.setBoolean(5, f.isTokenizable());
			stm.execute();
			ResultSet generated = stm.getGeneratedKeys();
			generated.next();
			dbIdMap.put(generated.getInt(1), f);
		}

		ResultSet rs = conn.createStatement().executeQuery(H2Queries.SELECT_ALL_FEATURES);
		while (rs.next()) {
			l.debug("{} {} {} {} {} {}", new Object[] { rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
					rs.getInt(5), rs.getBoolean(6) });
			Feature f = new FeatureImpl(rs.getString(3), rs.getString(4), H2Queries.toFeatureLevel(rs.getInt(5)),
					rs.getBoolean(6));
			l.debug("feature {} hashcode {}/{}", f, rs.getInt(2), f.hashCode());
			Assert.assertTrue(f.hashCode() == rs.getInt(2));
			// This feature MUST be in the initial set
			Assert.assertTrue(fff.contains(f));
			// This feature MUST be in the map with the given Id
			Assert.assertTrue(dbIdMap.get(rs.getInt(1)).equals(f));
		}
	}
	
}
