package dinowolf.database.annotations;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.h2.AnnotationsLoggerH2;
import dinowolf.database.h2.H2Queries;
import dinowolf.database.h2.LogWalker;
import enridaga.colatti.RuleImpl;

public class AnnotationsDatabaseH2Test {
	private static final Logger l = LoggerFactory.getLogger(AnnotationsDatabaseH2Test.class);

	@Rule
	public TestName name = new TestName();

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void before() throws IOException, SQLException {
		l.debug("{}", name.getMethodName());
		File folder =  testFolder.newFolder() ;
		connectionUrl = "jdbc:h2:file:" + folder.getAbsolutePath() + "/H2Test";
		l.debug("Connection url: {}", connectionUrl);

		Connection conn = DriverManager.getConnection(connectionUrl, user, pwd);
		// We only create resources needed by the two tables
		conn.createStatement().execute(H2Queries.CREATE_TABLE_BUNDLE);
		conn.createStatement().execute(H2Queries.CREATE_TABLE_PORTPAIR);
		//
		conn .createStatement().execute(H2Queries.CREATE_TABLE_ANNOTATION_UNIT);
		conn .createStatement().execute(H2Queries.CREATE_TABLE_ANNOTATION_LOG);
		
		// INSERT FAKE BUNDLES
		conn.createStatement().executeUpdate("INSERT INTO BUNDLE (FILE) VALUES ('FILE1')");
		conn.createStatement().executeUpdate("INSERT INTO BUNDLE (FILE) VALUES ('FILE2')");
		conn.createStatement().executeUpdate("INSERT INTO BUNDLE (FILE) VALUES ('FILE3')");
		
		// INSERT FAKE PORTPAIRS
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE1/Workflow1/Processor1/IO:foo1:bar1', 1)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE1/Workflow1/Processor1/IO:foo1:bar2', 1)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE1/Workflow1/Processor1/IO:bar2:foo2', 1)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE1/Workflow1/Processor1/IO:bar1:foo3', 1)");

		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE2/Workflow1/Processor1/IO:foo1:bar1', 2)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE2/Workflow1/Processor1/IO:foo1:bar2', 2)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE2/Workflow1/Processor1/IO:bar2:foo2', 2)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE2/Workflow1/Processor1/IO:bar1:foo3', 2)");

		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE3/Workflow1/Processor1/IO:foo1:bar1', 3)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE3/Workflow1/Processor1/IO:foo1:bar2', 3)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE3/Workflow1/Processor1/IO:bar2:foo2', 3)");
		conn.createStatement().executeUpdate("INSERT INTO PORTPAIR (NAME, BUNDLE) VALUES ('FILE3/Workflow1/Processor1/IO:bar1:foo3', 3)");
		
		database = new AnnotationsLoggerH2(folder, "H2Test", user, pwd);
	}
	
	
	private String connectionUrl;
	private String user = "user";
	private String pwd = "pwd234556";
	private AnnotationsLoggerH2 database;
	
	@Test
	public void empty() throws IOException{
		Assert.assertTrue(database.progress().size() == 3);
		for(Entry<String,Integer> e : database.progress().entrySet())
			Assert.assertTrue(e.getValue() == 0);
	}

	@Test
	public void annotating() throws IOException{
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar1", Arrays.asList("hasExtraction"), Collections.emptyList(), 1);
		Assert.assertTrue(database.annotating().size() == 1);
		List<String> str = database.annotating();
		l.debug("{}", str);
		Assert.assertTrue(str.get(0).equals("FILE1"));
	}
	
	@Test
	public void neverAnnotated() throws IOException, SQLException{
		Assert.assertTrue(database.neverAnnotated().size() == 3);
	}
	
	@Test
	public void isAnnotated() throws IOException, SQLException{
		Assert.assertTrue(database.annotated("wathever") == false);
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar1", Arrays.asList("hasExtraction"), Collections.emptyList(), 2);
		Assert.assertTrue(database.annotated("FILE1/Workflow1/Processor1/IO:foo1:bar1") == true);
	}
	
	private List<enridaga.colatti.Rule> fakeRules(){
		List<enridaga.colatti.Rule> rules = new ArrayList<enridaga.colatti.Rule>();
		RuleImpl r = new RuleImpl();
		r.body(new String[]{"a","b","c"});
		r.head(new String[]{"d"});
		r.confidence(1.0);
		r.support(0.5);
		r.relativeConfidence(0.7);
		rules.add(r);
		
		r = new RuleImpl();
		r.body(new String[]{"d","e","f"});
		r.head(new String[]{"g"});
		r.confidence(0.1);
		r.support(0.4);
		r.relativeConfidence(0.2);
		rules.add(r);
		return rules;
	}
	
	@Test
	public void walk() throws IOException, SQLException{
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar1", Arrays.asList("hasExtraction"), Collections.emptyList(), 0);
		database.skipAnnotations("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar2", Collections.emptyList(), 0);
		database.skipAnnotations("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar2", Collections.emptyList(), 0);
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar2", Arrays.asList("hasSelection"), fakeRules(), 0);
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:bar2:foo2", Arrays.asList("hasStandIn"), Collections.emptyList(), 0);
		
		final List<String> tester = new ArrayList<String>();
		database.walk(new LogWalker() {
			@Override
			public boolean read(String bundleId, String portPairName, List<String> annotations, List<enridaga.colatti.Rule> recommended,
					AnnotationAction action, int logId, int count, int duration, int fromrec, double avgrank, double avgrel) throws IOException {
				l.info("{} {} {} {} {} {} {} {} {} {} {}", new Object[]{bundleId, portPairName, annotations, recommended, action, logId, count, duration, fromrec, avgrank, avgrel});
				tester.add(bundleId);
				return true;
			}
		});
		
		Assert.assertTrue(tester.size() == 5);
	}
	
	
	@Test
	public void bundleAnnotations() throws IOException{
		//
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar1", Arrays.asList("hasExtraction"), Collections.emptyList(), 0);
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar2", Arrays.asList("hasSelection"), Collections.emptyList(), 0);
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:bar2:foo2", Arrays.asList("hasStandIn"), Collections.emptyList(), 0);
		Assert.assertTrue(database.annotating().size() == 1);
		Assert.assertTrue(database.annotating().get(0).equals("FILE1"));
		
		Map<String,List<String>> m = database.bundleAnnotations("FILE1");
		Assert.assertTrue(m.get("FILE1/Workflow1/Processor1/IO:foo1:bar1").get(0).equals("hasExtraction"));
		Assert.assertTrue(m.get("FILE1/Workflow1/Processor1/IO:foo1:bar2").get(0).equals("hasSelection"));
		Assert.assertTrue(m.get("FILE1/Workflow1/Processor1/IO:bar2:foo2").get(0).equals("hasStandIn"));
		Assert.assertTrue(m.get("FILE1/Workflow1/Processor1/IO:bar1:foo3").size() == 0);
	}
	
	@Test
	public void testMeasures() throws IOException {
		List<enridaga.colatti.Rule> rules = new ArrayList<enridaga.colatti.Rule>();
		RuleImpl r = new RuleImpl();
		r.body(new String[]{"a","b","c"});
		r.head(new String[]{"d"});
		r.confidence(1.0);
		r.support(0.5);
		r.relativeConfidence(0.7);
		rules.add(r);
		
		r = new RuleImpl();
		r.body(new String[]{"d","e","f"});
		r.head(new String[]{"g"});
		r.confidence(0.1);
		r.support(0.4);
		r.relativeConfidence(0.2);
		rules.add(r);
		database.annotate("FILE1", "FILE1/Workflow1/Processor1/IO:foo1:bar1", Arrays.asList(new String[]{"d","g"}), rules, 5);
		database.walk(new LogWalker() {
			@Override
			public boolean read(String bundleId, String portPairName, List<String> annotations, List<enridaga.colatti.Rule> recommended,
					AnnotationAction action, int logId, int count, int duration, int fromrec, double avgrank, double avgrel) throws IOException {
				l.info("{} {} {} {} {} {} {} {} {} {} {}", new Object[]{bundleId, portPairName, annotations, recommended, action, logId, count, duration, fromrec, avgrank, avgrel});
				
				return true;
			}
		});
	}
}
