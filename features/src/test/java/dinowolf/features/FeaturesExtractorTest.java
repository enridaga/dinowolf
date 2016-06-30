package dinowolf.features;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.jena.atlas.logging.Log;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;

public class FeaturesExtractorTest {
	private static final Logger l = LoggerFactory.getLogger(FeaturesExtractorTest.class);
	private static final FeaturesExtractor E = new StandardFeaturesExtractor();

	private InputStream __f(String n) {
		return getClass().getClassLoader().getResourceAsStream(n);
	}

	@Test
	public void test() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wb = io.readBundle(
				__f("A_graphical_plot_of_White_Ash__Beech__and_Hemlock_populations_in_Onondaga_County__NY-v1.wfbundle"),
				null);
		FromToCollector coll = new FromToCollector();
		List<FromTo> ft = coll
				.getList("A_graphical_plot_of_White_Ash__Beech__and_Hemlock_populations_in_Onondaga_County__NY-v1", wb);
		Assert.assertTrue(!ft.isEmpty());

		for (FromTo i : ft) {
			int fs = E.extract(i).size();
			Assert.assertTrue(fs > 0);
			l.debug("{} {} features", i, fs);

		}
	}

	@Test
	public void features() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wb = io.readBundle(__f("3291-Net_reproductive_rate__Ro_-v2.wfbundle"), null);
		FeaturesHashMap map = FeaturesMapExtractor.extract("3291-Net_reproductive_rate__Ro_-v2.wfbundle", wb,
				FromTo.FromToType.IO);
		
		Assert.assertTrue(map.size() == 8);
		for (Feature f : map.allFeatures()) {
			l.debug("{}", f);
		}
		l.debug("{} total features", map.allFeatures().size());
		Assert.assertTrue(map.allFeatures().size()==261);
	}

	@Test
	public void containsWorkflowTitle() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wb = io.readBundle(__f("4474-xcorrSound_sound-match_2.0.2-v1.wfbundle"), null);
		FeaturesHashMap map = FeaturesMapExtractor.extract("4474-xcorrSound_sound-match_2.0.2-v1.wfbundle", wb,
				FromTo.FromToType.IO);

		Assert.assertTrue(!map.isEmpty());
		// Features include Title and Description
		boolean includesTitle = false;
		boolean includesDescription = false;
		for (Feature f : map.allFeatures()) {
			l.debug("{}", f);
			if (f.getName().equals("Title"))
				includesTitle = true;
			if (f.getName().equals("Description"))
				includesDescription = true;
		}
		Assert.assertTrue(includesTitle);
		Assert.assertTrue(includesDescription);
	}
}
