package dinowolf.database;

import java.io.File;
import java.io.IOException;

public class DatabaseManagerFactory {

	public final static DatabaseManager getManager(final File home) throws IOException {
		return new DatabaseManagerImpl(home);
	}
}
