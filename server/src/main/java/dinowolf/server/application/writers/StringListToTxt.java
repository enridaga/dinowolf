package dinowolf.server.application.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.TEXT_PLAIN)
public class StringListToTxt extends PackedWriter<List<String>> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
//		l.debug("Checking {} {} {} {}", new Object[] { type, genericType, annotations, mediaType });
		return List.class.isAssignableFrom(type) && genericType.getTypeName().equals("java.util.List<java.lang.String>");
	}

	@Override
	public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		OutputStreamWriter w = getStreamWriter(entityStream);
		for (String s : t) {
			w.write(s);
			w.write('\n');
		}
		w.close();
	}
}