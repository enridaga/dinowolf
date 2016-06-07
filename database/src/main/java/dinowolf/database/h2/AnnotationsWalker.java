package dinowolf.database.h2;

import java.io.IOException;
import java.util.List;

public interface AnnotationsWalker {

	boolean read(String bundleId, String portPairName, List<String> annotations) throws IOException;
}
