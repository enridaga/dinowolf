package dinowolf.database;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromTo.FromToType;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

public class DatabaseManagerTest {

	public static final Logger l = LoggerFactory.getLogger(DatabaseManagerTest.class);

	private DatabaseManagerImpl manager;

	private static String[] testBundles = new String[] { "3Drec-v1", "A_Comparison_of_China_and_US_populations-v2",
			"A_graphical_plot_of_White_Ash__Beech__and_Hemlock_populations_in_Onondaga_County__NY-v1", "ADR-S-v2",
			"Get_similar_phenotypes_for_a_disease_and_a_gene-v1" };

	@Rule
	public TestName name = new TestName();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void before() throws IOException, ReaderException {
		l.debug("Preparing {}", name.getMethodName());
		manager = new DatabaseManagerImpl(
				new File(testFolder.newFolder().getAbsolutePath() + "/" + name.getMethodName()));
		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String f : testBundles) {
			WorkflowBundle b = io.readBundle(_f(f), null);
			manager.put(b, f, true);
			WorkflowBundle wb = manager.get(f);
			FeaturesMap map = FeaturesMapExtractor.extract(f, wb, FromToType.IO);
			l.debug(" (prepare) {} [{}]", new Object[] { f, map.size() });
			if (l.isDebugEnabled()) {
				for (Entry<FromTo, FeatureSet> pp : map.entrySet()) {
					l.debug("{} [{}]", pp.getKey(), pp.getValue());
				}
			}
			manager.put(f, map);
		}
	}

	@Test
	public void recommendationRules() throws IOException {
		// for(FromTo)
		// all IO ports except 3Drec-v1 are dn:hasDerivation
		String annotation = "hasDerivation";

		for (String bundle : testBundles) {
			if (bundle.equals("3Drec-v1"))
				continue;

			String bundleId = bundle;
			WorkflowBundle b = manager.get(bundleId);
			FeaturesMap map = manager.getFeatures(bundleId, b);
			for (Entry<FromTo, FeatureSet> pair : map.entrySet()) {
				manager.annotate(bundleId, pair.getKey().getId(), Arrays.asList(annotation), Collections.emptyList(), 1);
			}
		}

		WorkflowBundle b = manager.get("3Drec-v1");
		FeaturesMap map = manager.getFeatures("3Drec-v1", b);
		for (Entry<FromTo, FeatureSet> pair : map.entrySet()) {
			l.info("{} {} rules", pair.getKey().getId(), manager.recommend("3Drec-v1", pair.getKey().getId()).size());
		}
	}

	private static final InputStream _f(String name) {
		return DatabaseManagerTest.class.getClassLoader().getResourceAsStream(name + ".wfbundle");
	}
}
