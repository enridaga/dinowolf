package dinowolf.features;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class FeaturesHashMap extends HashMap<String, FeatureSet> implements FeaturesMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Set<String> getPortPairs() {
		return Collections.unmodifiableSet(keySet());
	}

	public Set<Feature> getFeatures(String portPair) {
		return Collections.unmodifiableSet(get(portPair));
	}

}
