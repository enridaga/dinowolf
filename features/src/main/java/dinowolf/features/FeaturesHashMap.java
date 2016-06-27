package dinowolf.features;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import dinowolf.annotation.FromTo;

public class FeaturesHashMap extends HashMap<FromTo, FeatureSet> implements FeaturesMap {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * read only
	 */
	public Set<FromTo> getPortPairs() {
		return Collections.unmodifiableSet(keySet());
	}

	/**
	 * Modifiable
	 */
	public FeatureSet getFeatures(FromTo portPair) {
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
