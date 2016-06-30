package dinowolf.server.application.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dinowolf.features.Feature;
import dinowolf.features.FeatureSet;

@Path("features/{ids}")
public class FeaturesResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(FeaturesResource.class);

	@PathParam("ids")
	public String ids;

	@GET
	public Response about() throws IOException {
		log.debug("{}", ids);
		String[] theIds=ids.split(",");
		List<Integer> ints = new ArrayList<Integer>();
		for(String tt : theIds){
			ints.add(Integer.parseInt(tt));
		}
		FeatureSet set = getManager().getFeatures(ints.toArray(new Integer[ints.size()]));
		JsonArray arr = new JsonArray();
		for(Feature f:set){
			JsonObject oo = new JsonObject();
			oo.addProperty("i", f.getId());
			oo.addProperty("n", f.getName());
			oo.addProperty("v", f.getValue());
			oo.addProperty("d", f.getLevel().name());
			arr.add(oo);
		}
		return Response.ok(new Gson().toJson(arr)).build();
	}
}