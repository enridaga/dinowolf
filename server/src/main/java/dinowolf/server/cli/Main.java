package dinowolf.server.cli;

import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import dinowolf.server.application.Application;

public class Main {
	public static void main(String[] args) {
		System.out.println("#1: dinowolf::server");
		Cli cli = new Cli(args);
		cli.parse();
		Server server = new Server();
		ServerConnector connector = new ServerConnector(server);

		connector.setIdleTimeout(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(cli.getParamPort());
		server.setConnectors(new Connector[] { connector });
		System.out.println("#2: server is starting on port " + cli.getParamPort());
		WebAppContext root = new WebAppContext();
		root.setContextPath("/");

		String webxmlLocation = Main.class
				.getResource("/WEB-INF/web.xml").toString();
		root.setDescriptor(webxmlLocation);
		// Pass Cli arguments to Application
		root.setInitParameter(Application._ParamHOME, cli.getParamData());
		root.setInitParameter(Application._ParamLOAD, cli.getParamLoad());
		if(cli.getParamBuild()){
			System.out.println("Setting build command");
			root.setInitParameter(Application._ParamBUILD, "true");
		}
		
		String resLocation = Main.class
				.getResource("/static").toString();
		root.setResourceBase(resLocation);
		root.setParentLoaderPriority(true);
		// Rewriter
		RewriteHandler rewrite = new RewriteHandler();
		rewrite.setRewriteRequestURI(true);
		rewrite.setRewritePathInfo(false);
		rewrite.setOriginalPathAttribute("requestedPath");
		RewriteRegexRule redirect = new RewriteRegexRule();
		redirect.setRegex("/(home|repository|workflow|annotation|annotations|logs|features).*");
		redirect.setReplacement("/index.html");
		redirect.setTerminating(true);
		rewrite.addRule(redirect);
		rewrite.setHandler(root);
		HandlerCollection _handlerCollection = new HandlerCollection();
		_handlerCollection.setHandlers(new Handler[] {rewrite});
		server.setHandler(_handlerCollection);
		System.out.println("#3: resources setup complete");

		try {
			server.start();
			System.out.println("#4: enjoy");
			server.join();
			System.out.println("#5: stopping server");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		System.out.println("#6: thank you");
	}

}
