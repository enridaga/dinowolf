package dinowolf.database.repository;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRepository implements Repository {
	private Logger l = LoggerFactory.getLogger(FileRepository.class);
	private File directory;
	private WorkflowBundleIO io = new WorkflowBundleIO();

	public FileRepository(File directory) {
		this.directory = directory;
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	@Override
	public List<String> list() {
		List<String> ids = new ArrayList<String>();
		for(String fname : directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".wfbundle");
			}
		})){
			ids.add(FilenameUtils.removeExtension(fname));
		}
		return ids;
	}

	@Override
	public WorkflowBundle get(String repositoryId) throws IOException {
		try {
			return io.readBundle(getFile(repositoryId), "application/vnd.taverna.scufl2.workflow-bundle");
		} catch (ReaderException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void put(WorkflowBundle bundle, String repositoryId, boolean force) throws IOException {
		File target = getFile(repositoryId);
		if (!target.exists() || force) {
			l.trace("Putting new bundle: {}", target);
			try {
				io.writeBundle(bundle, target, "application/vnd.taverna.scufl2.workflow-bundle");
			} catch (WriterException | IOException e) {
				l.error("Cannot write bundle: {}", e.getMessage());
				throw new IOException(e);
			}
		} else {
			throw new IOException("Already exists.");
		}
	}

	public File getDirectory() {
		return directory;
	}

	private File getFile(String repositoryId) {
		return new File(directory, new StringBuilder().append(repositoryId).append(".wfbundle").toString());
	}

	@Override
	public String put(File wfbundle, boolean override) throws IOException {
		try {
			WorkflowBundle b = io.readBundle(wfbundle, null);
			String repoId = FilenameUtils.removeExtension(wfbundle.getName());
			put(b, repoId, override);
			return repoId;
		} catch (ReaderException e) {
			throw new IOException(e);
		}
	}
}
