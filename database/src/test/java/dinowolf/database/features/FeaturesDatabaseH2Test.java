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

import dinowolf.annotation.FromTo;
import dinowolf.database.h2.FeaturesDatabaseH2;
import dinowolf.database.h2.H2Queries;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

public class FeaturesDatabaseH2Test {
	private static final Logger l = LoggerFactory.getLogger(FeaturesDatabaseH2Test.class);

	@Rule
	public TestName name = new TestName();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void before() throws IOException, SQLException {
		l.debug("{}", name.getMethodName());
		connectionUrl = "jdbc:h2:file:" + testFolder.newFolder().getAbsolutePath() + "/H2Test";
		l.debug("Connection url: {}", connectionUrl);
		
		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);

		conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
		conn.createStatement().execute(H2Queries.CREATE_TABLE_FEATURE);
		conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
		conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR_FEATURE);
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
		
		FeaturesMap map = FeaturesMapExtractor.extract(bundleFile, bundle);
		h2.put(bundleFile, map);
		// Put twice, should just update
		
		FeaturesMap read = h2.getFeatures(bundleFile, bundle);
		
		for(Entry<FromTo,FeatureSet> entry : read.entrySet()){
			FromTo port = entry.getKey();
			Assert.assertTrue(map.containsKey(port));
			FeatureSet fs1 = map.get(port);
			FeatureSet fs2 = read.get(port);
			Assert.assertTrue(fs2.containsAll(fs1));
			Assert.assertTrue(fs1.containsAll(fs2));
		}
	}
	
	@Test
	public void update() throws ReaderException, IOException{
		FeaturesDatabaseH2 h2 = new FeaturesDatabaseH2(testFolder.newFolder("test"), "H2Test", user, pwd);
		Assert.assertTrue(h2.getFeatures().size() == 0);
		
		String bundleFile = "ADR-S-v2";
		
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle bundle = io
				.readBundle(getClass().getClassLoader().getResourceAsStream("./" + bundleFile + ".wfbundle"), null);
		
		FeaturesMap map = FeaturesMapExtractor.extract(bundleFile, bundle);
		h2.put(bundleFile, map);
		Exception e = null;
		try {
			h2.put(bundleFile, map);
		} catch (Exception e2) {
			e = e2;
		}
		
		Assert.assertNull("A new put should override the data and not raise any exception", e);
	}
}
