package dinowolf.annotation;

import java.util.Set;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.Port;

import dinowolf.features.Feature;

public interface InOut {
	public Port from();

	public Port to();

	public Processor processor();

	public Workflow workflow();

	public Set<Feature> features();

	public Set<String> annotations();

	public void addFeature(Feature feature);

	public void annotate(String... datanodeRelations);
}
