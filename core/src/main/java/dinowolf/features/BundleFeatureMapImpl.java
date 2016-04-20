package dinowolf.features;

import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public class BundleFeatureMapImpl implements BundleFeaturesMap {

	private FeaturesMap featureSet;
	private WorkflowBundle wf;

	public BundleFeatureMapImpl(WorkflowBundle wf, FeaturesMap features) {
		this.wf = wf;
		this.featureSet = features;
	}

	@Override
	public Set<String> getPortPairs() {
		return featureSet.getPortPairs();
	}

	@Override
	public Set<Feature> getFeatures(String portPair) {
		return featureSet.getFeatures(portPair);
	}

	@Override
	public WorkflowBundle getWorkflowBundle() {
		return wf;
	}

}
