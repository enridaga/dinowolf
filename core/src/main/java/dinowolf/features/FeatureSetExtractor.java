package dinowolf.features;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;

public class FeatureSetExtractor {
	private static final Logger l = LoggerFactory.getLogger(FeatureSetExtractor.class);

	private static final FeaturesExtractor E = new FeaturesExtractor();
	private static final FromToCollector C = new FromToCollector();

	public final static FeatureSet generate(final WorkflowBundle bundle) {
		return new FeatureSetMap(bundle, extract(bundle));
	}

	public final static Map<String, Set<Feature>> extract(WorkflowBundle bundle) {
		Map<String, Set<Feature>> featuresMap = new HashMap<String, Set<Feature>>();
		for (FromTo io : C.collect(bundle)) {
			Set<Feature> fff = E.extract(io);
			featuresMap.put(io.getId(), fff);
			l.trace("{}: {} features extracted", io.getId(), fff.size());
		}
		
		return featuresMap;
	}

	private static class FeatureSetMap implements FeatureSet {
		private WorkflowBundle bundle;

		private FeatureSetMap(WorkflowBundle bundle, Map<String, Set<Feature>> featuresMap) {
			this.bundle = bundle;
			this.featuresMap = featuresMap;
		}

		private Map<String, Set<Feature>> featuresMap;

		public Set<String> getPortPairs() {
			return Collections.unmodifiableSet(featuresMap.keySet());
		}

		public Set<Feature> getFeatures(String portPair) {
			return Collections.unmodifiableSet(featuresMap.get(portPair));
		}

		public WorkflowBundle getWorkflowBundle() {
			return bundle;
		}
	}
}
