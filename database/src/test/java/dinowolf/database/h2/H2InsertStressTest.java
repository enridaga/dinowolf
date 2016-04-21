package dinowolf.database.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.features.Feature;
import dinowolf.features.FeatureImpl;
import dinowolf.features.FeatureLevel;

public class H2InsertStressTest {

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
	public void test1_mass_insert() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		boolean success = conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
		Assert.assertFalse(success);

		PreparedStatement stm = conn.prepareStatement(H2Queries.INSERT_FEATURE, Statement.RETURN_GENERATED_KEYS);
		int max = 100000; //100000000;
		int x = 0;
		try {
			for (x = 0; x < max; x++) {
				Feature f = new FeatureImpl("F" + x + Math.random(), "Value value value " + x, FeatureLevel.Workflow, true);
				stm.setInt(1, f.hashCode());
				stm.setString(2, f.getName());
				stm.setString(3, f.getValue());
				stm.setInt(4, H2Queries.toInt(f.getLevel()));
				stm.setBoolean(5, f.isTokenizable());
				stm.execute();
			}
		} catch (Exception e) {
			l.error("INSERT FAILED AT " + x, e);
		}
		conn.close();

		// New connection
		conn = DriverManager.getConnection(connectionUrl, user, pwd);

		ResultSet rs = conn.createStatement().executeQuery(H2Queries.SELECT_ALL_FEATURES);
		int c = 0;
		while (rs.next()) {
			c++;
		}
		l.debug("returned {} results", c);
		Assert.assertTrue(c == max);
	}

}
