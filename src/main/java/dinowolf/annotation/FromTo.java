package dinowolf.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.Port;

import dinowolf.features.Feature;

public interface FromTo {
	
	public Port from();
	
	public String roleFrom();

	public Port to();

	public String roleTo();

	public Processor processor();

	public Workflow workflow();

	public Set<Feature> features();

	public Set<String> annotations();

	public void addFeature(Feature feature);

	public void annotate(String... datanodeRelations);
}
