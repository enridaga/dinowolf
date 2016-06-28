package dinowolf.features;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;

public class BagOfWordsExtractor implements FeaturesExtractor {
	private static final Logger l = LoggerFactory.getLogger(BagOfWordsExtractor.class);
	@Override
	public FeatureSet extract(FromTo inOut, FeatureSet features) throws IOException {
		FeatureSet set = new FeatureHashSet();
		for (Feature f : features) {
			l.trace("{}", f);
			if (f.isTokenizable()) {
				l.trace("(tokenizable)");
				List<String> words = LuceneUtils.parseKeywords(f.getValue());
				for (String word : words) {
					l.trace("(word: {})", word);
					set.add(new DerivedFeature(f, "word", word));
				}
			}
		}
		return set;
	}

	@Override
	public FeatureSet extract(FromTo inOut) throws IOException {
		return extract(inOut, new FeatureHashSet());
	}
}
