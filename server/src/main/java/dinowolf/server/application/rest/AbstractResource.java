package dinowolf.server.application.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import dinowolf.database.DatabaseManager;
import dinowolf.server.application.Application;

public abstract class AbstractResource {
	@Context
	protected HttpHeaders requestHeaders;

	@Context
	protected UriInfo requestUri;

	@Context
	protected ServletContext context;
	private DatabaseManager manager;

	protected DatabaseManager getManager() {
		if (manager == null) {
			manager = (DatabaseManager) context.getAttribute(Application._ParamMANAGER);
		}
		return manager;
	}
}
