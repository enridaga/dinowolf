package dinowolf.features;

import java.util.Map;
import java.util.Set;

public interface FeaturesMap extends Map<String,FeatureSet>{

	public Set<String> getPortPairs();

	public FeatureSet getFeatures(String portPair);

	public FeatureSet allFeatures();
}
