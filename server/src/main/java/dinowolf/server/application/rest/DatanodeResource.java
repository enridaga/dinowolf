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
import com.google.gson.JsonObject;

import datanode.dsl.DNGraph;
import datanode.ontology.DN;

@Path("datanode")
@Produces({ MediaType.APPLICATION_JSON })
public class DatanodeResource extends AbstractResource {
	
	private static final Logger log = LoggerFactory.getLogger(DatanodeResource.class);
	private static DN _DN = null;
	private JsonObject datanode = null;

	private DN DN(){
		if(_DN == null){
			_DN = new DN();
		}
		return _DN;
	}
	
	@Path("list")
	@GET
	public Response list() throws IOException {
		
		log.debug("GET");
		JsonArray a = new JsonArray();
		for (String s : DNGraph.relations){
			JsonObject o = new JsonObject();
			o.addProperty("id", s);
			o.addProperty("label", DN().label(s));
			o.addProperty("comment", DN().comment(s));
			a.add(o);
		}
		return Response.ok(new Gson().toJson(a)).build();
	}

	private void makeTree(JsonObject main, String visiting) {
		
		JsonObject visited = new JsonObject();
		main.add(visiting, visited);
		visited.addProperty("id", visiting);
		visited.addProperty("label", DN().label(visiting));
		visited.addProperty("comment", DN().comment(visiting));
		visited.addProperty("subtree", DN().subProperties(visiting).length);
		visited.addProperty("supertree", DN().superProperties(visiting).length);
		String[] i = DN().inverseOf(visiting);
		if (i.length > 0) {
			visited.addProperty("inverse", i[0]);
		}
		JsonArray ch = new JsonArray();
		for (String s : DN().directSubProperties(visiting)) {
			ch.add(s);
			if(!main.has(s)){
				makeTree(main, s);
			}
		}
		visited.add("subProperties", ch);
	
		ch = new JsonArray();
		for (String s : DN().directSuperProperties(visiting)) {
			ch.add(s);
			if(!main.has(s)){
				makeTree(main, s);
			}
		}
		visited.add("superProperties", ch);
	}


	@GET
	@Path("tree")
	public Response tree() throws IOException {
		log.debug("GET");
		if(datanode == null){
			datanode = new JsonObject();
			makeTree(datanode, DNGraph.relatedWith);			
		}
		return Response.ok(new Gson().toJson(datanode)).build();
	}
}
