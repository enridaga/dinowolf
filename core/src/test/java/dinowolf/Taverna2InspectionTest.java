package dinowolf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.features.Feature;
import dinowolf.features.FeatureSet;
import dinowolf.features.FeatureSetExtractor;
import gonzales.xml.Gonzo;

public class Taverna2InspectionTest {
	private static final Logger l = LoggerFactory.getLogger(Taverna2InspectionTest.class);

	private InputStream __f(String n) {
		return getClass().getClassLoader().getResourceAsStream(n);
	}

	@Ignore
	@Test
	public void wf_16_datalinks() throws Exception {
		Gonzo g = new Gonzo(__f("taverna_wf_16.xml"));
		String source = null;
		String sink = null;
		String processor = null;
		Set<String> pinputs = new HashSet<String>();
		Set<String> poutputs = new HashSet<String>();
		Set<String> pactivities = new HashSet<String>();
		//
		Map<String, String> datanodes = new HashMap<String, String>();
		Map<String, Set<String>> inputs = new HashMap<String, Set<String>>();
		Map<String, Set<String>> activities = new HashMap<String, Set<String>>();
		Map<String, Set<String>> outputs = new HashMap<String, Set<String>>();
		while (g.stroll()) {
			// Processors
			if (g.meets("/:workflow/:dataflow/:processors/:processor/:name")) {
				processor = g.pick();
			} else if (g.meets("/:workflow/:dataflow/:processors/:processor/:inputPorts/:port/:name")) {
				pinputs.add(g.pick());
			} else if (g.meets("/:workflow/:dataflow/:processors/:processor/:outputPorts/:port/:name")) {
				poutputs.add(g.pick());
			} else if (g.meets("/:workflow/:dataflow/:processors/:processor/:activities/:activity/:class")) {
				pactivities.add(g.pick());
			}
			if (g.leaves("/:workflow/:dataflow/:processors/:processor")) {
				inputs.put(processor, pinputs);
				outputs.put(processor, poutputs);
				activities.put(processor, pactivities);
				processor = null;
				pinputs = new HashSet<String>();
				poutputs = new HashSet<String>();
				pactivities = new HashSet<String>();
			}

			// Datalinks
			if (g.meets("/:workflow/:dataflow/:datalinks/:datalink/:sink/:processor")) {
				sink = g.pick();
			} else if (g.meets("/:workflow/:dataflow/:datalinks/:datalink/:sink/:port")) {
				sink += ":" + g.pick();
			} else if (g.meets("/:workflow/:dataflow/:datalinks/:datalink/:source/:processor")) {
				source = g.pick();
			} else if (g.meets("/:workflow/:dataflow/:datalinks/:datalink/:source/:port")) {
				source += ":" + g.pick();
			}

			if (g.leaves("/:workflow/:dataflow/:datalinks/:datalink")) {
				datanodes.put(source, sink);
				source = null;
				sink = null;
			}
		}

		Set<String> processors = new HashSet<String>();
		processors.addAll(inputs.keySet());
		processors.addAll(outputs.keySet());
		for (String p : processors) {
			l.info("{} [{}]", p, activities.get(p));
			if (inputs.get(p).isEmpty()) {
				for (String o : outputs.get(p)) {
					l.info(" - - {}", o);
				}
			} else if (outputs.get(p).isEmpty()) {
				for (String i : inputs.get(p)) {
					l.info(" {} - -", i);
				}
			} else
				for (String i : inputs.get(p)) {
					for (String o : outputs.get(p)) {
						l.info(" {} ? {}", i, o);
					}
				}
		}

	}

	@Test
	public void scufl2_t2flow() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		WorkflowBundle wb = io.readBundle(__f("Get_similar_phenotypes_for_a_disease_and_a_gene-v1.wfbundle"), null);
		FeatureSet ex = FeatureSetExtractor.generate(wb);
		// Iterator<Annotation> it =
		// wb.getMainWorkflow().getAnnotations().iterator();
		// while(it.hasNext()){
		// System.out.print('-');
		// System.out.println(it.next());
		// }

		// for(InputPort ip : wb.getMainWorkflow().getInputPorts()){
		// Iterator<Annotation> it = ip.
		// }
		//

		System.out.println("Profiles count: " + wb.getProfiles().size());
		Profile p = wb.getMainProfile();
		// Map<Processor,Activity> activityBindings = new
		// HashMap<Processor,Activity>();
		Map<Processor, ProcessorBinding> bindings = new HashMap<Processor, ProcessorBinding>();
		Iterator<ProcessorBinding> it = p.getProcessorBindings().iterator();
		while (it.hasNext()) {
			ProcessorBinding pb = it.next();
			bindings.put(pb.getBoundProcessor(), pb);
		}
		for (Configuration c : p.getConfigurations()) {
			System.out.println(c);
			System.out.println(c.getConfigures());
			// System.out.println(c.getJson());
		}
		for (String i : ex.getPortPairs()) {
			System.out.println(i);
			for(Feature f: ex.getFeatures(i)){
				System.out.print(' ');
				System.out.println(f);
			}
			// Configuration c = ;

			// Configuration c =
			// wb.getTools().configurationForActivityBoundToProcessor(i.processor(),
			// p);
			// JsonNode jn = c.getJson();
			// System.out.println(c.getAnnotations());
			// while (it.hasNext()) {
			// System.out.print('-');
			// System.out.println(it.next());
			// }

			// Configuration c =
			// p.getTools().configurationForActivityBoundToProcessor(i.processor(),
			// p);
			//
//			Configuration ac;
//			try {
//				//ac = bindings.get(i.processor()).getBoundActivity().getConfiguration();
//				ac = i.processor().getActivityConfiguration(p);
//				//System.out.println(ac.getJsonAsString());
//				Iterator<String> fields = ac.getJson().fieldNames(); 
//				while(fields.hasNext()){
//					System.out.print(' ');
//					System.out.println(fields.next());
//				}
//			} catch (Exception e) {
//				// No configuration
//			}
//			Configuration pc;
//			try {
//				pc = bindings.get(i.processor()).getBoundProcessor().getConfiguration(p);
////				System.out.println(pc.getJsonAsString());
//				Iterator<String> fields = pc.getJson().fieldNames(); 
//				while(fields.hasNext()){
//					System.out.print(' ');
//					System.out.println(fields.next());
//				}
//			} catch (Exception e) {
//				// No configuration
//			}
//			// Iterator<ProcessorBinding> it =
//			// p.getProcessorBindings().iterator();
//			// while (it.hasNext()) {
//			// ProcessorBinding pb = it.next();
//			//
//			// System.out.print('-');
//			// Set<ProcessorInputPortBinding> ipbs = pb.getInputPortBindings();
//			// for (ProcessorInputPortBinding pipb : ipbs) {
//			// System.out.println(pipb.getBoundActivityPort());
//			// }
//			// }

		}
	}
}
