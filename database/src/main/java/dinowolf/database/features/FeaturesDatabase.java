package dinowolf.database.features;

import dinowolf.features.FeatureSet;

public interface FeaturesDatabase {
	public FeatureSet getFeatures();
	public void put(FeatureSet featureSet);
	public FeatureSet getFeatures(String bundleUri);
}
