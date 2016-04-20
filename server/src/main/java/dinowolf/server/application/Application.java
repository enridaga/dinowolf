package dinowolf.server.application;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.stream.Location;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dinowolf.database.repository.Repository;

public class Application extends ResourceConfig implements ServletContextListener {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static final String _ParamHOME = "dinowolf.home";
	public static final String _ParamLOAD = "dinowolf.loadfrom";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing context.");

		log.debug("Setting up repository");
		Repository repository;
		String homeString = sce.getServletContext().getInitParameter(_ParamHOME);
		if (homeString == null) {
			throw new RuntimeException("Cannot setup repository. Missing init parameter: " + _ParamHOME);
		}
		File database = (File) new File(homeString);
		if (!database.exists()) {
			log.debug("Creating directory " + database);
			database.mkdirs();
		}
		
//		if (data.canRead() && data.canWrite() && data.canExecute()) {
//			dataset = TDBFactory.createDataset(Location.create(data.getAbsolutePath()));
//			sce.getServletContext().setAttribute(_ObjectDataset, dataset);
//		} else {
//			throw new RuntimeException("Cannot setup database. Not enough permissions on folder " + data);
//		}
//		String paramLOAD = sce.getServletContext().getInitParameter(_ParamLOAD);
//		if (paramLOAD != null && !paramLOAD.isEmpty()) {
//			File load = new File(paramLOAD);
//			log.debug("Loading data from {}", load);
//			try {
//				RDFDataMgr.read(dataset, load.getAbsolutePath(), Lang.NQUADS);
//			} catch (Exception e) {
//				throw new RuntimeException("Cannot load data.");
//			}
//		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
