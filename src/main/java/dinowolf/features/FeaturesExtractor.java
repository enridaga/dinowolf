package dinowolf.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import dinowolf.annotation.FromTo;

public class FeaturesExtractor {
	private static final Logger l = LoggerFactory.getLogger(FeaturesExtractor.class);
	private Features F = new Features();

	public FeaturesExtractor() {

	}

	public Set<Feature> extract(FromTo inOut) {
		Set<Feature> features = new HashSet<Feature>();
		// Processor
		features.add(F.ProcessorType(inOut.processor().getType().toString()));
		features.add(F.ProcessorName(inOut.processor().getName()));
		// Configuration of this processor
		// ...

		// Focus ports
		features.add(F.FromPort(inOut.from().getName()));
		features.add(F.ToPort(inOut.to().getName()));
		// Roles
		features.add(F.FromToRoles(inOut.roleFrom() + inOut.roleTo()));

		// Other ports
		for (InputProcessorPort in : inOut.processor().getInputPorts())
			if (!in.getName().equals(inOut.from().getName()))
				features.add(F.InputPortName(in.getName()));

		for (OutputProcessorPort out : inOut.processor().getOutputPorts())
			if (!out.getName().equals(inOut.to().getName()))
				features.add(F.OutputPortName(out.getName()));

		// Activities
		for (Profile p : inOut.workflow().getParent().getProfiles()) {
			features.add(F.ActivityType(inOut.processor().getActivity(p).getType().toString()));
			features.add(F.ActivityName(inOut.processor().getActivity(p).getName()));

			// Configuration of activities of this processor
			try {
				Configuration c = inOut.processor().getActivityConfiguration(p);
				features.addAll(jsonToFeature(inOut, c.getJson(), new ArrayList<String>()));
			} catch (Exception e) {
				// No configuration
			}
		}
		return features;
	}

	private Set<Feature> jsonToFeature(FromTo inOut, JsonNode json, List<String> path) {
		Set<Feature> features = new HashSet<Feature>();
		Iterator<String> fields = json.fieldNames();
		while (fields.hasNext()) {
			List<String> branch = new ArrayList<String>();
			branch.addAll(path);
			String field = fields.next();
			branch.add(field);
			features.add(F.ActivityConfField(branch));
			JsonNode node = json.get(field);
			if (node.isValueNode()) {
				features.add(F.ActivityConfFieldValue(branch, node.asText()));
			} else {
				jsonToFeature(inOut, node, branch);
			}
		}
		return features;
	}
}
