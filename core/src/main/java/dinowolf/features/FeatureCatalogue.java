package dinowolf.features;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Factory methods will return null if any of the passed values are null
 */
public final class FeatureCatalogue {
	private Feature __f(String name, String value, FeatureDepth level, boolean t) {
		if (name == null || value == null)
			return null;
		return new FeatureImpl(name, value, level, t);
	}

	private Feature __f(String name, String value, FeatureDepth level) {
		if (name == null || value == null)
			return null;
		return new FeatureImpl(name, value, level);
	}

	@SuppressWarnings("unused")
	private Feature __workflow(String name, String value) {
		return __f(name, value, FeatureDepth.Workflow);
	}

	private Feature __processor(String name, String value) {
		return __f(name, value, FeatureDepth.Processor);
	}

	@SuppressWarnings("unused")
	private Feature __otherport(String name, String value) {
		return __f(name, value, FeatureDepth.OtherPort);
	}

	@SuppressWarnings("unused")
	private Feature __portpair(String name, String value) {
		return __f(name, value, FeatureDepth.FromToPorts);
	}

	@SuppressWarnings("unused")
	private Feature __workflow(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.Workflow, t);
	}

	private Feature __processor(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.Processor, t);
	}

	private Feature __activity(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.Activity, t);
	}

	private Feature __activity(String name, String value) {
		return __f(name, value, FeatureDepth.Activity);
	}

	private Feature __otherport(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.OtherPort, t);
	}

	private Feature __portpair(String name, String value, boolean t) {
		return __f(name, value, FeatureDepth.FromToPorts, t);
	}

	private Feature __annotation(String property, String value, FeatureDepth ofwhat, boolean tokenizable) {
		return __f(property, value, ofwhat, tokenizable);
	}

	/*
	 * CATALOGUE
	 * 
	 */

	public Feature ProcessorName(String value) {
		return __processor("ProcessorName", value, true);
	}

	public Feature ProcessorType(String value) {
		return __processor("ProcessorType", value);
	}

	public Feature ActivityName(String value) {
		return __activity("ActivityName", value, true);
	}

	public Feature ActivityType(String value) {
		return __activity("ActivityType", value);
	}

	public Feature ActivityConfField(List<String> path) {
		return __activity("ActivityConfField", StringUtils.join(path.toArray(new String[path.size()]), "/"));
	}

	public Feature ConfFieldValue(FeatureDepth depth, List<String> path, String value) {
		List<String> l = new ArrayList<String>();
		l.add("ConfField");
		l.addAll(path);
		return __f(StringUtils.join(l, '/'), value, depth);
	}

	public Feature OtherInputPortName(String value) {
		return __otherport("OtherInputPortName", value, true);
	}

	public Feature OtherOutputPortName(String value) {
		return __otherport("OtherOutputPortName", value, true);
	}

	public Feature FromPort(String value) {
		return __portpair("FromPort", value, true);
	}

	public Feature ToPort(String value) {
		return __portpair("ToPort", value, true);
	}

	public Feature FromToType(String value) {
		return __portpair("FromToType", value, true);
	}

	public Feature Annotation(String property, String value, FeatureDepth ofwhat, boolean tokenizable) {
		return __annotation(property, value, ofwhat, tokenizable);
	}

	public Feature WorkflowTitle(String value) {
		return Annotation("Title", value, FeatureDepth.Workflow, true);
	}

	public Feature WorkflowDescription(String value) {
		return Annotation("Description", value, FeatureDepth.Workflow, true);
	}

	public Feature ProcessorTitle(String value) {
		return Annotation("ProcessorTitle", value, FeatureDepth.Processor, true);
	}

	public Feature ProcessorDescription(String value) {
		return Annotation("ProcessorDescription", value, FeatureDepth.Processor, true);
	}

	public Feature ActivityTitle(String value) {
		return Annotation("ActivityTitle", value, FeatureDepth.Activity, true);
	}

	public Feature ActivityDescription(String value) {
		return Annotation("ActivityDescription", value, FeatureDepth.Activity, true);
	}

	public Feature OtherPortTitle(String value) {
		return Annotation("OtherPortTitle", value, FeatureDepth.OtherPort, true);
	}

	public Feature OtherPortDescription(String value) {
		return Annotation("OtherPortDescription", value, FeatureDepth.OtherPort, true);
	}

	public Feature FromPortTitle(String value) {
		return Annotation("FromPortTitle", value, FeatureDepth.From, true);
	}

	public Feature FromPortDescription(String value) {
		return Annotation("FromPortDescription", value, FeatureDepth.From, true);
	}

	public Feature ToPortTitle(String value) {
		return Annotation("ToPortTitle", value, FeatureDepth.To, true);
	}

	public Feature ToPortDescription(String value) {
		return Annotation("ToPortDescription", value, FeatureDepth.To, true);
	}

	public Feature OtherInputPortTitle(String value) {
		return Annotation("OtherInputPortTitle", value, FeatureDepth.OtherPort, true);
	}

	public Feature OtherInputPortDescription(String value) {
		return Annotation("OtherInputPortDescription", value, FeatureDepth.To, true);
	}

	public Feature OtherOutputPortTitle(String value) {
		return Annotation("OtherOutputPortTitle", value, FeatureDepth.OtherPort, true);
	}

	public Feature OtherOutputPortDescription(String value) {
		return Annotation("OtherOutputPortDescription", value, FeatureDepth.To, true);
	}

	public Feature ProcessorConfiguration(String jsonAsString) {
		return __processor("ProcessorConfiguration", jsonAsString, true);
	}

}
