package dinowolf.features;

import java.util.Set;

public interface FeaturesMap {

	public Set<String> getPortPairs();

	public Set<Feature> getFeatures(String portPair);

}
