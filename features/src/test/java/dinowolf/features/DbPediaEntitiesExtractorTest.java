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

public class DbPediaEntitiesExtractorTest {
	private static final Logger l = LoggerFactory.getLogger(DbPediaEntitiesExtractorTest.class);

	private InputStream __f(String n) {
		return getClass().getClassLoader().getResourceAsStream(n);
	}

	@Test
	public void test() throws ReaderException, IOException {

		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wb = io.readBundle(
				__f("3291-Net_reproductive_rate__Ro_-v2.wfbundle"),
				null);
		FromToCollector coll = new FromToCollector();
		List<FromTo> ft = coll
				.getList("3291-Net_reproductive_rate__Ro_-v2.wfbundle", wb);
		for (FromTo i : ft) {
			FeatureSet fs = new StandardFeaturesExtractor().extract(i);
			for (Feature f : fs) {
				l.info(" > {} :: {}", i, f);
			}
			FeatureSet fs3 = new DbPediaEntitiesExtractor("http://spotlight.sztaki.hu:2222/rest/annotate").extract(i, fs);
			for (Feature f : fs3) {
				l.info(" < {} :: {}", i, f);
			}
		}
	}
}
