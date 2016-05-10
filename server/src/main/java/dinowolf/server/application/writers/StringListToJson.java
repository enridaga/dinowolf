package dinowolf.server.application.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class StringListToJson extends PackedWriter<List<String>> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return List.class.isAssignableFrom(type) && genericType.getTypeName().equals("java.util.List<java.lang.String>");
	}

	@Override
	public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		JsonWriter w = getWriter(entityStream);
		w.jsonValue(new Gson().toJson(t));
		w.close();
	}
}
