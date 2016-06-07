package dinowolf.database;

import dinowolf.database.features.FeaturesDatabase;
import dinowolf.database.features.FeaturesRecommender;
import dinowolf.database.h2.Annotations;
import dinowolf.database.repository.Repository;

public interface DatabaseManager extends FeaturesDatabase, Repository, Annotations, FeaturesRecommender {

}
