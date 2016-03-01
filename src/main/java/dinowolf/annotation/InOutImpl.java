package dinowolf.annotation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.Port;

import dinowolf.features.Feature;

public class InOutImpl implements InOut {
	private Workflow workflow;
	private Processor processor;
	private Port from;
	private Port to;
	private Set<String> annotations;
	private Set<Feature> features;

	public InOutImpl(Workflow w, Processor p, Port from, Port to) {
		this.workflow = w;
		this.processor = p;
		this.from = from;
		this.to = to;
		this.features = new HashSet<Feature>();
		this.annotations = new HashSet<String>();
	}

	@Override
	public Port from() {
		return from;
	}

	@Override
	public Port to() {
		return to;
	}

	@Override
	public Set<String> annotations() {
		return Collections.unmodifiableSet(annotations);
	}

	@Override
	public void annotate(String... rels) {
		annotations.addAll(Arrays.asList(rels));
	}

	@Override
	public Set<Feature> features() {
		return Collections.unmodifiableSet(features);
	}

	@Override
	public void addFeature(Feature feature) {
		features.add(feature);
	}

	@Override
	public Processor processor() {
		return processor;
	}

	@Override
	public Workflow workflow() {
		return workflow;
	}

	public String toString() {
		String[] s = new String[] {
				workflow.getName(),
				processor.getName(),
				from.getName(),
				to.getName()
		};
		return StringUtils.join(s, '/');
	};
}
