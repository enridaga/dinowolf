package dinowolf.server.application.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import dinowolf.database.DatabaseManager;
import dinowolf.database.annotations.AnnotationAction;
import dinowolf.database.h2.AnnotationsLoggerH2;
import dinowolf.database.h2.AnnotationsWalker;
import dinowolf.database.h2.LogWalker;
import enridaga.colatti.Rule;

@Path("annotations")
@Produces({ MediaType.APPLICATION_JSON })
public class AnnotationsResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(AnnotationsResource.class);

	@GET
	@Path("list")
	public Response list() {
		log.debug("GET");
		final DatabaseManager man = getManager();

		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				os.write("[".getBytes("UTF-8"));

				man.walk(new AnnotationsWalker() {
					boolean first = true;

					@Override
					public boolean read(String bundleId, String portPairName, List<String> annotations)
							throws IOException {
						if (first) {
							first = false;
						} else {
							os.write("\n,".getBytes("UTF-8"));
						}
						JsonObject o = new JsonObject();
						o.addProperty("bundle", bundleId);
						o.addProperty("portpair", portPairName);
						JsonArray a = new JsonArray();
						for (String s : annotations)
							a.add(s);
						o.add("annotations", a);
						os.write(new Gson().toJson(o).getBytes("UTF-8"));
						return true;
					}
				});
				os.write("]".getBytes("UTF-8"));
			}
		};
		return Response.ok(stream).build();
	}

	@GET
	@Path("logs")
	public Response logs() {
		log.debug("GET");
		final DatabaseManager man = getManager();
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				os.write("[".getBytes("UTF-8"));
				man.walk(new LogWalker() {
					boolean first = true;
					@Override
					public boolean read(String bundleId, String portPairName, List<String> annotations, List<Rule> rules,
							AnnotationAction action, int logId, int annotationsCount, int duration, int fromrec,
							double avgrank, double avgrel) throws IOException {
						if (first) {
							first = false;
						} else {
							os.write("\n,".getBytes("UTF-8"));
						}
						JsonObject o = new JsonObject();
						o.addProperty("bundle", bundleId);
						o.addProperty("portpair", portPairName);
						o.addProperty("action", action.name());
						o.addProperty("logid", logId);
						o.addProperty("count", annotationsCount);
						o.addProperty("duration", duration);
						o.addProperty("fromrec", fromrec);
						o.addProperty("avgrank", avgrank);
						o.addProperty("avgrel", avgrel);
						JsonArray a = new JsonArray();
						for (String s : annotations){
							a.add(s);
						}
						o.add("annotations", a);
						JsonArray r = new JsonArray();
						for(Rule ru: rules){
							JsonObject or = AnnotationsLoggerH2.ruleToJsonObject(ru);
							r.add(or);
						}
						os.write(new Gson().toJson(o).getBytes("UTF-8"));
						return true;
					}
				});
				os.write("]".getBytes("UTF-8"));
			}
		};
		return Response.ok(stream).build();
	}

	@GET
	@Path("progress")
	public Response progress() {
		final DatabaseManager man = getManager();
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				os.write("[".getBytes("UTF-8"));
				Map<String, Integer> map = man.progress();
				boolean first = true;
				for (Entry<String, Integer> entry : map.entrySet()) {
					if (first) {
						first = false;
					} else {
						os.write("\n,".getBytes("UTF-8"));
					}
					JsonObject o = new JsonObject();
					o.addProperty("bundle", entry.getKey());
					o.addProperty("progress", entry.getValue());
					os.write(new Gson().toJson(o).getBytes("UTF-8"));
				}
				os.write("]".getBytes("UTF-8"));
			}
		};
		return Response.ok(stream).build();
	}

	@GET
	@Path("bundle/{bundle}")
	public Response bundle(@PathParam("bundle") String bundleId) {
		final DatabaseManager man = getManager();
		final Map<String, List<String>> ann;
		try {
			ann = man.bundleAnnotations(bundleId);
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}

		return Response.ok(new StreamingOutput() {
			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				os.write("{".getBytes("UTF-8"));
				boolean first = true;
				for (Entry<String, List<String>> entry : ann.entrySet()) {
					if (first) {
						first = false;
					} else {
						os.write("\n,".getBytes("UTF-8"));
					}
					JsonObject o = new JsonObject();
					o.addProperty("portpair", entry.getKey());
					o.addProperty("annotated",
							entry.getValue().isEmpty() ? getManager().annotated(entry.getKey()) : true);
					Type typeOfSrc = new TypeToken<List<String>>() {
					}.getType();
					o.add("annotations", new Gson().toJsonTree(entry.getValue(), typeOfSrc));
					os.write("\"".getBytes("UTF-8"));
					os.write(entry.getKey().getBytes("UTF-8"));
					os.write("\"".getBytes("UTF-8"));
					os.write(":".getBytes("UTF-8"));
					os.write(new Gson().toJson(o).getBytes("UTF-8"));
				}
				os.write("}".getBytes("UTF-8"));
			}
		}).build();
	}
}
