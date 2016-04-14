package dinowolf.features;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;

import dinowolf.annotation.FromTo;

public class Features {
	private Feature __f(String name, String value, FeatureLevel level, boolean t) {
		return new FeatureImpl(name, value, level, t);
	}

	private Feature __f(String name, String value, FeatureLevel level) {
		return new FeatureImpl(name, value, level);
	}

	private Feature __w(String name, String value) {
		return __f(name, value, FeatureLevel.Workflow);
	}

	private Feature __p(String name, String value) {
		return __f(name, value, FeatureLevel.Processor);
	}

	private Feature __sp(String name, String value) {
		return __f(name, value, FeatureLevel.SinglePort);
	}

	private Feature __pp(String name, String value) {
		return __f(name, value, FeatureLevel.FromToPorts);
	}

	private Feature __w(String name, String value, boolean t) {
		return __f(name, value, FeatureLevel.Workflow, t);
	}

	private Feature __p(String name, String value, boolean t) {
		return __f(name, value, FeatureLevel.Processor, t);
	}

	private Feature __sp(String name, String value, boolean t) {
		return __f(name, value, FeatureLevel.SinglePort, t);
	}

	private Feature __pp(String name, String value, boolean t) {
		return __f(name, value, FeatureLevel.FromToPorts, t);
	}

	public Feature ProcessorName(String value) {
		return __p("ProcessorName", value, true);
	}

	public Feature ProcessorType(String value) {
		return __p("ProcessorType", value);
	}

	public Feature ActivityName(String value) {
		return __p("ActivityName", value, true);
	}

	public Feature ActivityType(String value) {
		return __p("ActivityType", value);
	}

	public Feature ActivityConfField(List<String> path) {
		return __p("ActivityConfField", StringUtils.join(path.toArray(new String[path.size()]), "/"));
	}

	public Feature ActivityConfFieldValue(List<String> path, String value) {
		List<String> l = new ArrayList<String>();
		l.add("ActivityConfField");
		l.addAll(path);
		return __p(StringUtils.join(l, '/'), value);
	}

	public Feature InputPortName(String value) {
		return __sp("InputPortName", value, true);
	}

	public Feature OutputPortName(String value) {
		return __sp("OutputPortName", value, true);
	}

	public Feature FromPort(String value) {
		return __pp("FromPort", value, true);
	}

	public Feature ToPort(String value) {
		return __pp("ToPort", value, true);
	}

	public Feature FromToRoles(String value) {
		return __pp("FromToRoles", value, true);
	}
}
