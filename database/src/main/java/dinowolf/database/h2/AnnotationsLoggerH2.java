package dinowolf.database.h2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dinowolf.database.annotations.AnnotationAction;
import enridaga.colatti.Rule;

public class AnnotationsLoggerH2 extends H2Connected implements Annotations {
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
	 * @see dinowolf.database.h2.Annotations#noAnnotations(java.lang.String,
	 * java.lang.String, java.util.List)
	 */
	@Override
	public void noAnnotations(String bundleId, String portPairName, List<Rule> recommended) throws IOException {
		AnnotationAction action = AnnotationAction.NONE;
		_annotate(bundleId, portPairName, Collections.emptyList(), recommended, action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinowolf.database.h2.Annotations#skipAnnotations(java.lang.String,
	 * java.lang.String, java.util.List)
	 */
	@Override
	public void skipAnnotations(String bundleId, String portPairName, List<Rule> recommended) throws IOException {
		AnnotationAction action = AnnotationAction.SKIPPED;
		_annotate(bundleId, portPairName, Collections.emptyList(), recommended, action);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinowolf.database.h2.Annotations#annotate(java.lang.String,
	 * java.lang.String, java.util.List, java.util.List)
	 */
	@Override
	public void annotate(String bundleId, String portPairName, List<String> annotations, List<Rule> recommended)
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

		_annotate(bundleId, portPairName, annotations, recommended, action);
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
			AnnotationAction action) throws IOException {
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

	public static final String toJsonString(List<String> list) {
		JsonArray array = new JsonArray();
		for (String l : list) {
			array.add(l);
		}
		return new Gson().toJson(array);
	}

	public static final List<String> toStringList(String json) {
		List<String> arl = new ArrayList<String>();
		JsonArray arr = (JsonArray) new JsonParser().parse(json);
		for (int x = 0; x < arr.size(); x++) {
			arl.add(arr.get(x).getAsString());
		}
		return arl;
	}

	public static final String rulesToJsonString(List<Rule> list) {
		JsonArray array = new JsonArray();
		for (Rule l : list) {
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
			array.add(o);
		}
		return new Gson().toJson(array);
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
					annotating.add(rs.getString(1));
				}
			}
			return Collections.unmodifiableList(annotating);
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}

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

	@Override
	public Map<String, Integer> progress() throws IOException {
		Map<String, Integer> progress = new HashMap<String, Integer>();
		try {
			try (Connection conn = getConnection()) {
				PreparedStatement st = conn.prepareStatement(H2Queries.SELECT_BUNDLE_PORTPAIR_ANNOTATION_STATUS);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					String b = rs.getString(1);
					int amount = rs.getInt(2);
					boolean status = rs.getBoolean(3);
					if (!progress.containsKey(b)) {
						// First time we meet this bundle
						if (status == false) {
							// This item was never annotated, because the result set is
							// ordered by bundle,status desc, so if any true was occurred
							// it should have happened before.
							progress.put(b, 0);
						} else {
							// This bundle was partly or fully annotated
							// We remember the amount of items annotated.
							progress.put(b, amount);
						}
					} else {
						if (status == true) {
							// If status is TRUE, there is a problem in our
							// assumption or the
							// Query is wrong.
							// This should never happen, anyway
							throw new IOException("Unexpected assumption!");
						}
						// If it is already there I expect that the amount
						// previously loaded is the number of
						// portpairs that was annotated, so I calculate
						// the percentage
						int annotated = progress.get(b);
						int percentage = (int) ((annotated * 100) / (amount + annotated));
						progress.put(b, percentage);
						//
					}
				}
			}
			return Collections.unmodifiableMap(progress);
		} catch (SQLException | IOException e) {
			throw new IOException(e);
		}
	}
}
