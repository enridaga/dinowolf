package dinowolf.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationHelperTest {
	private static final Logger l = LoggerFactory.getLogger(AnnotationHelperTest.class);

	private InputStream __f(String n) {
		return getClass().getClassLoader().getResourceAsStream(n);
	}
	
	@Test
	public void test() throws ReaderException, IOException{
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wb = io.readBundle(__f("3291-Net_reproductive_rate__Ro_-v2.wfbundle"), null);
		AnnotationHelper h = new AnnotationHelper(wb);
		String title = h.getTitle(wb.getMainWorkflow());
		l.info("reading workflow title: {}", title);
		String description = h.getDescription(wb.getMainWorkflow());
		l.info("reading workflow description: {}", description);
	}
	
	
}
