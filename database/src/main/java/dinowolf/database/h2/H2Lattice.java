package dinowolf.database.h2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import enridaga.colatti.ColattiException;
import enridaga.colatti.Concept;
import enridaga.colatti.ConceptFactory;
import enridaga.colatti.Lattice;

/**
 * 
 * @author enridaga
 *
 */
public class H2Lattice extends H2Connected implements Lattice {

	static private final Logger l = LoggerFactory.getLogger(H2Lattice.class);

	static {
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e) {
			l.error("Error loading JDBC driver", e);
		}
	}

	private H2ConceptFactory factory;

	public H2Lattice(File location, String user, String password) {
		super(location, user, password);
	}

	public H2Lattice(File location, String database, String user, String password) {
		super(location, user, password);
	}

	private boolean _add(Connection conn, H2Concept arg0) throws ColattiException, SQLException {
		try (PreparedStatement statement = conn.prepareStatement(H2Queries.INSERT_CONCEPT,
				Statement.RETURN_GENERATED_KEYS)) {
			String objects = itemsToString(arg0.objects().toArray());
			String attributes = itemsToString(arg0.attributes().toArray());
			statement.setString(1, objects);
			statement.setString(2, attributes);
			statement.setInt(3, arg0.attributes().size());
			statement.executeUpdate();
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			int dbId = rs.getInt(1);
			((H2Concept) arg0).setDbId(dbId);
			return true;
		}
	}

	/**
	 * Concept: - objects are supposed to be PortPair Ids. - attributes are
	 * supposed to be Features Id or Datanode relations.
	 * 
	 * Existing Concepts cannot be added. In other words, Concept cannot be an
	 * instance of H2Concept.
	 */
	@Override
	public boolean add(Concept arg0) throws ColattiException {
		if (!(arg0 instanceof H2Concept)) {
			throw new ColattiException("Must be an instance of H2Concept");
		}
		if (((H2Concept) arg0).getDbId() != 0) {
			l.warn("H2Concept has a non-zero dbId: {}", ((H2Concept) arg0).getDbId());
			return false;
		}

		try (Connection conn = getConnection()) {
			return _add(conn, (H2Concept) arg0);
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	private void _addChildren(Connection conn, Concept concept, Concept... children) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement(H2Queries.INSERT_PARENT_CHILD)) {
			int conceptId = ((H2Concept) concept).getDbId();
			for (Concept c : children) {
				int childId = ((H2Concept) c).getDbId();
				statement.setInt(1, conceptId);
				statement.setInt(2, childId);
				statement.executeUpdate();
			}
		}
	}

	@Override
	public void addChildren(Concept concept, Concept... children) throws ColattiException {
		if (!validNonZero(concept) || !validNonZero(children)) {
			throw new ColattiException("Invalid concept(s)");
		}
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try {
				_addChildren(conn, concept, children);
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	private boolean validNonZero(Concept... arg) {
		for (Concept a : arg) {
			if (!(a instanceof H2Concept)) {
				l.error("Not a H2Concept: {}", a);
				return false;
			}

			if (!(((H2Concept) a).getDbId() > 0)) {
				l.error("Not a valid dbId: {}", ((H2Concept) a).getDbId());
				return false;
			}
		}
		return true;
	}

	@Override
	public void addParents(Concept concept, Concept... parents) throws ColattiException {
		if (!validNonZero(concept) || !validNonZero(parents)) {
			throw new ColattiException("Invalid concept(s)");
		}
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			try {
				_addParents(conn, concept, parents);
				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	private void _addParents(Connection conn, Concept concept, Concept[] parents) throws SQLException {
		try (PreparedStatement statement = conn.prepareStatement(H2Queries.INSERT_PARENT_CHILD)) {
			int conceptId = ((H2Concept) concept).getDbId();
			for (Concept c : parents) {
				int parentId = ((H2Concept) c).getDbId();
				statement.setInt(1, parentId);
				statement.setInt(2, conceptId);
				statement.executeUpdate();
			}
		}
	}

	private Set<Concept> toConcepts(ResultSet rs) throws SQLException {
		Set<Concept> cs = new HashSet<Concept>();
		while (rs.next()) {
			int id = rs.getInt(1);
			String o = rs.getString(2);
			String a = rs.getString(3);
			H2Concept concept = new H2ConceptReadOnly(id, stringToItems(o), stringToItems(a));
			cs.add(concept);
		}
		return Collections.unmodifiableSet(cs);
	}

	@Override
	public Set<Concept> getConceptsWithAttributesSize(int x) throws ColattiException {
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_CONCEPTS_WITH_ATTRIBUTES_SIZE)) {
			statement.setInt(1, x);
			return toConcepts(statement.executeQuery());
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public Set<Concept> children(Concept arg0) throws ColattiException {
		if (!validNonZero(arg0)) {
			throw new ColattiException("Not a valid concept: " + arg0);
		}
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_CHILDREN)) {
			statement.setInt(1, (((H2Concept) arg0).getDbId()));
			return toConcepts(statement.executeQuery());
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public int size() throws ColattiException {
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_CONCEPT_SIZE)) {
			ResultSet rs = statement.executeQuery();
			rs.next();
			int size = rs.getInt(1);
			return size;
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public boolean isParentOf(Concept arg0, Concept arg1) throws ColattiException {
		if (!validNonZero(arg0, arg1)) {
			throw new ColattiException("Invalid concepts");
		}
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_CONCEPT_IS_PARENT)) {
			statement.setInt(1, (((H2Concept) arg0).getDbId()));
			statement.setInt(2, (((H2Concept) arg1).getDbId()));
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
		return false;
	}

	@Override
	public int maxAttributeCardinality() throws ColattiException {
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_MAX_ATTRIBUTES_CARDINALITY)) {
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				int max = rs.getInt(1);
				return max;
			}
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
		;
		return 0;
	}

	@Override
	public Set<Concept> parents(Concept arg0) throws ColattiException {
		if (!validNonZero(arg0)) {
			throw new ColattiException("Not a valid concept: " + arg0);
		}
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_PARENTS)) {
			statement.setInt(1, (((H2Concept) arg0).getDbId()));
			return toConcepts(statement.executeQuery());
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public boolean removeChild(Concept concept, Concept child) throws ColattiException {
		if (!validNonZero(concept, child)) {
			throw new ColattiException("Not valid concept(s)");
		}
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.DELETE_PARENT_CHILD)) {
			statement.setInt(1, (((H2Concept) concept).getDbId()));
			statement.setInt(2, (((H2Concept) child).getDbId()));
			int rowCount = statement.executeUpdate();
			if (rowCount > 0) {
				return true;
			} else {
				return false;
			}
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public boolean removeParent(Concept concept, Concept parent) throws ColattiException {
		if (!validNonZero(concept, parent)) {
			throw new ColattiException("Not valid concept(s)");
		}
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.DELETE_PARENT_CHILD)) {
			statement.setInt(1, (((H2Concept) parent).getDbId()));
			statement.setInt(2, (((H2Concept) concept).getDbId()));
			int rowCount = statement.executeUpdate();
			if (rowCount > 0) {
				return true;
			} else {
				return false;
			}
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public void replace(Concept that, Concept with) throws ColattiException {
		if (!validNonZero(that)) {
			throw new ColattiException("'that' concept is not in the list");
		}
		if (!(with instanceof H2Concept)) {
			throw new ColattiException("Not a H2Concept");
		}

		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);

			try {
				// Add the new concept
				_add(conn, (H2Concept) with);

				// Get parents and children of that
				Set<Concept> parents = parents(that);
				Set<Concept> children = children(that);

				_addParents(conn, with, parents.toArray(new Concept[parents.size()]));
				_addChildren(conn, with, children.toArray(new Concept[children.size()]));

				try (PreparedStatement statement = conn.prepareStatement(H2Queries.DELETE_CONCEPT)) {
					statement.setInt(1, (((H2Concept) that).getDbId()));
					statement.executeUpdate();
				}
			} catch (SQLException e) {
				conn.rollback();
				l.error("", e);
				throw (e);
			}
			conn.commit();
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public Concept supremum() throws ColattiException {
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_SUPREMUM)) {
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				String objects = rs.getString(2);
				String attributes = rs.getString(3);
				return new H2ConceptReadOnly(rs.getInt(1), stringToItems(objects), stringToItems(attributes));
			} else
				throw new ColattiException("Invalid state.");
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	@Override
	public Concept infimum() throws ColattiException {
		try (Connection conn = getConnection();
				PreparedStatement statement = conn.prepareStatement(H2Queries.SELECT_INFIMUM)) {
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				String objects = rs.getString(2);
				String attributes = rs.getString(3);
				return new H2ConceptReadOnly(rs.getInt(1), stringToItems(objects), stringToItems(attributes));
			} else
				throw new ColattiException("Invalid state.");
		} catch (IOException | SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new ColattiException(e);
		}
	}

	private static final Gson gson = new Gson();

	private static final String itemsToString(Object[] items) throws ColattiException {
		JsonArray array = new JsonArray();
		for (Object i : items) {
			if (!(i instanceof String)) {
				throw new ColattiException("objects or attributes must be of type String");
			}
			array.add(i.toString());
		}
		return gson.toJson(array);
	}

	private static final String[] stringToItems(String items) {
		JsonArray array = (JsonArray) new JsonParser().parse(items);
		String[] o = new String[array.size()];
		for (int x = 0; x < array.size(); x++) {
			o[x] = array.get(x).getAsString();
		}
		return o;
	}

	@Override
	protected void setup() {
		factory = new H2ConceptFactory();
		try (Connection conn = getConnection()) {
			conn.setAutoCommit(false);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_CONCEPT);
			conn.createStatement().execute(H2Queries.CREATE_TABLE_LATTICE);
			conn.commit();
			conn.setAutoCommit(true);

			// Insert empty concept
			if (size() == 0) {
				_add(conn, factory.empty());
			}
		} catch (IOException | SQLException | ColattiException e) {
			l.error("SQL Exception", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public ConceptFactory getConceptFactory() {
		return factory;
	}

	private class H2Concept implements Concept {
		private int dbId;
		private Set<Object> objects;
		private Set<Object> attributes;
		private int hashCode;

		public H2Concept(Object[] objects, Object[] attributes) {
			this.dbId = 0;
			this.objects = new HashSet<Object>(Arrays.asList(objects));
			this.attributes = new HashSet<Object>(Arrays.asList(attributes));
			this.hashCode = new HashCodeBuilder().append(objects).append(attributes).toHashCode();
		}

		void setDbId(int dbId) {
			this.dbId = dbId;
		}

		public int getDbId() {
			return dbId;
		}

		@Override
		public Set<Object> objects() {
			return Collections.unmodifiableSet(objects);
		}

		@Override
		public Set<Object> attributes() {
			return attributes;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof H2Concept) && ((Concept) obj).attributes().equals(attributes)
					&& ((Concept) obj).objects().equals(objects);
		}

		public String toString() {
			return new StringBuilder().append(dbId).append('/').append(objects).append('/').append(attributes)
					.toString();
		}

		public int hashCode() {
			return hashCode;
		}
	}

	private class H2ConceptReadOnly extends H2Concept {
		public H2ConceptReadOnly(int dbId, Object[] objects, Object[] attributes) {
			super(objects, attributes);
			super.setDbId(dbId);
		}

		@Override
		void setDbId(int dbId) {
			throw new RuntimeException("Read only object!");
		}
	}

	/**
	 * This class reused the getConnection() method from the containing class.
	 */
	class H2ConceptFactory implements ConceptFactory {

		@Override
		public H2Concept make(Collection<Object> objects, Collection<Object> attributes) {
			return make(objects.toArray(), attributes.toArray());
		}

		@Override
		public H2Concept make(Object[] objects, Object[] attributes) {
			return new H2Concept(objects, attributes);
		}

		@Override
		public H2Concept empty() {
			return new H2Concept(new Object[0], new Object[0]);
		}

		@Override
		public H2Concept makeFromSingleObject(Object object, Object... attributes) {
			return new H2Concept(new Object[] { object }, attributes);
		}

		@Override
		public H2Concept makeAddAttributes(Concept concept, Object... attributesToAdd) {
			return new H2Concept(concept.objects().toArray(),
					CollectionUtils.union(concept.attributes(), Arrays.asList(attributesToAdd)).toArray());
		}

		@Override
		public H2Concept makeAddObject(Concept concept, Object object) {
			return new H2Concept(
					CollectionUtils.union(concept.objects(), Arrays.asList(new Object[] { object })).toArray(),
					concept.attributes().toArray());
		}

		@Override
		public H2Concept makeJoinAttributes(Concept concept, Object[]... attributeSetsToJoin) {
			Set<Object> attributes = new HashSet<Object>();
			attributes.addAll(concept.attributes());
			for (Object[] attributesToAdd : attributeSetsToJoin) {
				attributes.addAll(Arrays.asList(attributesToAdd));
			}
			return new H2Concept(concept.objects().toArray(), attributes.toArray());
		}

	}
}
