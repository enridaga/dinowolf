package dinowolf.database.h2;

import org.apache.commons.codec.digest.DigestUtils;

import dinowolf.database.annotations.AnnotationAction;
import dinowolf.features.Feature;
import dinowolf.features.FeatureDepth;

public class H2Queries {
	public static final int toInt(FeatureDepth level) {
		switch (level) {
		case Processor:
			return 'P';
		case FromToPorts:
			return 'M';
		case Workflow:
			return 'W';
		case Activity:
			return 'A';
		case OtherPort:
			return 'S';
		case From:
			return 'F';
		case To:
			return 'T';
		default:
			throw new UnsupportedOperationException("Unsupported enum value");
		}
	}

	public static int toInt(AnnotationAction action) {
		switch (action) {
		case MIXED:
			return 'M';
		case PICKED:
			return 'P';
		case NON_PICKED:
			return 'N';
		case NONE:
			return '0';
		case SKIPPED:
			return 'S';
		default:
			throw new UnsupportedOperationException("Unsupported enum value");
		}
	}

	public static final String hashCode(Feature feature) {
		return hashCode(feature.serialize());
	}

	public static final String hashCode(String string) {
		return DigestUtils.shaHex(string);
	}

	public static final FeatureDepth toFeatureLevel(int level) {
		switch (level) {
		case 'P':
			return FeatureDepth.Processor;
		case 'M':
			return FeatureDepth.FromToPorts;
		case 'W':
			return FeatureDepth.Workflow;
		case 'S':
			return FeatureDepth.OtherPort;
		case 'A':
			return FeatureDepth.Activity;
		case 'F':
			return FeatureDepth.From;
		case 'T':
			return FeatureDepth.To;
		default:
			throw new UnsupportedOperationException("Unsupported enum value");
		}
	}

	public static final AnnotationAction toAnnotationAction(int action) {
		switch (action) {
		case 'P':
			return AnnotationAction.PICKED;
		case 'N':
			return AnnotationAction.NON_PICKED;
		case 'M':
			return AnnotationAction.MIXED;
		case '0':
			return AnnotationAction.NONE;
		case 'S':
			return AnnotationAction.SKIPPED;
		default:
			throw new UnsupportedOperationException("Unsupported enum value");
		}
	}

	/*
	 * FEATURES
	 */

