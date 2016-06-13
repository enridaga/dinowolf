package dinowolf.server.application.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.h2.AnnotationsLoggerH2;

@Path("annotation/{fromTo: .*}")
@Produces({ MediaType.APPLICATION_JSON })
public class AnnotationResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(AnnotationResource.class);

	@PathParam("fromTo")
	private String fromTo;

	private String getBundleId() {
		return fromTo.substring(0, fromTo.indexOf('/'));
	}

	private String getFromTo() {
		return fromTo;
	}

	@GET
	public Response list() throws IOException {
		log.debug("GET {} {}", getBundleId(), getFromTo());
		List<String> list = getManager().annotations(getFromTo());
		return Response.ok(AnnotationsLoggerH2.toJsonString(list)).build();
	}

	@POST
	public Response annotate(@FormParam("relation") List<String> annotations) throws IOException {
		log.debug("POST {} {} {}", getBundleId(), getFromTo(), annotations);
		getManager().annotate(getBundleId(), getFromTo(), annotations, getManager().recommend(getBundleId(), getFromTo()));
		return Response.ok().build();
	}
}
