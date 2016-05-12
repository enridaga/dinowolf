package dinowolf.features;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;

public class FeaturesExtractorTest {
	private static final Logger l = LoggerFactory.getLogger(FeaturesExtractorTest.class);
	private static final FeaturesExtractor E = new FeaturesExtractor();
	
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
		List<FromTo> ft = coll.collect(wb);
		for (FromTo i : ft) {
			l.info(" > {}", i);
			l.info("{}",E.extract(i));
		}
	}
}
