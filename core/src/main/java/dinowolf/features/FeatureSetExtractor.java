package dinowolf.features;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo;
import dinowolf.annotation.FromToImpl;

public class FeatureSet {
	private static final Logger l = LoggerFactory.getLogger(FeatureSet.class);

	private Features F = new Features();
	private FeaturesExtractor E = new FeaturesExtractor();
	private Map<FromTo, Set<Feature>> featuresMap = new HashMap<FromTo, Set<Feature>>();

	public FeatureSet() {

	}

	public void add(WorkflowBundle workflow) {
		for (Workflow w : workflow.getWorkflows()) {
			add(w);
		}
	}

	public void add(Workflow workflow) {
		for (Processor p : workflow.getProcessors()) {
			add(p);
		}
	}

	public void add(Processor processor) {
		Set<Port> allPorts = new HashSet<Port>();
		allPorts.addAll(processor.getInputPorts());
		allPorts.addAll(processor.getOutputPorts());
		for (Port i : allPorts) {
			for (Port o : allPorts) {
				if (i.equals(o))
					continue;
				FromTo io = new FromToImpl(processor.getParent(), processor, i, o);
				l.trace("{}", io);
				Set<Feature> fff = E.extract(io);
				featuresMap.put(io, fff);
			}
		}
	}

	public Set<FromTo> getPortPairs() {
		return Collections.unmodifiableSet(featuresMap.keySet());
	}

	public Set<Feature> getFeatures(FromTo portPair) {
		return Collections.unmodifiableSet(featuresMap.get(portPair));
	}
}
