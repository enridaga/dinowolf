package dinowolf.server.application.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("myexperiments")
public class MyexperimentsResource {
	private static final Logger log = LoggerFactory.getLogger(MyexperimentsResource.class);

	@GET
	@Path("image/{bundle: .*}")
	@Produces("image/svg+xml")
	public Response workflowImage(@PathParam("bundle") String bundle) {
		log.debug("GET {}", bundle);
		String myexperimentsId = bundle.substring(0, bundle.indexOf("-"));
		try {
			if (Integer.decode(myexperimentsId) != 0) {
				String url = "http://www.myexperiment.org/workflows/" + myexperimentsId;
				log.debug("url {}", url);
				Model m = ModelFactory.createDefaultModel();
				RDFDataMgr.read(m, url +".rdf", Lang.RDFXML);
				try {
					m.read(new URL(url + ".rdf").openStream(), null, "RDF/XML");
				} catch ( IOException e) {
					throw new WebApplicationException(e);
				}
				log.debug("rdf received: {} triples", m.size());
				Resource r = m.getResource(url).getPropertyResourceValue(
						m.createProperty("http://rdf.myexperiment.org/ontologies/contributions/svg"));
				if(r == null){
					log.debug("Not Found: {}", myexperimentsId);
					throw new WebApplicationException(404);
				}
				
				final String imageUrl = r.getURI();
				return Response.ok(new StreamingOutput() {
					@Override
					public void write(OutputStream output) throws IOException, WebApplicationException {
						URLConnection conn = new URL(imageUrl).openConnection();
						
						IOUtils.copy(conn.getInputStream(), output);
					}
				}).build();
			} else {
				throw new WebApplicationException("Not a number: " + myexperimentsId);
			}
		} catch (NumberFormatException e) {
			throw new WebApplicationException(e);
		}

	}
}
