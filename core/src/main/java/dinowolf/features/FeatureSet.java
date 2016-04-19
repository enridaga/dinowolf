package dinowolf.features;

import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public interface FeatureSet {

	public Set<String> getPortPairs();

	public Set<Feature> getFeatures(String portPair);

}
