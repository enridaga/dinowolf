package dinowolf.features;

import dinowolf.annotation.FromTo;

public interface FeaturesExtractor {

	FeatureHashSet extract(FromTo inOut);

}