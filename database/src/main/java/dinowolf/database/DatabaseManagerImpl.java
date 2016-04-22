package dinowolf.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.features.FeaturesDatabase;
import dinowolf.database.h2.FeaturesDatabaseH2;
import dinowolf.database.repository.FileRepository;
import dinowolf.database.repository.Repository;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;

class DatabaseManagerImpl implements DatabaseManager {
	private File home;
	private Repository repository;
	private FeaturesDatabase features;
	
	private static final Logger l = LoggerFactory.getLogger(DatabaseManagerImpl.class);

	public DatabaseManagerImpl(File home) {
		l.trace("Opening instance on folder {}", home);
		this.home = home;
		this.home.mkdirs();
		this.repository = new FileRepository(new File(home, "repository"));
		this.features = new FeaturesDatabaseH2(new File(home, "metadata"));
	}

	@Override
	public synchronized WorkflowBundle get(String wfId) throws IOException {
		return repository.get(wfId);
	}

	@Override
	public synchronized List<String> list() {
		return repository.list();
	}

	@Override
	public synchronized void put(WorkflowBundle bundle, String repositoryId, boolean force) throws IOException {
		l.trace("put bundle {}", repositoryId);
		repository.put(bundle, repositoryId, force);
	}

	@Override
	public FeatureSet getFeatures() throws IOException {
		return features.getFeatures();
	}

	@Override
	public FeaturesMap getFeatures(String bundleUri) throws IOException {
		return getFeatures(bundleUri);
	}

	@Override
	public void put(String bundleUri, FeaturesMap featureSet) throws IOException {
		put(bundleUri, featureSet);
	}

	@Override
	public String put(File wfbundle, boolean override) throws IOException {
		l.trace("put file {}", wfbundle);
		return repository.put(wfbundle, override);
	}
}
