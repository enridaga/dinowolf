package dinowolf.features;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;

public class FeatureSetExtractor {
	private static final Logger l = LoggerFactory.getLogger(FeatureSetExtractor.class);

	private static final FeaturesExtractor E = new FeaturesExtractor();
	private static final FromToCollector C = new FromToCollector();

	public final static BundleFeaturesMap generate(final WorkflowBundle bundle) {
		return new BundleFeatureMapImpl(bundle, extract(bundle));
	}

	public final static FeaturesHashMap extract(WorkflowBundle bundle) {
		FeaturesHashMap featuresMap = new FeaturesHashMap();
		for (FromTo io : C.collect(bundle)) {
			FeatureHashSet fff = E.extract(io);
			featuresMap.put(io.getId(), fff);
			l.trace("{}: {} features extracted", io.getId(), fff.size());
		}
		
		return featuresMap;
	}
}
