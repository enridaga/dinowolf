package dinowolf.server.application.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import datanode.dsl.DNGraph;

@Path("datanode")
@Produces({ MediaType.APPLICATION_JSON })
public class DatanodeResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(DatanodeResource.class);

	@Path("list")
	@GET
	public Response list() throws IOException {
		log.debug("GET");
		JsonArray a = new JsonArray();
		for(String s: DNGraph.relations)
			a.add(s);
		return Response.ok(new Gson().toJson(a)).build();
	}

}
