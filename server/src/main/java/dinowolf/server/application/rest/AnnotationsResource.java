package dinowolf.server.application.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

import dinowolf.database.DatabaseManager;
import dinowolf.database.h2.AnnotationsWalker;

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
	@Path("progress")
	public Response progress(){
		final DatabaseManager man = getManager();
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(final OutputStream os) throws IOException, WebApplicationException {
				os.write("[".getBytes("UTF-8"));
				Map<String,Integer> map = man.progress();
				boolean first = true;
				for(Entry<String,Integer> entry : map.entrySet()){
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
}
