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
		WorkflowBundle wb = io.readBundle(__f("A_graphical_plot_of_White_Ash__Beech__and_Hemlock_populations_in_Onondaga_County__NY-v1.wfbundle"), null);
		AnnotationHelper h = new AnnotationHelper(wb);
		String title = h.getTitle(wb.getMainWorkflow());
		l.info("reading title: {}", title);
	}
}
