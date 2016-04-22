package dinowolf.server.application;

import java.io.File;
import java.io.FilenameFilter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.DatabaseManager;
import dinowolf.database.DatabaseManagerFactory;

public class Application extends ResourceConfig implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static final String _ParamHOME = "dinowolf.home";
	public static final String _ParamLOAD = "dinowolf.loadfrom";
	public static final String _ParamMANAGER = "dinowolf.manager";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing context.");

		String homeString = sce.getServletContext().getInitParameter(_ParamHOME);
		if (homeString == null) {
			throw new RuntimeException("Cannot setup repository. Missing init parameter: " + _ParamHOME);
		}
		File home = (File) new File(homeString);
		if (!home.exists()) {
			log.debug("Creating directory " + home);
			home.mkdirs();
		}

		log.info("Setup DB maanager");
		DatabaseManager manager = DatabaseManagerFactory.getManager(home);
		sce.getServletContext().setAttribute(_ParamMANAGER, manager);

		String paramLOAD = sce.getServletContext().getInitParameter(_ParamLOAD);
		if (paramLOAD != null && !paramLOAD.isEmpty()) {
			File loaddir = new File(paramLOAD);
			log.info("Loading data from {}", loaddir);
			try {
				if (loaddir.isDirectory() && loaddir.canRead()) {
					File[] fff = loaddir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".wfbundle");
						}
					});
					int c = 0;
					for (File f : fff) {
						c++;
						String id = manager.put(f, true);
						log.info("Loaded {}/{}: {}", c, fff.length, id);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot load data.", e);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
