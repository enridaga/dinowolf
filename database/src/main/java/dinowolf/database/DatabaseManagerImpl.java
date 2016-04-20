package dinowolf.database;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import dinowolf.database.features.FeaturesDatabaseTDB;
import dinowolf.database.repository.FileRepository;
import dinowolf.database.repository.Repository;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;

class DatabaseManagerImpl implements DatabaseManager {
	private File home;
	private Repository repository;
	private FeaturesDatabaseTDB features;

	public DatabaseManagerImpl(File home) {
		this.home = home;
		this.home.mkdirs();
		this.repository = new FileRepository(new File(home, "repository"));
		this.features = new FeaturesDatabaseTDB(new File(home, "metadata"));
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
}
