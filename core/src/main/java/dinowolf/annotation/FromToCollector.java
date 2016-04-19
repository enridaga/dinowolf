package dinowolf.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FromToCollector {
	private static final Logger l = LoggerFactory.getLogger(FromToCollector.class);

	public List<FromTo> collect(WorkflowBundle bundle) {
		List<FromTo> list = new ArrayList<FromTo>();
		for (Workflow w : bundle.getWorkflows()) {
			for (Processor processor : w.getProcessors()) {
				Set<Port> allPorts = new HashSet<Port>();
				allPorts.addAll(processor.getInputPorts());
				allPorts.addAll(processor.getOutputPorts());
				for (Port i : allPorts) {
					for (Port o : allPorts) {
						if (i.equals(o))
							continue;
						FromTo io = new FromToImpl(processor.getParent(), processor, i, o);
						if(!list.contains(io)){
							l.trace("{}", io);
							list.add(io);
						}
					}
				}
			}
		}
		return list;
	}
}
