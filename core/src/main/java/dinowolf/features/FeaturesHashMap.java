package dinowolf.features;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class FeaturesHashMap extends HashMap<String, FeatureSet> implements FeaturesMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * read only
	 */
	public Set<String> getPortPairs() {
		return Collections.unmodifiableSet(keySet());
	}

	/**
	 * Modifiable
	 */
	public FeatureSet getFeatures(String portPair) {
		return get(portPair);
	}

	@Override
	public FeatureSet allFeatures() {
		FeatureSet set = new FeatureHashSet();
		for(FeatureSet s : values()){
			set.addAll(s);
		}
		return set;
	}
}
