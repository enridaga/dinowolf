package dinowolf.annotation;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.Port;

public interface FromTo {
	
	enum FromToType{
		II,OO,IO,OI
	}
	public String getId();
	
	public Port from();
	
	/**
	 * Is it Input or Output?
	 * @return
	 */
	public String roleFrom();

	public Port to();

	public FromToType getType();
	
	/**
	 * Is it Input or Output?
	 * @return
	 */
	public String roleTo();

	public Processor processor();

	public String shortName();
	
	public Workflow workflow();

//	public Set<Feature> features();

//	public Set<String> annotations();

//	public void addFeature(Feature feature);

//	public void annotate(String... datanodeRelations);
}
