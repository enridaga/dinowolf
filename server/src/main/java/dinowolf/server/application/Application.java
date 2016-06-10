package dinowolf.server.application;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.annotation.FromTo.FromToType;
import dinowolf.database.DatabaseManager;
import dinowolf.database.DatabaseManagerFactory;
import dinowolf.features.FeaturesMap;
import dinowolf.features.FeaturesMapExtractor;

public class Application extends ResourceConfig implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static final String _ParamHOME = "dinowolf.home";
	public static final String _ParamLOAD = "dinowolf.loadfrom";
	public static final String _ParamMANAGER = "dinowolf.manager";
	public static final String _ParamBUILD = "dinowolf.build";

	public Application() {
		packages("dinowolf.server.application.rest", "dinowolf.server.application.writers");
	}

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
		DatabaseManager manager;
		try {
			manager = DatabaseManagerFactory.getManager(home);
		} catch (IOException e1) {
			log.error("Initialization Failed", e1);
			throw new RuntimeException(e1);
		}
		sce.getServletContext().setAttribute(_ParamMANAGER, manager);

		String paramLOAD = sce.getServletContext().getInitParameter(_ParamLOAD);
		if (paramLOAD != null && !paramLOAD.isEmpty()) {
			File loaddir = new File(paramLOAD);
			log.info("Loading data from {}", loaddir);
			try {
				if (loaddir.isDirectory() && loaddir.canRead()) {
					FilenameFilter wfbundle = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".wfbundle");
						}
					};
					File[] fff = loaddir.listFiles(wfbundle);
					int c = 0;
					for (File f : fff) {
						// If file is a directory, then skip it
						if(f.isDirectory()){
							// Skip this
							log.error("SKIPPING {}", f);
							continue;
						}
						c++;
						try {
							String id = manager.put(f, false);
							log.info("Loaded {}/{}: {}", c, fff.length, id);
						} catch (Exception e) {
							log.error("An error occurred while attemping to load " + f, e);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot load data.", e);
			}
		}

		String paramBuildFeatures = sce.getServletContext().getInitParameter(_ParamBUILD);
		if (paramBuildFeatures != null && "true".equals(paramBuildFeatures)) {
			log.info("Build features");
			int nb = manager.list().size();
			int c = 0;
			Map<String, Exception> errors = new HashMap<String, Exception>();
			for (String bundleId : manager.list()) {
				log.trace("Loading bundle {}", bundleId);
				c++;
				try {
					WorkflowBundle wb = manager.get(bundleId);
					FeaturesMap map = FeaturesMapExtractor.extract(bundleId, wb, FromToType.IO);
					log.debug("{}/{} {} [{}]", new Object[] { c, nb, bundleId, map.size() });
					manager.put(bundleId, map);
				} catch (Exception e) {
					log.warn("Skipping {} (error occurred)", bundleId);
					errors.put(bundleId, e);
				}
			}
			if (log.isErrorEnabled()) {
				log.error("The following bundles raised exceptions:");
				for (Entry<String, Exception> entries : errors.entrySet()) {
					log.error(" > {} : {}", entries.getKey(), entries.getValue());
					if (log.isDebugEnabled()) {
						log.error(" -- ", entries.getValue());
					}
				}
			}
			log.info("Build features completed.");
			/**
			 * FIXME
			 */
			log.warn("Should rebuild Lattice. FIXME");
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//
	}

}
