package dinowolf.features;

import java.util.Arrays;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;

public class FeaturesMapExtractor {
	private static final Logger l = LoggerFactory.getLogger(FeaturesMapExtractor.class);

	private static final FeaturesExtractor E = new FeaturesExtractor();
	private static final FromToCollector C = new FromToCollector();

	public final static BundleFeaturesMap generate(final WorkflowBundle bundle, FromTo.FromToType... types) {
		return new BundleFeatureMapImpl(bundle, extract(bundle, types));
	}

	public final static FeaturesHashMap extract(WorkflowBundle bundle, FromTo.FromToType... types) {
		FeaturesHashMap featuresMap = new FeaturesHashMap();
		for (FromTo io : C.getList(bundle)) {
			if (types.length > 0) {
				if(!Arrays.asList(types).contains(io.getType())){
					continue;
				}
			}
			FeatureHashSet fff = E.extract(io);
			featuresMap.put(io, fff);
			l.trace("{}: {} features extracted", io.getId(), fff.size());
		}

		return featuresMap;
	}
}
