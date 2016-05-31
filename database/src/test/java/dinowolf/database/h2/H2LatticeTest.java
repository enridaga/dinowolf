package dinowolf.database.h2;

import java.io.IOException;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enridaga.colatti.Concept;
import enridaga.colatti.ConceptInMemory;
import enridaga.colatti.InsertObject;
import enridaga.colatti.Lattice;
import enridaga.colatti.LatticeInMemory;

public class H2LatticeTest {
	private static final Logger l = LoggerFactory.getLogger(H2InsertStressTest.class);

	@Rule
	public TestName name = new TestName();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void before() throws IOException {
		l.debug(">>> {} <<<", name.getMethodName());
		connectionUrl = "jdbc:h2:file:" + testFolder.newFolder().getAbsolutePath() + "/H2Test";
		l.debug("Connection url: {}", connectionUrl);
		lattice = new H2Lattice(testFolder.newFolder(), "Test", user, pwd);

	}

	private String connectionUrl;
	private String user = "user";
	private String pwd = "pwd234556";
	private Lattice lattice;

	@Test
	public void addReturnBooleanCorrectly() throws Exception {
		// TODO
		l.debug("supremum: {}");
		InsertObject colatti = new InsertObject(lattice);
		boolean ret = colatti.perform("A", "a", "b", "c");
		Assert.assertTrue(ret);
		ret = colatti.perform("A", "a", "b", "c");
		Assert.assertTrue(!ret);
	}

	@Test
	public void testWithTwoObjects() throws Exception {
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		l.debug("Supremum: {}", colatti.lattice().supremum());
		l.debug("Infimum: {}", colatti.lattice().infimum());
		Assert.assertTrue(colatti.lattice().size() == 4);

		Concept supremumTest = lattice.getConceptFactory().make(new String[] { "A", "B" }, new String[] { "b", "c" });
		Concept infimumTest = lattice.getConceptFactory().make(new String[] {}, new String[] { "a", "b", "c", "d" });

		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));

		lattice.topDown(debugConcepts);
	}

	@Test
	public void testAddExistingObjectDoesNotChangeLattice() throws Exception {
		Lattice lattice = new LatticeInMemory();
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		Assert.assertTrue(colatti.perform("D", "e", "f"));
		// log.info("{}",colatti.lattice().concepts());
		Assert.assertTrue(colatti.lattice().size() == 6);

		if (l.isDebugEnabled()) {
			l.debug("Supremum: {}", colatti.lattice().supremum());
			l.debug("Infimum: {}", colatti.lattice().infimum());
		}
		Concept supremumTest = new ConceptInMemory(new String[] { "D", "A", "B" }, new String[] {});
		Concept infimumTest = new ConceptInMemory(new String[] {}, new String[] { "e", "f", "a", "b", "c", "d" });
		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		if (l.isDebugEnabled())
			colatti.lattice().bottomUp(debugConcepts);

		boolean affects = colatti.perform("A", "a", "b", "c");

		if (l.isDebugEnabled())
			colatti.lattice().bottomUp(debugConcepts);

		Assert.assertFalse(affects);
	}

	@Test
	public void testWithManyeObjects() throws Exception {
		InsertObject colatti = new InsertObject(lattice);
		Assert.assertTrue(colatti.perform("A", "a", "b", "c"));
		Assert.assertTrue(colatti.perform("B", "b", "c", "d"));
		Assert.assertTrue(colatti.perform("C", "l"));
		Assert.assertTrue(colatti.perform("D", "e", "f", "g"));
		Assert.assertTrue(colatti.perform("E", "e", "f", "h"));
		Assert.assertTrue(colatti.perform("F", "e", "f", "i"));
		Assert.assertTrue(colatti.perform("G", "e", "f", "j"));
		Assert.assertTrue(colatti.perform("H", "e", "f", "k"));
		Assert.assertTrue(colatti.perform("I", "e", "f", "l"));
		// Assert.assertTrue(colatti.lattice().size() == 6);

		if (l.isDebugEnabled()) {
			l.debug("Supremum: {}", colatti.lattice().supremum());
			l.debug("Infimum: {}", colatti.lattice().infimum());
		}
		Concept supremumTest = lattice.getConceptFactory().make(new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" },
				new String[] {});
		Concept infimumTest = lattice.getConceptFactory().make(new String[] {},
				new String[] { "e", "f", "a", "b", "c", "d", "g", "h", "i", "j", "k", "l" });
		Assert.assertTrue(colatti.lattice().supremum().equals(supremumTest));
		Assert.assertTrue(colatti.lattice().infimum().equals(infimumTest));
		if (l.isDebugEnabled())
			colatti.lattice().bottomUp(debugConcepts);
	}

	private static final Function<Concept, Boolean> debugConcepts = new Function<Concept, Boolean>() {
		@Override
		public Boolean apply(Concept c) {
			l.debug("> {}", c);
			return true;
		};
	};
}
