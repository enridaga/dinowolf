package dinowolf.database.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public interface Repository {
	public List<String> list();

	/**
	 * 
	 * @param repositoryId
	 * @return
	 * @throws IOException
	 */
	public WorkflowBundle get(String repositoryId) throws IOException;

	/**
	 * returns the generated Repository ID
	 * @param bundle
	 * @param force
	 * @return
	 * @throws IOException
	 */
	public void put(WorkflowBundle bundle, String repositoryId, boolean override) throws IOException;
	
	/**
	 * 
	 * @param wfbundle
	 * @param override
	 * @return - the used repositoryId
	 * @throws IOException
	 */
	public String put(File wfbundle, boolean override) throws IOException;
}
