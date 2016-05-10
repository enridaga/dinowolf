package dinowolf.server.application.writers;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.stream.JsonWriter;

abstract class PackedWriter<T> implements MessageBodyWriter<T> {
	protected final Logger l = LoggerFactory.getLogger(getClass());

	@Override
	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// Deprecated by JAX-RS 2.0
		return 0;
	}

	protected JsonWriter getWriter(OutputStream s) throws UnsupportedEncodingException {
		return new JsonWriter(new OutputStreamWriter(s, "UTF-8"));
	}

	protected OutputStreamWriter getStreamWriter(OutputStream s) throws UnsupportedEncodingException {
		return new OutputStreamWriter(s, "UTF-8");
	}

}
