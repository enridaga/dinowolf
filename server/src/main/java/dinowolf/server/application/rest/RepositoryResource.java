package dinowolf.server.application.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("repository")
public class RepositoryResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(RepositoryResource.class);

	@GET
	public Response get() {
		log.debug("GET");
		List<String> list = getManager().list();
		return Response.ok(new Gson().toJson(list)).build();
	}
}
