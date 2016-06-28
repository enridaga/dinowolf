package dinowolf.features;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;

public class BagOfWordsAnalyzer extends StopwordAnalyzerBase {
	public static final CharArraySet STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

	public BagOfWordsAnalyzer() {
		super(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	}

	@Override
	protected TokenStreamComponents createComponents(final String fieldName) {
		final Tokenizer src;
		StandardTokenizer t = new StandardTokenizer();
		src = t;
		TokenStream tok = new WordDelimiterFilter(src,
				WordDelimiterFilter.ALPHA | WordDelimiterFilter.GENERATE_WORD_PARTS
						| WordDelimiterFilter.SPLIT_ON_CASE_CHANGE | WordDelimiterFilter.STEM_ENGLISH_POSSESSIVE,
				null);
		tok = new LowerCaseFilter(tok);
		tok = new StopFilter(tok, stopwords);
		return new TokenStreamComponents(src, tok);
	}
}
