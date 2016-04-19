package dinowolf.database.schema;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import dinowolf.features.Feature;

public class QueryHelper {

	public final static Resource toResource(WorkflowBundle bundle){
		return ResourceFactory.createResource(bundle.getIdentifier().toString());
	}
	
	public final static Resource toResource(Feature feature){
		return ResourceFactory.createResource(Vocabulary.FEATURE + feature.getId());
	}
}
