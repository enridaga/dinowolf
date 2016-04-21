package dinowolf.database.features;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.h2.FeaturesDatabaseH2;
import dinowolf.database.h2.H2Queries;
import dinowolf.database.h2.H2Test;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

public class FeaturesDatabaseH2Test {
	private static final Logger l = LoggerFactory.getLogger(H2Test.class);

	@Rule
	public TestName name = new TestName();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void before() throws IOException, SQLException {
		l.debug(">>> {} <<<", name.getMethodName());
		connectionUrl = "jdbc:h2:file:" + testFolder.newFolder().getAbsolutePath() + "/H2Test";
		l.debug("Connection url: {}", connectionUrl);
		
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		boolean success = conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
		success = conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR_FEATURE);
	}

	private String connectionUrl;
	private String user = "user";
	private String pwd = "pwd234556";

	
	@Test
	public void test() throws IOException, ReaderException {
		FeaturesDatabaseH2 h2 = new FeaturesDatabaseH2(testFolder.newFolder("test"), "H2Test", user, pwd);
		Assert.assertTrue(h2.getFeatures().size() == 0);
		
		String bundleFile = "ADR-S-v2";
		
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle bundle = io
				.readBundle(getClass().getClassLoader().getResourceAsStream("./" + bundleFile + ".wfbundle"), null);
		
		FeaturesMap map = FeaturesMapExtractor.extract(bundle);
		h2.put(bundleFile, map);
		
		FeaturesMap read = h2.getFeatures(bundleFile);
		
		for(Entry<String,FeatureSet> entry : read.entrySet()){
			String port = entry.getKey();
			Assert.assertTrue(map.containsKey(port));
			
			FeatureSet fs1 = map.get(port);
			FeatureSet fs2 = read.get(port);
			Assert.assertTrue(fs2.containsAll(fs1));
			Assert.assertTrue(fs1.containsAll(fs2));
		}
		
	}
}
