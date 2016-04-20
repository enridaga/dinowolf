package dinowolf.database.features;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

public class FeaturesDatabaseTest {
	private static final Logger l = LoggerFactory.getLogger(FeaturesDatabaseTest.class);

	@Rule
	public TestName name = new TestName();

	private static File directory;

	private FeaturesDatabase features;

	private WorkflowBundleIO io;
	private String bundleFile = "ADR-S-v2";

	@BeforeClass
	public static void beforeClass() throws IOException {
		directory = new File("./FeaturesDatabaseTest-data");
		if (directory.exists())
			FileUtils.deleteDirectory(directory);
		directory.mkdir();
		directory.deleteOnExit();
	}

	@Before
	public void before() {
		features = new FeaturesDatabaseTDB(directory);
		io = new WorkflowBundleIO();
		
	}

	@Test
	public void put() throws ReaderException, IOException {
		l.info("{}", name.getMethodName());
		WorkflowBundle bundle = io
				.readBundle(getClass().getClassLoader().getResourceAsStream("./" + bundleFile + ".wfbundle"), null);
		
		FeaturesMap map = FeaturesMapExtractor.extract(bundle);
		
		Assert.assertTrue(features.getFeatures().size() == 0);
		
		FeaturesMap fm = features.getFeatures(bundleFile);
		Assert.assertTrue(fm.size() == 0);
		
		l.debug("Putting features of workflow {} ({} features)", bundleFile, map.allFeatures().size());
		features.put(bundleFile, map);
		
		l.debug("Loading features of workflow {}", bundleFile);
		FeaturesMap mapGot = features.getFeatures(bundleFile);
		Assert.assertTrue(map.size() == mapGot.size());
		
//		
//		WorkflowBundle bundle2 = repository.get(bundleFile);
//		l.debug("Loaded workflow name {}", bundle2.getName());
//		l.debug("Loaded workflow id {}", bundle2.getIdentifier());
//		Assert.assertTrue(bundle2.getIdentifier().equals(bundle.getIdentifier()));
	}
}
