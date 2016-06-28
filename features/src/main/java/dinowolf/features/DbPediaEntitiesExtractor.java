package dinowolf.features;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dinowolf.annotation.FromTo;

public class DbPediaEntitiesExtractor implements FeaturesExtractor {

	private static final String SPOTLIGHT_SERVICE_SYSTEM_PROPERTY = "dinowolf.spotlight";
	private static final String DBPEDIA_ENDPOINT_SYSTEM_PROPERTY = "dinowolf.dbpedia";
	private static final Logger l = LoggerFactory.getLogger(DbPediaEntitiesExtractor.class);
	private String spotlightServiceUrl = null;
	private String dbpediaEndpointUrl = null;

	public DbPediaEntitiesExtractor() {
		if (System.getProperty(SPOTLIGHT_SERVICE_SYSTEM_PROPERTY) != null) {
			spotlightServiceUrl = System.getProperty(SPOTLIGHT_SERVICE_SYSTEM_PROPERTY);
		} else {
			l.error("Spotlight url missing. Use system property: " + SPOTLIGHT_SERVICE_SYSTEM_PROPERTY);
		}
		if (System.getProperty(DBPEDIA_ENDPOINT_SYSTEM_PROPERTY) != null) {
			dbpediaEndpointUrl = System.getProperty(DBPEDIA_ENDPOINT_SYSTEM_PROPERTY);
		} else {
			l.debug("DbPedia url missing. Using public endpoint.");
			dbpediaEndpointUrl = "http://dbpedia.org/sparql";
		}
	}

	public DbPediaEntitiesExtractor(String serviceUrl) {
		spotlightServiceUrl = serviceUrl;
		l.debug("Using public DBPedia endpoint.");
		dbpediaEndpointUrl = "http://dbpedia.org/sparql";
	}
	
	public DbPediaEntitiesExtractor(String serviceUrl, String sparqlEndpoint) {
		spotlightServiceUrl = serviceUrl;
		dbpediaEndpointUrl = sparqlEndpoint;
		l.debug("{} {}", spotlightServiceUrl, dbpediaEndpointUrl);
	}

	@Override
	public FeatureSet extract(FromTo inOut, FeatureSet features) throws IOException {
		if (spotlightServiceUrl == null) {
			l.warn("Spotlight url missing. Skipping.");
			return extract(inOut);
		}

		// Concat all text to send
		StringBuilder sb = new StringBuilder();
		for (Feature f : features) {
			if (f.isTokenizable() && (f.getName().contains("Title") || f.getName().contains("Description"))) {
				sb.append(f.getValue()).append(" ");
			}
		}
		String input = sb.toString();

		FeatureHashSet set = new FeatureHashSet();
		List<String> entities = spotlight(input);
		for (String u : entities) {
			set.add(new FeatureImpl("DbPediaEntity", u, FeatureDepth.FromToPorts));
		}
		for (String entity : entities) {
			for (Entry<String, String> u : about(entity).entrySet()) {
				String related = u.getKey();
				String type = u.getValue();
				if(type.equals("C")){
					type = "DbPediaCategory";
				}else{
					type = "DbPediaType";
				}
				set.add(new FeatureImpl(type, related, FeatureDepth.FromToPorts));
			}
		}

		return set;
	}

	@Override
	public FeatureSet extract(FromTo inOut) throws IOException {
		return new FeatureHashSet();
	}

	List<String> spotlight(String input) throws MalformedURLException, IOException {
		String qs = URLEncoder.encode(input, "UTF-8");
		String url = new StringBuilder().append(spotlightServiceUrl).append("?text=").append(qs).append("&confidence=")
				.append("0.3").toString();
		HttpURLConnection hc = (HttpURLConnection) new URL(url).openConnection();
		hc.setDoOutput(true);
		hc.setRequestProperty("Accept", "application/json");
		int status = hc.getResponseCode();
		List<String> uris = new ArrayList<String>();
		if (status == 200) {
			InputStream is = hc.getInputStream();
			JsonObject o = (JsonObject) new JsonParser().parse(new InputStreamReader(is));
			JsonArray a = o.getAsJsonArray("Resources");
			if (a != null) {
				Iterator<JsonElement> it = a.iterator();
				while (it.hasNext()) {
					JsonObject k = (JsonObject) it.next();
					uris.add(k.get("@URI").getAsString());
				}
			}
		}
		return uris;
	}

	Map<String, String> about(String entity) throws IOException {
		String q = new StringBuilder("PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX dct: <http://purl.org/dc/terms/>\n"
				+ "\n" + "select distinct ?Concept ?Type where {\n" + "{<").append(entity)
						.append("> dct:subject* ?Concept . bind (\"C\" as ?Type) }\n" + "UNION \n" + "{<").append(entity)
						.append("> rdf:type* ?Concept . bind (\"T\" as ?Type) }\n" + "}").toString();
		String qs = URLEncoder.encode(q, "UTF-8");
		String url = new StringBuilder().append(dbpediaEndpointUrl).append("?query=").append(qs).append("&format=json").toString();
		HttpURLConnection hc = (HttpURLConnection) new URL(url).openConnection();
		hc.setDoOutput(true);
		hc.setRequestProperty("Accept", "application/sparql-results+json");
		int status = hc.getResponseCode();
		Map<String,String> uris = new HashMap<String,String>();
		//l.debug("DBPedia response: {}", status);
		if (status == 200) {
			InputStream is = hc.getInputStream();
			JsonObject o = (JsonObject) new JsonParser().parse(new InputStreamReader(is));
			JsonArray a = o.getAsJsonObject("results").getAsJsonArray("bindings");
			
			if (a != null) {
				Iterator<JsonElement> it = a.iterator();
				while (it.hasNext()) {
					JsonObject k = (JsonObject) it.next();
					JsonObject cc = (JsonObject) k.get("Concept");
					JsonObject vv = (JsonObject) k.get("Type");
					uris.put(cc.get("value").getAsString(),vv.get("value").getAsString());
				}
			}
		}
		return uris;
	}
}
