package dinowolf.database.h2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import dinowolf.database.annotations.AnnotationAction;
import enridaga.colatti.Rule;
import enridaga.colatti.RuleImpl;

public class AnnotationsLoggerH2 extends H2Connected implements AnnotationsDatabase {
	static private final Logger l = LoggerFactory.getLogger(AnnotationsLoggerH2.class);
	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			l.error("Error loading JDBC driver", e);
		}
	}

	public AnnotationsLoggerH2(File location, String user, String password) {
		super(location, user, password);
	}

	public AnnotationsLoggerH2(File location) {
		super(location);
	}

	public AnnotationsLoggerH2(File location, String database, String user, String password) {
		super(location, database, user, password);
	}

	@Override
	protected void setup() {
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_ANNOTATION_UNIT);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_ANNOTATION_LOG);
			conn.createStatement().execute(H2Queries.ALTER_TABLE_ANNOTATION_LOG_ADD_COLUMN_AVGCONFIDENCE);
			conn.createStatement().execute(H2Queries.ALTER_TABLE_ANNOTATION_LOG_ADD_COLUMN_AVGSUPPORT);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dinowolf.database.h2.AnnotationsDatabase#noAnnotations(java.lang.String,
	 * java.lang.String, java.util.List)
	 */
	@Override
	public void noAnnotations(String bundleId, String portPairName, List<Rule> recommended, int duration) throws IOException {
		AnnotationAction action = AnnotationAction.NONE;
		_annotate(bundleId, portPairName, Collections.emptyList(), recommended, action, duration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinowolf.database.h2.AnnotationsDatabase#skipAnnotations(java.lang.
	 * String, java.lang.String, java.util.List)
	 */
	@Override
	public void skipAnnotations(String bundleId, String portPairName, List<Rule> recommended, int duration) throws IOException {
		AnnotationAction action = AnnotationAction.SKIPPED;
		_annotate(bundleId, portPairName, Collections.emptyList(), recommended, action, duration);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinowolf.database.h2.AnnotationsDatabase#annotate(java.lang.String,
	 * java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public void annotate(String bundleId, String portPairName, List<String> annotations, List<Rule> recommended, int duration)
			throws IOException {
		// l.debug("annotate {} {} {} {}", new Object[] { bundleId,
		// portPairName, annotations, recommended });
		// default when no annotations
		AnnotationAction action = AnnotationAction.SKIPPED;
		if (!annotations.isEmpty()) {
			action = AnnotationAction.NON_PICKED;
		}
		if (!recommended.isEmpty()) {
			boolean oneFoundTrue = false;
			boolean oneFoundFalse = false;
			for (String ann : annotations) {
				if (recommended(ann, recommended)) {
					if (!oneFoundTrue) {
						oneFoundTrue = true;
					}
				} else if (!oneFoundFalse) {
					oneFoundFalse = true;
				}
			}

			if (oneFoundTrue) {
				action = AnnotationAction.PICKED;
				if (oneFoundFalse) {
					action = AnnotationAction.MIXED;
				}
			}
		}

		_annotate(bundleId, portPairName, annotations, recommended, action, duration);
	}

	private boolean recommended(String annotation, List<Rule> rules) {
		for (Rule r : rules) {
			if (Arrays.asList(r.head()).contains(annotation)) {
				return true;
			}
		}
		return false;
	}

	private void _annotate(String bundleId, String portPairName, List<String> annotations, List<Rule> recommended,
			AnnotationAction action, int duration) throws IOException {
		l.trace("_annotate {} {} {} {} {}", new Object[] { bundleId, portPairName, annotations, recommended, action });
		try {
			try (Connection conn = getConnection()) {
				conn.setAutoCommit(false);
				try {
					PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_PORTPAIR_ID);
					st.setString(1, portPairName);
					ResultSet rs = st.executeQuery();
					if (!rs.next()) {
						throw new IOException("portpair does not exists!");
					}
					int portPairId = rs.getInt(1);

					PreparedStatement st2 = conn.prepareStatement(H2Queries.SELECT_BUNDLE_ID);
					st2.setString(1, bundleId);
					ResultSet rs2 = st2.executeQuery();
					if (!rs2.next()) {
						throw new IOException("bundle does not exists!");
					}
					int bundle = rs2.getInt(1);

					// When annotation is skipped, we don't write it.
					if (!action.equals(AnnotationAction.SKIPPED)) {
						String ann = toJsonString(annotations);
						try (PreparedStatement ins = conn.prepareStatement(H2Queries.INSERT_ANNOTATION_UNIT)) {
							ins.setInt(1, portPairId);
							ins.setString(2, ann);
							ins.executeUpdate();
						}
					}
					String rec = rulesToJsonString(recommended);
					try (PreparedStatement ins = conn.prepareStatement(H2Queries.INSERT_ANNOTATION_LOG)) {
						ins.setInt(1, portPairId);
						ins.setInt(2, bundle);
						ins.setString(3, rec);
						ins.setString(4, H2Queries.toDbKey(action));
						
						// annotations
						ins.setInt(5, annotations.size());
						// duration
						ins.setInt(6, duration);
						// fromrec
						Set<String> fromRec = new HashSet<String>();
						List<Integer> ranks = new ArrayList<Integer>();
						List<Double> rel = new ArrayList<Double>();
						List<Double> confidence = new ArrayList<Double>();
						List<Double> support = new ArrayList<Double>();
						recommended.sort(new RuleSortByRelevance());
						
						for(String a: annotations) {
							int rank = 0;
							for(Rule r: recommended){
								rank++;
								if(Arrays.asList(r.head()).contains(a)){
									fromRec.add(a);
									ranks.add(rank);
									rel.add(r.relativeConfidence());
									confidence.add(r.confidence());
									support.add(r.support());
								}
							}
						}
						ins.setInt(7, fromRec.size());
						// avgrank
						Double avgrank = 0.0;
						if(!ranks.isEmpty()){
							avgrank = ranks.stream().mapToInt(val -> val).average().getAsDouble();
						}
						ins.setDouble(8, avgrank);
						Double avgrel = 0.0;
						// avgrel
						if(!rel.isEmpty()){
							avgrel = rel.stream().mapToDouble(val -> val).average().getAsDouble();
						}
						ins.setDouble(9, avgrel);
						
						// avgconfidence
						Double avgconfidence = 0.0;
						if(!confidence.isEmpty()){
							avgconfidence = confidence.stream().mapToDouble(val -> val).average().getAsDouble();
						}
						ins.setDouble(10, avgconfidence);
						
						// avgsupport
						Double avgsupport = 0.0;
						if(!support.isEmpty()){
							avgsupport = support.stream().mapToDouble(val -> val).average().getAsDouble();
						}
						ins.setDouble(11, avgsupport);
						
						l.trace("_annotate measures: annotations:{}, duration:{}, fromRec:{}, avgrank:{}, avgrel:{}, avgconfidence: {}, avgsupport: {}", new Object[] {
								annotations.size(),
								duration,
								fromRec,
								avgrank,
								avgrel,
								avgconfidence,
								avgsupport
						});
						ins.executeUpdate();
					}
					conn.commit();
				} catch (SQLException e) {
					conn.rollback();
					throw e;
				}
			}
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}
	
	public static class RuleSortByRelevance implements Comparator<Rule>{
		@Override
		public int compare(Rule o1, Rule o2) {
			// -1 o1 first
			if(o1.relativeConfidence() > o2.relativeConfidence())
				return -1;
			if(o1.relativeConfidence() < o2.relativeConfidence())
				return 1;
			if(o1.confidence() > o2.confidence())
				return -1;
			if(o1.confidence() < o2.confidence())
				return 1;
			if(o1.support() > o2.support())
				return -1;
			if(o1.support() < o2.support())
				return 1;
			
			return 0;
		}
	}

	public static final String toJsonString(List<String> list) {
		JsonArray array = new JsonArray();
		for (String l : list) {
			array.add(l);
		}
		return new Gson().toJson(array);
	}

	public static final List<String> toStringList(String json) {
		List<String> arl = new ArrayList<String>();
		if(json != null){
			JsonArray arr = (JsonArray) new JsonParser().parse(json);
			for (int x = 0; x < arr.size(); x++) {
				arl.add(arr.get(x).getAsString());
			}
		}
		return arl;
	}

	public static final String rulesToJsonString(List<Rule> list) {
		JsonArray array = new JsonArray();
		for (Rule l : list) {
			array.add(ruleToJsonObject(l));
		}
		return new Gson().toJson(array);
	}
	
	
	public static final JsonObject ruleToJsonObject(Rule l){
		JsonObject o = new JsonObject();
		o.addProperty("support", l.support());
		o.addProperty("confidence", l.confidence());
		o.addProperty("relativeConfidence", l.relativeConfidence());
		JsonArray h = new JsonArray();
		for (Object j : l.head())
			h.add(j.toString());
		o.add("head", h);
		JsonArray b = new JsonArray();
		for (Object j : l.body())
			b.add(j.toString());
		o.add("body", b);
		return o;
	}

	public static final List<Rule> jsonStringToRules(String json) {
		List<Rule> rules = new ArrayList<Rule>();
		if(json != null && json.length() > 2){ // 2 is the lenght of the empty array []
			JsonArray array = (JsonArray) new JsonParser().parse(json);
			for(int i = 0; i < array.size(); i ++){
				JsonObject o = (JsonObject) array.get(i);
				rules.add(jsonObjectToRule(o));
			}
		}
		return rules;
	}
	
	public static final Rule jsonObjectToRule(JsonObject json) {
		Gson G = new Gson();
		JsonObject o = (JsonObject) json;
		RuleImpl r = new RuleImpl();
		Type type = new TypeToken<String[]>() {}.getType();
		r.head(G.fromJson(o.get("head"), type));
		r.body(G.fromJson(o.get("body"), type));
		r.confidence(o.get("confidence").getAsDouble());
		r.confidence(o.get("support").getAsDouble());
		r.confidence(o.get("relativeConfidence").getAsDouble());
		return r;
	}
	@Override
	public void walk(AnnotationsWalker walker) throws IOException {
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_ALL_ANNOTATIONS);
				ResultSet rs = st.executeQuery();
				boolean goNext = true;
				while (rs.next() && goNext) {
					String bundleId = rs.getString(1);
					String portPairName = rs.getString(2);
					String annotations = rs.getString(3);
					goNext = walker.read(bundleId, portPairName, toStringList(annotations));
				}
			}
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void walk(LogWalker walker) throws IOException {
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_ANNOTATION_LOGS);
				ResultSet rs = st.executeQuery();
				boolean goNext = true;
				while (rs.next() && goNext) {
					String bundleId = rs.getString(1);
					String portPairName = rs.getString(2);
					String annotations = rs.getString(3);
					String recommended = rs.getString(4);
					String action =  rs.getString(5);
					int logId = rs.getInt(6);
					int count = rs.getInt(7);
					int duration = rs.getInt(8);
					int fromRec = rs.getInt(9);
					double avgrank = rs.getDouble(10);
					double avgrel = rs.getDouble(11);
					double avgconfidence = rs.getDouble(12);
					double avgsupport = rs.getDouble(13);
					goNext = walker.read(bundleId, portPairName, toStringList(annotations), jsonStringToRules(recommended), H2Queries.toAnnotationAction(action.charAt(0)), logId, count, duration, fromRec, avgrank, avgrel, avgconfidence, avgsupport);
				}
			}
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<String> annotations(String portPairName) throws IOException {
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_ANNOTATION_OF_PORTPAIR);
				st.setString(1, portPairName);
				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					return toStringList(rs.getString(1));
				}
			}
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<String> annotating() throws IOException {
		List<String> annotating = new ArrayList<String>();
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_BUNDLE_ANNOTATING);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					annotating.add(rs.getString(2));
				}
			}
			return Collections.unmodifiableList(annotating);
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public boolean annotated(String portPairName) throws IOException {
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_PORTPAIR_IS_ANNOTATED);
				st.setString(1,  portPairName);
				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					return true;
				}
			}
			return false;
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}

	/**
	 * FIXME
	 */
	@Override
	public List<String> neverAnnotated() throws IOException {
		List<String> annotating = new ArrayList<String>();
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_BUNDLE_NEVER_ANNOTATED);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					annotating.add(rs.getString(1));
				}
			}
			return Collections.unmodifiableList(annotating);
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Return all portpairs with the optional list of annotations, if any, or an
	 * empty list.
	 */
	@Override
	public Map<String, List<String>> bundleAnnotations(String bundleId) throws IOException {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			try (Connection conn = getConnection()) {

				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_ANNOTATIONS_OF_BUNDLE);
				st.setString(1, bundleId);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					String pp = rs.getString(1);
					String annotations = rs.getString(2);
					if (annotations != null) {
						List<String> ann = toStringList(rs.getString(2));
						if (map.containsKey(pp)) {
							throw new IOException("Assumption not met");
						}
						map.put(pp, ann);
					} else {
						map.put(pp, Collections.emptyList());
					}
				}
			}
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
		return Collections.unmodifiableMap(map);
	}

	@Override
	public Map<String, Integer> progress() throws IOException {
		Map<String, Integer> progress = new LinkedHashMap<String, Integer>();
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_BUNDLE_PORTPAIR_ANNOTATION_PROGRESS);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					String b = rs.getString(1);
					int annotated = rs.getInt(2);
					int total = rs.getInt(3);
					int percentage = (int) ((annotated * 100) / (total));
					progress.put(b, percentage);

				}
			}
			return Collections.unmodifiableMap(progress);
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}
}
