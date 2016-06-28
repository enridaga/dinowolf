package dinowolf.features.cache;

import java.io.IOException;

import org.junit.Test;

public class HttpCallCachingTest {

	@Test
	public void test() throws IOException{
		HttpCallCaching cacher = new HttpCallCaching();
		cacher.get("http://dbpedia.org/resource/Caswell_County,_North_Carolina", "application/json");
		cacher.get("http://dbpedia.org/resource/Caswell_County,_North_Carolina", "application/json");
		cacher.get("http://dbpedia.org/resource/Caswell_County,_North_Carolina", "application/json");
	}
}
