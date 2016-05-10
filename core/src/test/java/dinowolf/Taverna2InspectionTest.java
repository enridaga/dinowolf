package dinowolf;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.scufl2.annotation.AnnotationTools;
import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
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

import org.apache.jena.query.Dataset;

import dinowolf.features.Feature;
import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;
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
		FeaturesMap ex = FeaturesMapExtractor.generate(wb);

		System.out.println("Profiles count: " + wb.getProfiles().size());
		Profile p = wb.getMainProfile();

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
			for (Feature f : ex.getFeatures(i)) {
				System.out.print(' ');
				System.out.println(f);
			}
		}
	}

	@Test
	public void annotations() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		AnnotationTools ann = new AnnotationTools();
		
		final WorkflowBundle wb = io.readBundle(__f("3Drec-v1.wfbundle"), null);
		//		 The description of a workflow (as I can see it on the myexperiment portal)

		// Scufl2Tools scufl2Tools = new Scufl2Tools();
		for (Annotation a: wb.getAnnotations()) {
			  System.out.println(a.getRDFContent());
			  // Or more elaborate:
			  // (in case you want to replace the content you will need the path)
			  //String path = getResourcePath();
			  //System.out.println(wb.getResources().getResourceAsString(path));
			}

	}

	@Test
	public void moreAnnotations() throws ReaderException, IOException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		AnnotationTools tools = new AnnotationTools();

		final WorkflowBundle wb = io.readBundle(__f("3Drec-v1.wfbundle"), null);
		Dataset ds = tools.annotationDatasetFor(wb.getMainWorkflow());
		System.out.println("DatasetGraph size: " + ds.asDatasetGraph().size());
		System.out.println("Annotations: " + wb.getAnnotations().size());

		wb.accept(new Visitor() {
			@Override
			public boolean visit(WorkflowBean node) {
				System.out.println(">>>");
				System.out.println(tools.getTitle((Child<?>) node));
				System.out.println(tools.getCreator((Child<?>) node));
				System.out.println(tools.getExampleValue((Child<?>) node));
				System.out.println(tools.getDescription((Child<?>) node));
				System.out.println("<<<");
				return true;
			}

			@Override
			public boolean visitEnter(WorkflowBean node) {
				return true;
			}

			@Override
			public boolean visitLeave(WorkflowBean node) {
				return true;
			}
		});

		System.out.println("=============================================");

		wb.getMainProfile().accept(new Visitor() {
			@Override
			public boolean visit(WorkflowBean node) {
				System.out.print(node);
				System.out.print(' ');
				System.out.println(wb.getTools().annotationsFor(node, wb));
				return true;
			}

			@Override
			public boolean visitEnter(WorkflowBean node) {
				return true;
			}

			@Override
			public boolean visitLeave(WorkflowBean node) {
				return true;
			}
		});
	}
}
