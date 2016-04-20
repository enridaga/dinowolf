package dinowolf.database.repository;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRepositoryTest {
	private static final Logger l = LoggerFactory.getLogger(FileRepositoryTest.class);
	
	@Rule
	public TestName name = new TestName();

	private static File directory;

	private Repository repository;
	private WorkflowBundleIO io;

	private String bundleFile = "ADR-S-v2";

	@BeforeClass
	public static void beforeClass() throws IOException {
		directory = new File("./FileRepositoryTest-data");
		if (directory.exists())
			FileUtils.deleteDirectory(directory);
		directory.mkdir();
		directory.deleteOnExit();
	}

	@Before
	public void before() {
		repository = new FileRepository(directory);
		io = new WorkflowBundleIO();
	}

	@Test
	public void put() throws ReaderException, IOException {
		l.info("{}", name.getMethodName());
		WorkflowBundle bundle = io
				.readBundle(getClass().getClassLoader().getResourceAsStream("./" + bundleFile + ".wfbundle"), null);
		l.debug("Putting workflow name {}", bundle.getName());
		l.debug("Putting workflow id {}", bundle.getIdentifier());
		l.debug("Putting repositoryId {}", bundleFile);
		repository.put(bundle, bundleFile, false);
		Assert.assertTrue(repository.list().size() == 1);
		
		l.debug("Loading repositoryId {}", bundleFile);
		WorkflowBundle bundle2 = repository.get(bundleFile);
		l.debug("Loaded workflow name {}", bundle2.getName());
		l.debug("Loaded workflow id {}", bundle2.getIdentifier());
		Assert.assertTrue(bundle2.getIdentifier().equals(bundle.getIdentifier()));
	}
}
