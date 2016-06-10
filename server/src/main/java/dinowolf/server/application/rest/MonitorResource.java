package dinowolf.server.application.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import enridaga.colatti.ColattiException;
import enridaga.colatti.Lattice;

@Path("monitor")
@Produces({ MediaType.APPLICATION_JSON })
public class MonitorResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(MonitorResource.class);

	@GET
	@Path("lattice")
	public Response lattice(){
		JsonObject o = new JsonObject();
		Lattice lattice = getManager().lattice();
		JsonArray a = new JsonArray();
		try {
			for(Object s : lattice.supremum().objects()){
				a.add((String) s);
			}
		} catch (ColattiException e1) {
			log.error("ERROR",e1);
			return Response.serverError().build();
		}
		o.add("objects", a);
		a = new JsonArray();
		try {
			for(Object s : lattice.infimum().attributes()){
				a.add((String) s);
			}
		} catch (ColattiException e) {
			log.error("ERROR",e);
			return Response.serverError().build();
		}
		o.add("attributes", a);
		try {
			o.addProperty("concepts", lattice.size());
		} catch (ColattiException e) {
			o.addProperty("concepts", "?");
			log.error("",e);
		}
		return Response.ok(new Gson().toJson(o)).build();
	}
	
}
