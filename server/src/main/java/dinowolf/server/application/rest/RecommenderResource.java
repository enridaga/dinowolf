package dinowolf.server.application.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.h2.AnnotationsLoggerH2;
import enridaga.colatti.Rule;

@Path("recommend/{fromTo: .*}")
public class RecommenderResource extends AbstractResource{
	private static final Logger log = LoggerFactory.getLogger(RecommenderResource.class);

	@PathParam("fromTo")
	private String fromTo;

	private String getBundleId() {
		return fromTo.substring(0, fromTo.indexOf('/'));
	}

	private String getFromTo() {
		return fromTo;
	}

	@GET
	public Response recommend() throws IOException {
		log.debug("GET recommend {} {}", getBundleId(), getFromTo());
		List<Rule> rules = getManager().recommend(getBundleId(), getFromTo());
		return Response.ok(AnnotationsLoggerH2.rulesToJsonString(rules)).build();
	}
}
