package dinowolf.database.features;

import java.io.IOException;

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

	public FeaturesMap getFeatures(String bundleId) throws IOException;
}
