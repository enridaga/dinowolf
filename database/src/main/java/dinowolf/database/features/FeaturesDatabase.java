package dinowolf.database.features;

import java.io.IOException;
import java.util.Map;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;

public interface FeaturesDatabase {
	public FeatureSet getFeatures() throws IOException;

	/**
	 * 
	 * @param bundleId
	 *            - the identifier of the workflow in the repository (not the
	 *            taverna wf URI!)
	 * @param featureSet
	 * @throws IOException
	 */
	public void put(String bundleId, FeaturesMap featureSet) throws IOException;

	public FeaturesMap getFeatures(String bundleId, WorkflowBundle container) throws IOException;
	
	public FeatureSet getFeatures(String bundleId, String portPair) throws IOException;
	
	public Map<String,FeatureSet> getFeatures(String bundleId) throws IOException;

	public FeatureSet getFeatures(Integer[] dbIds) throws IOException;
}
