package dinowolf.database.features;

import java.io.IOException;
import java.util.List;

import enridaga.colatti.Lattice;
import enridaga.colatti.Rule;

public interface FeaturesRecommender {

	public List<Rule> recommend(String bundleId, String portPairName) throws IOException;
	
	public Lattice lattice();
}
