package dinowolf.server.application.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.base.GeneratorBase;

import dinowolf.features.FeatureSet;
import enridaga.colatti.Rule;

@Path("annotation/{id}/{fromTo}")
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
public class AnnotationResource extends AbstractResource{
	private static final Logger log = LoggerFactory.getLogger(AnnotationResource.class);
	

	@PathParam("id")
	private String id;

	@PathParam("fromTo")
	private String fromTo;

	private String getId(){
		return id;
	}
	
	private String getFromTo(){
		if(fromTo.endsWith(".txt") || fromTo.endsWith(".json")){
			return fromTo.substring(0, fromTo.lastIndexOf('.'));
		}
		return fromTo;
	}
	
	
	@Path("recommend")
	@GET
	public Response get() throws IOException {
		log.debug("GET recommend {} {}", getId(), getFromTo());
		List<Rule> rules = getManager().recommend(getId(), getFromTo());
		return Response.ok(rules).build();
//		return null;
	}
}
