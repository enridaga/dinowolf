package dinowolf.server.application.rest;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import dinowolf.database.annotations.AnnotationAction;
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
		List<String> list;
		list = getManager().annotations(getFromTo());
		return Response.ok(AnnotationsLoggerH2.toJsonString(list)).build();

	}


	@POST
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED})
	public Response annotateWithForm(@FormParam("relation") List<String> annotations, @FormParam("action") String action, @FormParam("elapsedTime") int duration) throws IOException {
		log.debug("POST {} {} {} [{}]", getBundleId(), getFromTo(), annotations, action);
		AnnotationAction act = AnnotationAction.valueOf(action);
		if(act == null){
			log.error("Invalid action: " + action);
			throw new WebApplicationException(500);
		}
		switch(act){
			case NONE:
				getManager().noAnnotations(getBundleId(), getFromTo(), getManager().recommend(getBundleId(), getFromTo()), duration);
				break;
			case SKIPPED:
				getManager().skipAnnotations(getBundleId(), getFromTo(), getManager().recommend(getBundleId(), getFromTo()), duration);
				break;
			default:
				getManager().annotate(getBundleId(), getFromTo(), annotations, getManager().recommend(getBundleId(), getFromTo()), duration);
				
		}
		return Response.ok().build();
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response annotate(String jsonBody) throws IOException {
		log.debug("POST {} {} {}", getBundleId(), getFromTo(), jsonBody);
		JsonObject o = (JsonObject) new JsonParser().parse(jsonBody);
		List<String> annotations = new Gson().fromJson(o.get("annotations") , new TypeToken<List<String>>(){}.getType());
		int elapsedTime = o.get("elapsedTime").getAsInt();
		getManager().annotate(getBundleId(), getFromTo(), annotations, getManager().recommend(getBundleId(), getFromTo()), elapsedTime);
		return Response.ok().build();
	}
}
