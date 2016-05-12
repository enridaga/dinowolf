package dinowolf.server.application.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

@Path("workflow/{id}")
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
public class WorkflowResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(WorkflowResource.class);

	@PathParam("id")
	private String id;

	private String getId(){
		if(id.endsWith(".txt") || id.endsWith(".json")){
			return id.substring(0, id.lastIndexOf('.'));
		}
		return id;
	}
	
	@GET
	public Response get() throws IOException {
		log.debug("GET {}", id);
		WorkflowBundle bundle = getManager().get(getId());
		return Response.ok(bundle).build();
	}

	@GET
	@Path("/features")
	public Response features() throws IOException {
		log.debug("GET {} features", id);
		WorkflowBundle bundle = getManager().get(getId());
		FeaturesMap map = FeaturesMapExtractor.generate(bundle);
		return Response.ok(new Gson().toJson(map)).build();
	}

}
