package dinowolf.features;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToImpl;

public class FromToExtractor {
	private static final Logger l = LoggerFactory.getLogger(FromToExtractor.class);
	private Set<FromTo> inOut = new HashSet<FromTo>();
	private Features F = new Features();

	public FromToExtractor(WorkflowBundle wb) {
		for (Workflow w : wb.getWorkflows())
			for (Processor p : w.getProcessors()) {
				Set<Port> allPorts = new HashSet<Port>();
				allPorts.addAll(p.getInputPorts());
				allPorts.addAll(p.getOutputPorts());
				for (Port i : allPorts) {
					for (Port o : allPorts) {
						if (i.equals(o))
							continue;
						FromTo io = new FromToImpl(w, p, i, o);
						l.trace("{}", io);
						F.setup(io);
						inOut.add(io);
					}
				}
			}
	}

	public Set<FromTo> getSet() {
		return Collections.unmodifiableSet(inOut);
	}
}
