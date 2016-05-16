package dinowolf.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import dinowolf.annotation.FromTo;
import dinowolf.io.AnnotationHelper;

class FeaturesExtractor {
	private static final Logger l = LoggerFactory.getLogger(FeaturesExtractor.class);
	private FeatureCatalogue F = new FeatureCatalogue();

	public FeaturesExtractor() {

	}

	private void safeAdd(FeatureHashSet features, Feature feature){
		if(feature != null){
			features.add(feature);
		}
	}
	public FeatureHashSet extract(FromTo inOut) {
		AnnotationHelper anno;
		try {
			anno = new AnnotationHelper(inOut.processor().getParent().getParent());
		} catch (IOException e1) {
			l.error("", e1);
			throw new RuntimeException("Cannot access bundle annotations!", e1);
		}

		FeatureHashSet features = new FeatureHashSet();

		// Processor
		features.add(F.ProcessorType(inOut.processor().getType().toString()));
		features.add(F.ProcessorName(inOut.processor().getName()));
		safeAdd(features, F.ProcessorTitle(anno.getTitle(inOut.processor())));
		safeAdd(features, F.ProcessorDescription(anno.getDescription(inOut.processor())));

		// From
		features.add(F.FromPortName(inOut.from().getName()));
		safeAdd(features, F.FromPortTitle(anno.getTitle(inOut.from())));
		safeAdd(features, F.FromPortDescription(anno.getDescription(inOut.from())));

		// To
		features.add(F.ToPortName(inOut.to().getName()));
		safeAdd(features, F.ToPortTitle(anno.getTitle(inOut.to())));
		safeAdd(features, F.ToPortDescription(anno.getDescription(inOut.to())));

		// Roles
		features.add(F.FromToType(inOut.roleFrom() + inOut.roleTo()));

		// Other ports
		for (InputProcessorPort in : inOut.processor().getInputPorts()) {
			if (!in.equals(inOut.from()) && !in.equals(inOut.to())) {
				features.add(F.OtherInputPortName(in.getName()));
				safeAdd(features, F.OtherInputPortTitle(anno.getTitle(in)));
				safeAdd(features, F.OtherInputPortDescription(anno.getDescription(in)));
			}
		}
		for (OutputProcessorPort out : inOut.processor().getOutputPorts()) {
			if (!out.equals(inOut.from()) && !out.equals(inOut.to())) {
				features.add(F.OtherOutputPortName(out.getName()));
				safeAdd(features, F.OtherOutputPortTitle(anno.getTitle(out)));
				safeAdd(features, F.OtherOutputPortDescription(anno.getDescription(out)));
			}
		}
		// Profile dependant features
		for (Profile p : inOut.workflow().getParent().getProfiles()) {
			// Profile annotations? TODO
			
			// Configuration of the processor for this profile
			for(Configuration c: inOut.processor().getConfigurations(p)){
				features.addAll(jsonToFeature(FeatureDepth.Processor, inOut, c.getJson(), new ArrayList<String>()));
			}
			
			// Activities
			features.add(F.ActivityType(inOut.processor().getActivity(p).getType().toString()));
			features.add(F.ActivityName(inOut.processor().getActivity(p).getName()));
			safeAdd(features, F.ActivityTitle(anno.getTitle(inOut.processor().getActivity(p))));
			safeAdd(features, F.ActivityDescription(anno.getDescription(inOut.processor().getActivity(p))));

			// Configuration of activities of this processor
			try {
				Configuration c = inOut.processor().getActivityConfiguration(p);
				features.addAll(jsonToFeature(FeatureDepth.Activity, inOut, c.getJson(), new ArrayList<String>()));
			} catch (Exception e) {
				// No configuration
			}
		}
		l.trace("{} features extracted", features.size());
		return features;
	}

	private Set<Feature> jsonToFeature(FeatureDepth depth, FromTo inOut, JsonNode json, List<String> path) {
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
				features.add(F.ConfFieldValue(depth, branch, node.asText()));
			} else {
				jsonToFeature(depth, inOut, node, branch);
			}
		}
		return features;
	}
}
