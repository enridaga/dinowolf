package dinowolf.database.h2;

import org.apache.commons.codec.digest.DigestUtils;

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
	
	public static final String hashCode(Feature feature){
		return hashCode(feature.serialize());
	}
	
	public static final String hashCode(String string){
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

	public static final String CREATE_TABLE_FEATURE = "CREATE TABLE IF NOT EXISTS FEATURE (ID INT AUTO_INCREMENT PRIMARY KEY, HASHCODE VARCHAR(40) NOT NULL, NAME VARCHAR NOT NULL, VALUE VARCHAR NOT NULL, LEVEL INT NOT NULL, TOKENIZABLE BOOLEAN DEFAULT FALSE, UNIQUE(HASHCODE))"; //, 
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
	public static final String SELECT_FEATURES_OF_BUNDLE = "SELECT PORTPAIR.NAME, FEATURE.HASHCODE, FEATURE.NAME, FEATURE.VALUE, FEATURE.LEVEL, FEATURE.TOKENIZABLE FROM FEATURE, BUNDLE, PORTPAIR, PORTPAIR_FEATURE WHERE FEATURE.ID = PORTPAIR_FEATURE.FEATURE AND PORTPAIR.ID = PORTPAIR_FEATURE.PORTPAIR AND PORTPAIR.BUNDLE = BUNDLE.ID AND BUNDLE.ID = ?";
	public static final String SELECT_BUNDLE_ID_BY_NAME = "SELECT ID FROM BUNDLE WHERE FILE = ?";
	//
	public static final String SELECT_PORTPAIR_OF_BUNDLE = "SELECT NAME FROM PORTPAIR, BUNDLE WHERE PORTPAIR.BUNDLE = BUNDLE.ID AND BUNDLE.FILE = ?";
	//
	public static final String SELECT_FEATURE_EXISTS = "SELECT ID FROM FEATURE WHERE HASHCODE = ? LIMIT 1";
	
}
