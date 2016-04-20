package dinowolf.database.schema;

public class SparqlQueries {
	public final static String PREFIXES = "" + "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" + "PREFIX dcat: <http://www.w3.org/ns/dcat#>\n"
			+ "PREFIX qb:   <http://purl.org/linked-data/cube#>\n" + "PREFIX dwt: <" + Vocabulary.TYPE + "> \n"
			+ "PREFIX dwp: <" + Vocabulary.PROPERTY + "> \n" + "PREFIX dwg: <" + Vocabulary.GRAPH + "> \n";

	public final static String ListAllFeatures = PREFIXES
			+ "SELECT ?id ?name ?value ?tokenizable ?level FROM dwg:features WHERE { [] a dwt:Feature ; dwp:id ?id ; dwp:name ?name ; dwp:value ?value ; dwp:level ?level ; dwp:tokenizable ?tokenizable . }";
	public final static String ListFromToFeaturesOfBundle = PREFIXES
			+ "SELECT ?fromTo ?name ?value ?tokenizable ?level FROM dwg:features FROM ?bundleId WHERE { ?bundleId dwp:hasFromTo ?fromToUri .  ?fromToUri dwp:id ?fromTo  . ?fromToUri dwp:hasFeature ?f . ?f a dwt:Feature ; dwp:id ?id ; dwp:name ?name ; dwp:value ?value ; dwp:level ?level ; dwp:tokenizable ?tokenizable . }";

	public final static String DeleteBundleFeatures = PREFIXES 
			+ "WITH ?bundleId DELETE { ?bundleId dwp:hasFromTo ?fromTo . ?fromTo dwp:hasFeature ?feature } WHERE { ?bundleId dwp:hasFromTo ?fromTo . ?fromTo dwp:hasFeature ?feature }";

	public final static String ADDFeature = PREFIXES 
			+ "INSERT DATA { GRAPH dwg:features { ?feature a dwt:Feature ; dwp:id ?id ; dwp:name ?name ; dwp:value ?value ; dwp:level ?level ; dwp:tokenizable ?tokenizable .  } }";
	public final static String ADDBundleFeature = PREFIXES 
			+ "INSERT DATA { GRAPH ?bundleId { ?bundleId dwp:hasFromTo ?fromTo .  ?fromTo dwp:id ?id . ?fromTo dwp:hasFeature ?feature } }";
}
