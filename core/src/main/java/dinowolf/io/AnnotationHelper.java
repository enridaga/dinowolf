package dinowolf.io;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.sparql.core.Quad;
import org.apache.taverna.scufl2.annotation.AnnotationTools;
import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationHelper {
	private static final Logger l = LoggerFactory.getLogger(AnnotationHelper.class);

	private WorkflowBundle bundle;
	private Dataset dataset;
	private URITools uriTools = new URITools();
	
	public AnnotationHelper(WorkflowBundle bundle) throws IOException {
		this.bundle = bundle;
		init();
	}

	private void init() throws IOException {
		dataset = DatasetFactory.createMem();
		for (ResourceEntry resource : bundle.getResources().listResources("annotation").values()) {
			l.debug("Loading resource '{}'", resource.getPath());
			Lang lang = RDFLanguages.contentTypeToLang(resource.getMediaType());
			if (lang == null) {
				l.warn("Not a semantic annotation: '{}'", resource.getPath());
				// Not a semantic annotation
				continue;
			}

			// Hackish way to generate a name from the annotation filename
			String path = resource.getPath();
			URI base = bundle.getGlobalBaseURI().resolve(path);
			Model model = ModelFactory.createDefaultModel();
			RDFDataMgr.read(model, resource.getUcfPackage().getResourceAsInputStream(path), base.toASCIIString(), lang);
			dataset.addNamedModel(base.toString(), model);
		}
	}
	
	public Dataset getDataset(){
		return dataset;
	}
	
	private String getLiteral(Child<?> workflowBean, String propertyUri) {
		URI beanUri = uriTools.uriForBean(workflowBean);
		Node subject = NodeFactory.createURI(beanUri.toString());
		Node property = NodeFactory.createURI(propertyUri);

		Iterator<Quad> found = dataset.asDatasetGraph().find(null, subject,
				property, null);
		if (!found.hasNext()) {
			return null;
		}
		return found.next().getObject().toString(false);
	}

	public String getTitle(WorkflowBean bean) {
		return getLiteral((Child<?>) bean, AnnotationTools.TITLE.toString());
	}

	public String getDescription(WorkflowBean bean) {
		return getLiteral((Child<?>) bean, AnnotationTools.DESCRIPTION.toString());
	}

	public String getCreator(WorkflowBean bean) {
		return getLiteral((Child<?>) bean, AnnotationTools.CREATOR.toString());
	}

	public final static void parseAnnotations(final WorkflowBundle wb) throws IOException {
		if (!wb.getAnnotations().isEmpty()) {
			// Assume already parsed
			return;
		}
		URITools uriTools = new URITools();
		for (ResourceEntry resource : wb.getResources().listResources("annotation").values()) {
			l.debug("inspecting resource {}", resource);
			Lang lang = RDFLanguages.contentTypeToLang(resource.getMediaType());
			if (lang == null) {
				l.debug("not a semantic annotation");
				// Not a semantic annotation
				continue;
			}
			Annotation ann = new Annotation();
			// Hackish way to generate a name from the annotation filename
			String name = resource.getPath().replace("annotation/", "").replaceAll("\\..*", ""); // strip
																									// extension
			ann.setName(name);
			ann.setParent(wb);

			String path = resource.getPath();
			ann.setBody(URI.create("/" + path));
			URI base = wb.getGlobalBaseURI().resolve(path);
			Model model = ModelFactory.createDefaultModel();
			RDFDataMgr.read(model, resource.getUcfPackage().getResourceAsInputStream(path), base.toASCIIString(), lang);
			ResIterator subjs = model.listSubjects();
			while (subjs.hasNext()) {
				Resource r = subjs.next();
				// System.out.println(r);
				WorkflowBean b = uriTools.resolveUri(URI.create(r.getURI()), wb);
				// System.out.println(b);
				if (b != null) {
					ann.setTarget(b);
				} else {
					l.warn("uri resolution failed for {}", r.getURI());
				}
				break;
			}
			// TODO: Could just dump the nquads directly from here
		}
	}
}
