package dinowolf.database;

import java.io.File;

public class DatabaseManagerFactory {
	private DatabaseManagerFactory() {
	}

	public final static DatabaseManager getManager(final File home) {
		
		return new DatabaseManagerImpl(home);
	}
}
