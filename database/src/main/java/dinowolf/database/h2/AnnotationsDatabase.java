package dinowolf.database.h2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import dinowolf.database.annotations.AnnotationAction;
import enridaga.colatti.Rule;

public interface AnnotationsDatabase {

	/**
	 * 
	 * Considered as {@link AnnotationAction#NONE}
	 * 
	 * @param bundleId
	 * @param portPairName
	 * @param recommended
	 * @throws IOException
	 */
	void noAnnotations(String bundleId, String portPairName, List<Rule> recommended, int duration) throws IOException;

	/**
	 * 
	 * Considered as {@link AnnotationAction#SKIPPED}
	 * 
	 * @param bundleId
	 * @param portPairName
	 * @param recommended
	 * @throws IOException
	 */
	void skipAnnotations(String bundleId, String portPairName, List<Rule> recommended, int duration) throws IOException;

	/**
	 * 
	 * 
	 * @param bundleId
	 * @param portPairName
	 * @param annotations - If empty, considered as {@link AnnotationAction#SKIPPED}
	 * @throws IOException
	 */
	void annotate(String bundleId, String portPairName, List<String> annotations, List<Rule> recommended, int duration)
			throws IOException;
	
	void walk(AnnotationsWalker walker) throws IOException;
	
	void walk(LogWalker walker) throws IOException;
	
	List<String> annotations(String portPairName) throws IOException;
	
	Map<String,Integer> progress() throws IOException;
	
	Map<String,List<String>> bundleAnnotations(String bundleId) throws IOException;
	
	/**
	 * Bundle that was partially annotated
	 * @return
	 */
	List<String> annotating()throws IOException;
	
	List<String> neverAnnotated()throws IOException;
	
	boolean annotated(String portPairName) throws IOException;
	
}