package dinowolf.server.application.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

@Provider
@Produces(MediaType.TEXT_PLAIN)
public class WorkflowBundleToTxt extends PackedWriter<WorkflowBundle> {
	private static final Logger l = LoggerFactory.getLogger(WorkflowBundleToTxt.class);

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == WorkflowBundle.class;
	}
	
	@Override
	public void writeTo(WorkflowBundle t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		l.debug("Writing {} to txt", type);
		OutputStreamWriter w = getStreamWriter(entityStream);
		w.write("name:\t");
		w.write(t.getName());
		w.write('\n');
		w.close();
	}

}