	public static final String CREATE_TABLE_FEATURE = "CREATE TABLE IF NOT EXISTS FEATURE (ID INT AUTO_INCREMENT PRIMARY KEY, HASHCODE VARCHAR(40) NOT NULL, NAME VARCHAR NOT NULL, VALUE VARCHAR NOT NULL, LEVEL INT NOT NULL, TOKENIZABLE BOOLEAN DEFAULT FALSE, UNIQUE(HASHCODE))"; // ,
	public static final String CREATE_TABLE_BUNDLE = "CREATE TABLE IF NOT EXISTS BUNDLE (ID INT AUTO_INCREMENT PRIMARY KEY , FILE VARCHAR(255), UNIQUE(FILE))";
	public static final String CREATE_TABLE_PORTPAIR = "CREATE TABLE IF NOT EXISTS PORTPAIR (ID INT AUTO_INCREMENT PRIMARY KEY, BUNDLE INT NOT NULL, NAME VARCHAR NOT NULL, UNIQUE(NAME), FOREIGN KEY (BUNDLE) REFERENCES BUNDLE(ID) ON DELETE CASCADE )";
	public static final String CREATE_TABLE_PORTPAIR_FEATURE = "CREATE TABLE IF NOT EXISTS PORTPAIR_FEATURE (PORTPAIR INT NOT NULL, FEATURE INT NOT NULL, FOREIGN KEY (PORTPAIR) REFERENCES PORTPAIR(ID) ON DELETE CASCADE, FOREIGN KEY (FEATURE) REFERENCES FEATURE(ID) ON DELETE CASCADE, PRIMARY KEY (PORTPAIR,FEATURE))";
	//
	public static final String INSERT_FEATURE = "INSERT INTO FEATURE (HASHCODE,NAME,VALUE,LEVEL,TOKENIZABLE) VALUES (?,?,?,?,?)";
	public static final String INSERT_BUNDLE = "INSERT INTO BUNDLE (FILE) VALUES (?)";
	public static final String DELETE_BUNDLE = "DELETE FROM BUNDLE WHERE FILE = ?";
	public static final String INSERT_PORTPAIR = "INSERT INTO PORTPAIR (BUNDLE, NAME) VALUES (?,?)";
	public static final String INSERT_PORTPAIR_FEATURE = "INSERT INTO PORTPAIR_FEATURE (PORTPAIR,FEATURE) VALUES (?,?)";
	//
	public static final String SELECT_ALL_FEATURES = "SELECT ID, HASHCODE, NAME, VALUE, LEVEL, TOKENIZABLE FROM FEATURE";
	public static final String SELECT_FEATURES_OF_BUNDLE = "SELECT PORTPAIR.NAME, FEATURE.HASHCODE, FEATURE.NAME, FEATURE.VALUE, FEATURE.LEVEL, FEATURE.TOKENIZABLE, FEATURE.ID FROM FEATURE, BUNDLE, PORTPAIR, PORTPAIR_FEATURE WHERE FEATURE.ID = PORTPAIR_FEATURE.FEATURE AND PORTPAIR.ID = PORTPAIR_FEATURE.PORTPAIR AND PORTPAIR.BUNDLE = BUNDLE.ID AND BUNDLE.ID = ?";
	public static final String SELECT_FEATURES_OF_PORTPAIR = "SELECT PORTPAIR.NAME, FEATURE.HASHCODE, FEATURE.NAME, FEATURE.VALUE, FEATURE.LEVEL, FEATURE.TOKENIZABLE, FEATURE.ID FROM FEATURE, BUNDLE, PORTPAIR, PORTPAIR_FEATURE WHERE FEATURE.ID = PORTPAIR_FEATURE.FEATURE AND PORTPAIR.ID = PORTPAIR_FEATURE.PORTPAIR AND PORTPAIR.BUNDLE = BUNDLE.ID AND BUNDLE.ID = ? AND PORTPAIR.NAME = ?";
	public static final String SELECT_FEATURE_IDS_OF_PORTPAIR = "SELECT FEATURE.ID, FEATURE.LEVEL, FEATURE.TOKENIZABLE FROM PORTPAIR, PORTPAIR_FEATURE WHERE PORTPAIR.ID = PORTPAIR_FEATURE.PORTPAIR AND PORTPAIR.NAME = ?";
	public static final String SELECT_BUNDLE_ID_BY_NAME = "SELECT ID FROM BUNDLE WHERE FILE = ?";
	//
	public static final String SELECT_PORTPAIR_OF_BUNDLE = "SELECT NAME FROM PORTPAIR, BUNDLE WHERE PORTPAIR.BUNDLE = BUNDLE.ID AND BUNDLE.FILE = ?";
	public static final String SELECT_PORTPAIR_ID = "SELECT ID FROM PORTPAIR WHERE NAME = ?";
	//
	public static final String SELECT_FEATURE_EXISTS = "SELECT ID FROM FEATURE WHERE HASHCODE = ? LIMIT 1";

