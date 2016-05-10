package dinowolf.server.application.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("repository")
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
public class RepositoryResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(RepositoryResource.class);

	@GET
	public Response get() {
		log.debug("GET");
		GenericEntity<List<String>> list = new GenericEntity<List<String>>(getManager().list()) {
		};
		return Response.ok(list).build();
	}
}
