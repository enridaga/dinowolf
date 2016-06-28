package dinowolf.features;

import java.io.IOException;

import dinowolf.annotation.FromTo;

public interface FeaturesExtractor {

	FeatureSet extract(FromTo inOut) throws IOException;

	FeatureSet extract(FromTo inOut, FeatureSet features) throws IOException;

}