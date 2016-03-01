package dinowolf.features;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;

import dinowolf.annotation.InOut;

public class Features {
	private Feature __f(String name, String value, boolean t) {
		return new FeatureImpl(name, value, t);
	}

	private Feature __f(String name, String value) {
		return new FeatureImpl(name, value);
	}

	public Feature ProcessorName(String value) {
		return __f("ProcessorName", value, true);
	}

	public Feature ProcessorType(String value) {
		return __f("ProcessorType", value);
	}

	public Feature ActivityName(String value) {
		return __f("ActivityName", value, true);
	}

	public Feature ActivityType(String value) {
		return __f("ActivityType", value);
	}

	public Feature ActivityConfField(List<String> path) {
		return __f("ActivityConfField", StringUtils.join(path.toArray(new String[path.size()]), "/"));
	}

	public Feature ActivityConfFieldValue(List<String> path, String value) {
		List<String> l = new ArrayList<String>();
		l.add("ActivityConfField");
		l.addAll(path);
		return __f(StringUtils.join(l, '/'), value);
	}

	public Feature InputPortName(String value) {
		return __f("InputPortName", value, true);
	}

	public Feature OutputPortName(String value) {
		return __f("OutputPortName", value, true);
	}

	public void setup(InOut inOut) {
		// Processor
		inOut.addFeature(ProcessorType(inOut.processor().getType().toString()));
		inOut.addFeature(ProcessorName(inOut.processor().getName()));
		// Configuration of this processor
		// ...

		// Ports
		inOut.addFeature(InputPortName(inOut.from().getName()));
		inOut.addFeature(OutputPortName(inOut.to().getName()));

		// Activities
		for (Profile p : inOut.workflow().getParent().getProfiles()) {
			inOut.addFeature(ActivityType(inOut.processor().getActivity(p).getType().toString()));
			inOut.addFeature(ActivityName(inOut.processor().getActivity(p).getName()));

			// Configuration of activities of this processor
			try {
				Configuration c = inOut.processor().getActivityConfiguration(p);
				jsonToFeature(inOut, c.getJson(), new ArrayList<String>());
			} catch (Exception e) {
				// No configuration
			}
		}
	}

	private void jsonToFeature(InOut inOut, JsonNode json, List<String> path) {
		Iterator<String> fields = json.fieldNames();
		while (fields.hasNext()) {
			List<String> branch = new ArrayList<String>();
			branch.addAll(path);
			String field = fields.next();
			branch.add(field);
			inOut.addFeature(ActivityConfField(branch));
			JsonNode node = json.get(field);
			if (node.isValueNode()) {
				inOut.addFeature(ActivityConfFieldValue(branch, node.asText()));
			} else {
				jsonToFeature(inOut, node, branch);
			}
		}
	}
}
