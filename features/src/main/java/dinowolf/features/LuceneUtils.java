package dinowolf.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LuceneUtils {
	private static final Logger l = LoggerFactory.getLogger(LuceneUtils.class);
    public static List<String> parseKeywords(String text) throws IOException {
    	try(Analyzer tokenizer = new BagOfWordsAnalyzer()){
	    	TokenStream ts = tokenizer.tokenStream("", text);
	    	List<String> tokens = new ArrayList<String>();
	    	ts.addAttribute(CharTermAttribute.class);
	    	ts.reset();
	    	while(ts.incrementToken()) {
	    	    String term = ts.getAttribute(CharTermAttribute.class).toString();
	    	    l.trace(" - {}", term);
	    	    tokens.add(term);
	    	}
	    	return tokens;
    	}
    }  
}