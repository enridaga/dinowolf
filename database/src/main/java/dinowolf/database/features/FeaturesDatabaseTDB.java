package dinowolf.database.features;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.update.UpdateAction;

import dinowolf.database.schema.QueryHelper;
import dinowolf.database.schema.SparqlQueries;
import dinowolf.features.Feature;
import dinowolf.features.FeatureHashSet;
import dinowolf.features.FeatureImpl;
import dinowolf.features.FeatureLevel;
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

	@Override
	public synchronized FeaturesMap getFeatures(String bundleId) {
		String bundleUri = QueryHelper.quoteUri(QueryHelper.toBundleUri(bundleId));
		dataset.begin(ReadWrite.READ);
		QueryExecution qe = QueryExecutionFactory.create(SparqlQueries.ListFromToFeaturesOfBundle.replace("?bundleId", bundleUri), dataset);
		ResultSet rs = qe.execSelect();
		return makeFeaturesMap(rs);
	}

	@Override
	public synchronized void put(String bundleId, FeaturesMap featureSet) throws IOException {
		// open transaction
		try {
			String bundleUri = QueryHelper.quoteUri(QueryHelper.toBundleUri(bundleId));
			dataset.begin(ReadWrite.WRITE);
			// First delete
			UpdateAction.parseExecute(SparqlQueries.DeleteBundleFeatures.replace("?bundleId", bundleUri), dataset);
			for (String portPair : featureSet.getPortPairs()) {
				String fromToUri = QueryHelper.quoteUri(QueryHelper.toPortPairUri(portPair));
				String addBundleFeature_general = SparqlQueries.ADDBundleFeature.replace("?bundleId", bundleUri)
						.replace("?fromTo", fromToUri)
						.replace("?id", QueryHelper.quotedString(portPair));
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
					FeatureLevel.valueOf(qs.get("level").asLiteral().getLexicalForm()),
					qs.get("tokenizable").asLiteral().getLexicalForm().equals("true"));
			fsm.add(f);
		}
		if(dataset.isInTransaction()){
			dataset.end();
		}
		return fsm;
	}

	private FeaturesMap makeFeaturesMap(ResultSet rs) {
		FeaturesHashMap fsm = new FeaturesHashMap();
		while (rs.hasNext()) {
			QuerySolution qs = rs.next();
			Feature f = new FeatureImpl(qs.get("name").asLiteral().getLexicalForm(),
					qs.get("value").asLiteral().getLexicalForm(),
					FeatureLevel.valueOf(qs.get("level").asLiteral().getLexicalForm()),
					qs.get("tokenizable").asLiteral().getLexicalForm().equals("true"));
			String fromTo = qs.get("fromTo").asLiteral().getLexicalForm();
			if (!fsm.containsKey(fromTo)) {
				fsm.put(fromTo, new FeatureHashSet());
			}
			fsm.get(fromTo).add(f);
		}
		if(dataset.isInTransaction()){
			dataset.end();
		}
		return fsm;
	}
}
