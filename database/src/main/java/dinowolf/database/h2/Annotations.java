package dinowolf.database.h2;

import java.io.IOException;
import java.util.List;

import dinowolf.database.annotations.AnnotationAction;
import enridaga.colatti.Lattice;
import enridaga.colatti.Rule;

public interface Annotations {

	/**
	 * 
	 * Considered as {@link AnnotationAction#NONE}
	 * 
	 * @param bundleId
	 * @param portPairName
	 * @param recommended
	 * @throws IOException
	 */
	void noAnnotations(String bundleId, String portPairName, List<Rule> recommended) throws IOException;

	/**
	 * 
	 * Considered as {@link AnnotationAction#SKIPPED}
	 * 
	 * @param bundleId
	 * @param portPairName
	 * @param recommended
	 * @throws IOException
	 */
	void skipAnnotations(String bundleId, String portPairName, List<Rule> recommended) throws IOException;

	/**
	 * 
	 * 
	 * @param bundleId
	 * @param portPairName
	 * @param annotations - If empty, considered as {@link AnnotationAction#SKIPPED}
	 * @throws IOException
	 */
	void annotate(String bundleId, String portPairName, List<String> annotations, List<Rule> recommended)
			throws IOException;
	
	void walk(AnnotationsWalker walker) throws IOException;
}