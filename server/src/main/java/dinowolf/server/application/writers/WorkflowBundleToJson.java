package dinowolf.server.application.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import com.google.gson.stream.JsonWriter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class WorkflowBundleToJson extends PackedWriter<WorkflowBundle> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == WorkflowBundle.class;
	}

	@Override
	public void writeTo(final WorkflowBundle t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		l.debug("Writing {} to json ({} annotations)", type, t.getAnnotations().size());
		final JsonWriter w = getWriter(entityStream);
		w.beginObject();
		w.name("name").value(t.getName());
		w.name("annotations");
		w.beginArray();
		
		t.accept(new Visitor() {

			@Override
			public boolean visitLeave(WorkflowBean node) {
				return true;
			}

			@Override
			public boolean visitEnter(WorkflowBean node) {
				return true;
			}

			@Override
			public boolean visit(WorkflowBean node) {
				String uri = t.getUriTools().uriForBean(node).toString();
				l.trace("visiting node: {}", uri);
				for (org.apache.taverna.scufl2.api.annotation.Annotation ann : t.getTools().annotationsFor(node, t)) {
					try {
						w.beginObject();
						w.name("on");
						w.value(uri);
						w.name("name");
						w.value(ann.getName());
						w.name("value");
						w.value(ann.getRDFContent());
						w.endObject();
					} catch (IOException e) {
						l.error("", e);
						return false;
					}
				}
				return true;
			}
		});
		w.endArray();
		w.endObject();
		w.close();
	}

}
