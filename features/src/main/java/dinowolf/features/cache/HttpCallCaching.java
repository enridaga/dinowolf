package dinowolf.features.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.engine.control.CompositeCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCallCaching {
	private CacheAccess<Object, Object> cache;
	private static final Logger l = LoggerFactory.getLogger(HttpCallCaching.class);

	public HttpCallCaching(String diskLocation) throws IOException {
		CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("HttpCallCaching.properties"));
		props.put("jcs.auxiliary.DC.attributes.DiskPath", diskLocation);
		ccm.configure(props);
	}

	public HttpCallCaching() throws IOException {
		CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream("HttpCallCaching.properties"));
		ccm.configure(props);
		cache = JCS.getInstance("default");
	}

	public InputStream get(String url) throws IOException {
		return get(url, "*/*");
	}
	
	public InputStream get(String url, String accept) throws IOException {
		l.trace("(http) {}", url);
		Object output = cache.get(url);
		String outputs;
		if (output == null) {
			l.trace("(not cached) {}", url);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("Accept", accept);
			int status = conn.getResponseCode();
			if (status != 200) {
				l.error("Response status: {}", status);
				throw new IOException("Response: " + status);
			}
			l.trace("(caching) {}", url);
			outputs = IOUtils.toString(conn.getInputStream());
			cache.put(url, outputs);
		} else {
			outputs = (String) output;
			l.trace("(cached) {} > {}", url, outputs.length());
		}
		return IOUtils.toInputStream(outputs);
	}
}
