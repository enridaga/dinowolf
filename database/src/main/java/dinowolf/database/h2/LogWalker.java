package dinowolf.database.h2;

import java.io.IOException;
import java.util.List;

import dinowolf.database.annotations.AnnotationAction;
import enridaga.colatti.Rule;

public interface LogWalker {

	boolean read(String bundleId, String portPairName, List<String> annotations, List<Rule> list, AnnotationAction action, int logId, int annotationsCount, int duration, int fromrec, double avgrank, double avgrel) throws IOException;
}
