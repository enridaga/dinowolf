package dinowolf.server.application.writers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public abstract class MustacheWriter<T> extends PackedWriter<T> {

	@Override
	public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(getTemplate());
		mustache.execute(new PrintWriter(entityStream), getScope()).flush();
	}

	protected abstract String getTemplate();

	protected abstract Object getScope();

}
