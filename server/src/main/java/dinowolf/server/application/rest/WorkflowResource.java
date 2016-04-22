package dinowolf.server.application.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

@Path("workflow/{id}")
public class WorkflowResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(WorkflowResource.class);

	@PathParam("id")
	private String id;
	

	@GET
	public Response get() throws IOException {
		log.debug("GET {}", id);
		WorkflowBundle bundle = getManager().get(id);
		return Response.ok(bundle.getName()).build();
	}
	
	@GET
	@Path("features")
	public Response features() throws IOException {
		log.debug("GET {} features", id);
		WorkflowBundle bundle = getManager().get(id);
		FeaturesMap map = FeaturesMapExtractor.generate(bundle);
		return Response.ok(new Gson().toJson(map)).build();
	}
}
