package dinowolf.database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datanode.dsl.DNGraph;
import dinowolf.database.features.FeaturesDatabase;
import dinowolf.database.h2.AnnotationsDatabase;
import dinowolf.database.h2.AnnotationsLoggerH2;
import dinowolf.database.h2.AnnotationsWalker;
import dinowolf.database.h2.FeaturesDatabaseH2;
import dinowolf.database.h2.FeaturesDatabaseH2.FeatureH2;
import dinowolf.database.h2.LogWalker;
import dinowolf.database.repository.FileRepository;
import dinowolf.database.repository.Repository;
import dinowolf.features.Feature;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesMap;
import enridaga.colatti.ColattiException;
import enridaga.colatti.InsertObject;
import enridaga.colatti.Lattice;
import enridaga.colatti.LatticeInMemory;
import enridaga.colatti.Rule;
import enridaga.colatti.Rules;

class DatabaseManagerImpl implements DatabaseManager {
	private File home;
	private Repository repository;
	private FeaturesDatabase features;
	private Lattice lattice;
	private AnnotationsDatabase annotations;

	private static final Logger l = LoggerFactory.getLogger(DatabaseManagerImpl.class);

	public DatabaseManagerImpl(File home) throws IOException {
		l.trace("Opening instance on folder {}", home);
		this.home = home;
		this.home.mkdirs();
		this.repository = new FileRepository(new File(home, "repository"));
		// Features, lattice and logs are in the same database
		this.features = new FeaturesDatabaseH2(new File(home, "metadata"));
		this.lattice = new LatticeInMemory();
		this.annotations = new AnnotationsLoggerH2(new File(home, "metadata"));
		initLattice();
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
	public Map<String,FeatureSet> getFeatures(String bundleId) throws IOException {
		return getFeatures(bundleId);
	}

	@Override
	public FeaturesMap getFeatures(String bundleId, WorkflowBundle bundle) throws IOException {
		return features.getFeatures(bundleId, bundle);
	}

	@Override
	public void put(String bundleUri, FeaturesMap featureSet) throws IOException {
		features.put(bundleUri, featureSet);
	}

	@Override
	public String put(File wfbundle, boolean override) throws IOException {
		l.trace("put file {}", wfbundle);
		return repository.put(wfbundle, override);
	}

	@Override
	public FeatureSet getFeatures(String bundleId, String portPair) throws IOException {
		return features.getFeatures(bundleId, portPair);
	}

	@Override
	public List<Rule> recommend(String bundleId, String portPairName) throws IOException {
		Rules rules = new Rules(lattice);
		Object[] inHead = DNGraph.relations; // All datanode relations
		FeatureSet set = features.getFeatures(bundleId, portPairName);

		List<Object> features = new ArrayList<Object>();
		for (Feature f : set) {
			String feature = featureToAttribute((FeatureH2) f);
			// l.debug("{} >> {}", f, feature);
			features.add(feature);
		}
		try {
			// l.debug("{} ", features);
			return Arrays.asList(rules.rules(inHead, features.toArray()));
		} catch (ColattiException e) {
			throw new IOException(e);
		}
	}

	public static final String featureToAttribute(FeatureH2 feature) {
		return new StringBuilder().append('f').append(':').append(feature.getDbId()).toString();
	}

	public static final Set<String> featuresToAttributes(FeatureSet set) {
		Set<String> ss = new HashSet<String>();
		for (Feature f : set) {
			ss.add(featureToAttribute((FeatureH2) f));
		}
		return ss;
	}

	public static final boolean attributeIsFeature(String attribute) {
		return attribute.charAt(0) == 'f';
	}

	@Override
	public void annotate(String bundleId, String portPairName, List<String> annotations, List<Rule> recommended, int duration)
			throws IOException {
		if (insertInLattice(bundleId, portPairName, annotations)) {
			this.annotations.annotate(bundleId, portPairName, annotations, recommended, duration);
		}
	}

	private boolean insertInLattice(String bundleId, String portPairName, List<String> annotations) throws IOException {
		InsertObject insert = new InsertObject(lattice);
		boolean valid;
		try {
			Set<String> attributes = featuresToAttributes(features.getFeatures(bundleId, portPairName));
			attributes.addAll(annotations);
			if (l.isTraceEnabled()) {
				l.trace("{} {} {}", bundleId, portPairName, attributes.toArray());
			}
			valid = insert.perform(portPairName, attributes.toArray());
		} catch (ColattiException e) {
			throw new IOException(e);
		}

		return valid;
	}

	private void initLattice() throws IOException {

		// Load each element in the lattice
		annotations.walk(new AnnotationsWalker() {

			@Override
			public boolean read(String bundleId, String portPairName, List<String> annotations) throws IOException {
				l.info(" <init> {} {} {} ", bundleId, portPairName, annotations);
				insertInLattice(bundleId, portPairName, annotations);
				return true;
			}
		});
	}

	@Override
	public void skipAnnotations(String bundleId, String portPairName, List<Rule> recommended, int duration) throws IOException {
		// Don't add to lattice and register item has been skipped
		annotations.skipAnnotations(bundleId, portPairName, recommended, duration);
	}

	@Override
	public void noAnnotations(String bundleId, String portPairName, List<Rule> recommended, int duration) throws IOException {
		// Add to lattice without annotations
		InsertObject insert = new InsertObject(lattice);

		boolean valid;
		try {
			valid = insert.perform(portPairName,
					featuresToAttributes(features.getFeatures(bundleId, portPairName)).toArray());
		} catch (ColattiException e) {
			throw new IOException(e);
		}
		if (valid) {
			annotations.noAnnotations(bundleId, portPairName, recommended, duration);
		}
	}

	@Override
	public void walk(AnnotationsWalker walker) throws IOException {
		annotations.walk(walker);
	}
	
	@Override
	public void walk(LogWalker walker) throws IOException {
		annotations.walk(walker);
	}

	@Override
	public List<String> annotations(String portPairName) throws IOException {
		return annotations.annotations(portPairName);
	}
	
	@Override
	public boolean annotated(String portPairName) throws IOException {
		return annotations.annotated(portPairName);
	}

	@Override
	public Lattice lattice() {
		return lattice;
	}

	@Override
	public List<String> annotating() throws IOException {
		return annotations.annotating();
	}

	@Override
	public List<String> neverAnnotated() throws IOException {
		return annotations.neverAnnotated();
	}
	@Override
	public Map<String, Integer> progress() throws IOException {
		return annotations.progress();
	}

	@Override
	public Map<String, List<String>> bundleAnnotations(String bundleId) throws IOException {
		return annotations.bundleAnnotations(bundleId);
	}
	
	@Override
	public FeatureSet getFeatures(Integer[] dbIds) throws IOException {
		return features.getFeatures(dbIds);
	}
}
