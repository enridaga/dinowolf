package dinowolf.database;



import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.update.UpdateAction;

import dinowolf.database.repository.FileRepository;
import dinowolf.database.repository.Repository;
import dinowolf.database.schema.QueryHelper;
import dinowolf.database.schema.SparqlQueries;
import dinowolf.features.FeatureSet;

class DatabaseManagerImpl implements DatabaseManager {
	private File home;
	private Repository repository;
	private Dataset dataset;

	public DatabaseManagerImpl(File home) {
		this.home = home;
		this.home.mkdirs();
		this.repository = new FileRepository(new File(home, "repository"));
		this.dataset = TDBFactory.createDataset(Location.create(new File(home, "metadata").getAbsolutePath()));
	}

	@Override
	public synchronized FeatureSet getFeatures() {
		String q = "SELECT ?FromTo ?Feature FROM <urn:x-dinowolf:graph:features> WHERE { [] a <urn:x-dinowolf:type:FeatureMap> ; <urn:x-dinowolf:property:fromto> ?FromTo ; <urn:x-dinowolf:property:feature> ?Feature }";
		QueryExecution qe = QueryExecutionFactory.create(q, dataset);
		return null;
	}

	@Override
	public synchronized FeatureSet getFeatures(String bundleUri) {
		QuerySolutionMap bindings = new QuerySolutionMap();
		bindings.add("bundleId", ResourceFactory.createResource(bundleUri));
		QueryExecution qe = QueryExecutionFactory.create(SparqlQueries.ListFeaturesOfBundle, dataset, bindings);
		return null;
	}

	@Override
	public synchronized void put(FeatureSet featureSet) {
		UpdateAction.parseExecute("", dataset);
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
	public synchronized void put(WorkflowBundle bundle,String repositoryId, boolean force) throws IOException {
		repository.put(bundle, repositoryId, force);
	}
}
