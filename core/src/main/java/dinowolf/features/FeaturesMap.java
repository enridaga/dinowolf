package dinowolf.features;

import java.util.Map;
import java.util.Set;

import dinowolf.annotation.FromTo;

public interface FeaturesMap extends Map<FromTo,FeatureSet>{

	public Set<FromTo> getPortPairs();

	public FeatureSet getFeatures(FromTo portPair);

	public FeatureSet allFeatures();
}
