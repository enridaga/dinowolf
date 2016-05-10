package dinowolf.database.tdb;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import dinowolf.database.schema.Vocabulary;
import dinowolf.features.Feature;

public class QueryHelper {

	public final static Resource toResource(Feature feature) {
		return ResourceFactory.createResource(Vocabulary.FEATURE + feature.getId());
	}

	public final static Resource toBundleResource(String bundleId) {
		return ResourceFactory.createResource(toBundleUri(bundleId));
	}
	
	public static String toPortPairUri(String portPair) {
		return Vocabulary.PORTPAIR + new HashCodeBuilder().append(portPair).toHashCode();
	}

	public static String quoteUri(String uri) {
		return new StringBuilder().append("<").append(uri).append(">").toString();
	}

	public final static String quotedUri(Feature feature) {
		return quoteUri(Vocabulary.FEATURE + feature.getId());
	}

	public final static String quotedString(String v) {
		return new StringBuilder().append("\"\"\"").append(v.replace("\"", "\\\"")).append("\"\"\"").toString();
	}
	
	public static String toBundleUri(String bundleId){
		return Vocabulary.BUNDLE + bundleId;
	}
}