	/*
	 * LATTICE
	 */
	public static final String CREATE_TABLE_CONCEPT = "CREATE TABLE IF NOT EXISTS CONCEPT (ID INT AUTO_INCREMENT PRIMARY KEY, OBJECTS VARCHAR NOT NULL, ATTRIBUTES VARCHAR NOT NULL, ATTRIBUTES_SIZE INT NOT NULL)";
	public static final String CREATE_TABLE_LATTICE = "CREATE TABLE IF NOT EXISTS LATTICE (PARENT INT NOT NULL, CHILD INT NOT NULL, PRIMARY KEY (PARENT,CHILD), FOREIGN KEY (PARENT) REFERENCES CONCEPT(ID) ON DELETE CASCADE, FOREIGN KEY (CHILD) REFERENCES CONCEPT(ID) ON DELETE CASCADE)";
	public static final String INSERT_CONCEPT = "INSERT INTO CONCEPT (OBJECTS, ATTRIBUTES, ATTRIBUTES_SIZE) VALUES (?,?,?)";
	public static final String DELETE_CONCEPT = "DELETE FROM CONCEPT WHERE ID = ?";
	public static final String INSERT_PARENT_CHILD = "INSERT INTO LATTICE (PARENT,CHILD) VALUES (?,?)";
	public static final String DELETE_PARENT_CHILD = "DELETE FROM LATTICE WHERE PARENT = ? AND CHILD = ?";
	//
	public static final String SELECT_ALL_CONCEPTS = "SELECT ID, OBJECTS, ATTRIBUTES FROM CONCEPT";
	public static final String SELECT_CONCEPTS_WITH_ATTRIBUTES_SIZE = "SELECT ID, OBJECTS, ATTRIBUTES FROM CONCEPT WHERE ATTRIBUTES_SIZE = ?";
	public static final String SELECT_PARENTS = "SELECT ID, OBJECTS, ATTRIBUTES FROM CONCEPT WHERE ID IN (SELECT PARENT FROM LATTICE WHERE CHILD = ?)";
	public static final String SELECT_CHILDREN = "SELECT ID, OBJECTS, ATTRIBUTES FROM CONCEPT WHERE ID IN (SELECT CHILD FROM LATTICE WHERE PARENT = ?)";
	public static final String SELECT_SUPREMUM = "SELECT ID, OBJECTS, ATTRIBUTES FROM CONCEPT WHERE ID NOT IN (SELECT CHILD FROM LATTICE) LIMIT 1";
	public static final String SELECT_INFIMUM = "SELECT ID, OBJECTS, ATTRIBUTES FROM CONCEPT WHERE ID NOT IN (SELECT PARENT FROM LATTICE) LIMIT 1";
	public static final String SELECT_CONCEPT_SIZE = "SELECT COUNT(*) FROM CONCEPT";
	public static final String SELECT_CONCEPT_IS_PARENT = "SELECT TRUE FROM LATTICE WHERE CHILD = ? AND PARENT = ? LIMIT 1";
	public static final String SELECT_MAX_ATTRIBUTES_CARDINALITY = "SELECT MAX(ATTRIBUTES_SIZE) FROM CONCEPT";

	/*
	 * Annotations
	 */
	public static final String CREATE_TABLE_ANNOTATION_UNIT = "CREATE TABLE IF NOT EXISTS ANNOTATION_UNIT (PORTPAIR INT NOT NULL PRIMARY KEY, ANNOTATION VARCHAR NOT NULL, FOREIGN KEY (PORTPAIR) REFERENCES PORTPAIR(ID))";
	public static final String CREATE_TABLE_ANNOTATION_LOG = "CREATE TABLE IF NOT EXISTS ANNOTATION_LOG (ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, PORTPAIR INT NOT NULL, BUNDLE INT NOT NULL, RECOMMENDED VARCHAR NOT NULL, ACTION VARCHAR(1) NOT NULL, FOREIGN KEY (PORTPAIR) REFERENCES PORTPAIR(ID))";
	public static final String INSERT_ANNOTATION_LOG = "INSERT INTO ANNOTATION_LOG (PORTPAIR, RECOMMENDED, ACTION) VALUES (?,?,?)";
	public static final String INSERT_ANNOTATION_UNIT = "INSERT INTO ANNOTATION_UNIT (PORTPAIR, ANNOTATION) VALUES (?,?)";
	public static final String SELECT_ANNOTATIONS = "SELECT BUNDLE.ID, PORTPAIR.NAME, ANNOTATION FROM BUNDLE, ANNOTATION_UNIT, PORTPAIR WHERE BUNDLE.ID = PORTPAIR.BUNDLE AND PORTPAIR.ID = ANNOTATION_UNIT.PORTPAIR";
}
