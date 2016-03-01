package dinowolf.features;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.InOut;
import dinowolf.annotation.InOutImpl;

public class InOutExtractor {
	private static final Logger l = LoggerFactory.getLogger(InOutExtractor.class);
	private Set<InOut> inOut = new HashSet<InOut>();
	private Features F = new Features();

	public InOutExtractor(WorkflowBundle wb) {
		for (Workflow w : wb.getWorkflows())
			for (Processor p : w.getProcessors()) {
				for (InputProcessorPort i : p.getInputPorts()) {
					for (OutputProcessorPort o : p.getOutputPorts()) {
						InOut io = new InOutImpl(w, p, i, o);
						l.trace("{}", io);
						F.setup(io);
						inOut.add(io);
					}
				}
			}
	}

	public Set<InOut> getSet() {
		return Collections.unmodifiableSet(inOut);
	}
}
