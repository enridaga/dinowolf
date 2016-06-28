package dinowolf.features;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneUtilsTest {

	private static final Logger L = LoggerFactory.getLogger(LuceneUtilsTest.class);

	@Test
	public void etst() throws IOException {
		Assert.assertTrue(LuceneUtils.parseKeywords("").isEmpty());
		
		for(String t:LuceneUtils.parseKeywords("There are a lot of examples that show how to use the StandardTokenizer")){
			L.debug(">{}<",t);
		}
	}
}
