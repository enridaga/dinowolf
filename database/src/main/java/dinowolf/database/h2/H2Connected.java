package dinowolf.database.h2;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class H2Connected {
	static private final Logger l = LoggerFactory.getLogger(FeaturesDatabaseH2.class);
	protected String connectionString;
	protected String username;
	protected String password;

	public H2Connected(File location, String user, String password) {
		this(location, "dinowolf", user, password, "MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO");
	}

	public H2Connected(File location, String dbname, String user, String password) {
		this(location, dbname, user, password, "MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO");
	}

	public H2Connected(File location, String dbname, String user, String password, String options) {
		this.connectionString = "jdbc:h2:file:" + location.getAbsolutePath() + "/" + dbname + ";" + options;
		this.username = user;
		this.password = password;

		setup();
	}

	protected Connection getConnection() throws IOException {
		Connection conn;
		try {
			conn = DriverManager.getConnection(connectionString, username, password);
		} catch (SQLException e) {
			l.error("SQL Exception", e.getMessage());
			throw new IOException(e);
		}
		return conn;
	}

	protected abstract void setup();
}