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

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonWriter;

import dinowolf.io.AnnotationHelper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class WorkflowBundleToJson extends PackedWriter<WorkflowBundle> {
	private static final Logger l = LoggerFactory.getLogger(WorkflowBundleToJson.class); 
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == WorkflowBundle.class;
	}

	@Override
	public void writeTo(final WorkflowBundle t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		final JsonWriter w = getWriter(entityStream);
		w.beginObject();
		w.name("name").value(t.getName());
		l.trace("name: ", t.getName());
		AnnotationHelper tools = new AnnotationHelper(t);
		w.name("title");
//		if(l.isTraceEnabled()){
//			l.trace("1: ", t.getName());
//			l.trace("2: ", tools.getTitle(t.getMainProfile()));
//			l.trace("3: ", tools.getTitle(t.getMainWorkflow()));
//		}
		w.value(tools.getTitle(t.getMainWorkflow()));
		w.name("description");
//		if(l.isTraceEnabled()){
//			l.trace("description: {}", tools.getDescription(t.getMainWorkflow()));
//		}
		w.value(tools.getDescription(t.getMainWorkflow()));
		w.name("creator");
//		if(l.isTraceEnabled()){
//			l.trace("creator: {}", tools.getCreator(t.getMainWorkflow()));
//		}
		w.value(tools.getCreator(t.getMainWorkflow()));
		w.endObject();
		w.close();
	}

}
