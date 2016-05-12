package dinowolf.database.tdb;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.base.file.Location;
import org.apache.jena.update.UpdateAction;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToCollector;
import dinowolf.database.features.FeaturesDatabase;
import dinowolf.features.Feature;
import dinowolf.features.FeatureDepth;
import dinowolf.features.FeatureHashSet;
import dinowolf.features.FeatureImpl;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeaturesHashMap;
import dinowolf.features.FeaturesMap;

public class FeaturesDatabaseTDB implements FeaturesDatabase {
	private static final Logger l = LoggerFactory.getLogger(FeaturesDatabaseTDB.class);
	private Dataset dataset;

	public FeaturesDatabaseTDB(File location) {
		this.dataset = TDBFactory.createDataset(Location.create(location.toString()));
	}

	@Override
	public synchronized FeatureSet getFeatures() {
		dataset.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create(SparqlQueries.ListAllFeatures, dataset);
		return makeFeatureSet(qe.execSelect());
	}

	public synchronized FeaturesMap getFeatures(String bundleId, WorkflowBundle container) throws IOException {
		String bundleUri = QueryHelper.quoteUri(QueryHelper.toBundleUri(bundleId));
		dataset.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory
				.create(SparqlQueries.ListFromToFeaturesOfBundle.replace("?bundleId", bundleUri), dataset);
		ResultSet rs = qe.execSelect();
		return makeFeaturesMap(rs, container);
	}

	@Override
	public synchronized void put(String bundleId, FeaturesMap featureSet) throws IOException {
		// open transaction
		try {
			String bundleUri = QueryHelper.quoteUri(QueryHelper.toBundleUri(bundleId));
			dataset.begin(ReadWrite.WRITE);
			// First delete
			UpdateAction.parseExecute(SparqlQueries.DeleteBundleFeatures.replace("?bundleId", bundleUri), dataset);
			for (FromTo portPair : featureSet.getPortPairs()) {
				String fromToUri = QueryHelper.quoteUri(QueryHelper.toPortPairUri(portPair.getId()));
				String addBundleFeature_general = SparqlQueries.ADDBundleFeature.replace("?bundleId", bundleUri)
						.replace("?fromTo", fromToUri).replace("?id", QueryHelper.quotedString(portPair.getId()));
				String addFeature_general = SparqlQueries.ADDFeature;

				for (Feature f : featureSet.getFeatures(portPair)) {
					// Put Feature data
					String addFeature = addFeature_general.replace("?feature", QueryHelper.quotedUri(f))
							.replace("?feature", QueryHelper.quotedUri(f));
					addFeature = addFeature.replace("?name", QueryHelper.quotedString(f.getName()))
							.replace("?id", QueryHelper.quotedString(f.getId()))
							.replace("?value", QueryHelper.quotedString(f.getValue()))
							.replace("?level", QueryHelper.quotedString(f.getLevel().name()))
							.replace("?tokenizable", Boolean.toString(f.isTokenizable()));
					l.trace("{}", addFeature);
					UpdateAction.parseExecute(addFeature, dataset);
					String addBundleFeature = addBundleFeature_general.replace("?feature", QueryHelper.quotedUri(f));
					l.trace("{}", addBundleFeature);
					UpdateAction.parseExecute(addBundleFeature, dataset);
				}
			}
			dataset.commit();
		} catch (Exception e) {
			dataset.abort();
			throw new IOException(e);
		} finally {
			dataset.end();
		}
	}

	private FeatureSet makeFeatureSet(ResultSet rs) {
		FeatureHashSet fsm = new FeatureHashSet();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			Feature f = new FeatureImpl(qs.get("name").asLiteral().getLexicalForm(),
					qs.get("value").asLiteral().getLexicalForm(),
					FeatureDepth.valueOf(qs.get("level").asLiteral().getLexicalForm()),
					qs.get("tokenizable").asLiteral().getLexicalForm().equals("true"));
			fsm.add(f);
		}
		if (dataset.isInTransaction()) {
			dataset.end();
		}
		return fsm;
	}

	private static final FromToCollector C = new FromToCollector();

	private FeaturesMap makeFeaturesMap(ResultSet rs, WorkflowBundle bundle) throws IOException {
		Map<String, FromTo> map = C.getMap(bundle);
		FeaturesHashMap fsm = new FeaturesHashMap();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			Feature f = new FeatureImpl(qs.get("name").asLiteral().getLexicalForm(),
					qs.get("value").asLiteral().getLexicalForm(),
					FeatureDepth.valueOf(qs.get("level").asLiteral().getLexicalForm()),
					qs.get("tokenizable").asLiteral().getLexicalForm().equals("true"));
			String fromTo = qs.get("fromTo").asLiteral().getLexicalForm();
			if (!fsm.containsKey(fromTo)) {
				FromTo k = map.get(fromTo);
				if (k == null) {
					throw new IOException("Cannot find port pair in bundle! Id was " + fromTo);
				}
				fsm.put(k, new FeatureHashSet());
			}
			fsm.get(fromTo).add(f);
		}
		if (dataset.isInTransaction()) {
			dataset.end();
		}
		return fsm;
	}
}
