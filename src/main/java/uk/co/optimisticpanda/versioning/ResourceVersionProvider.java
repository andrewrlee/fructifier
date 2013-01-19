package uk.co.optimisticpanda.versioning;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;


import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class ResourceVersionProvider implements Supplier<List<ResourceChangeSetAndDeltaVersion>> {

	private static final Pattern deltaPattern = Pattern.compile("(\\d+).*");
	private static final Pattern releasePattern = Pattern.compile("([\\d\\._]+).*");
	private final static Logger logger = Logger.getLogger(ResourceVersionProvider.class);
	private File root;

	public ResourceVersionProvider(Resource resource) {
		try {
			root = resource.getFile();
			if (!root.isDirectory()) {
				throw new IOException("file is not a directory");
			}
		} catch (IOException e) {
			logger.error("Problem accessing file for resource:" + resource.getDescription());
			Throwables.propagate(e);
		}
	}

	public List<ResourceChangeSetAndDeltaVersion> get() {
		List<ResourceChangeSetAndDeltaVersion> versions = Lists.newArrayList();
		collect(versions, root);
		Collections.sort(versions);
		return versions;
	}

	private void collect(List<ResourceChangeSetAndDeltaVersion> versions, File file) {
		if (!file.isDirectory()) {
			long extractIdFromFilename = extractIdFromFilename(file.getName());
			String changeSet = extractReleaseNumberFromFolderName(file.getParentFile());
			versions.add(new ResourceChangeSetAndDeltaVersion(changeSet, extractIdFromFilename, new FileSystemResource(file)));
		} else {
			for (File child : file.listFiles()) {
				if (!child.getName().startsWith(".")) {
					collect(versions, child);
				}
			}
		}
	}

	public long extractIdFromFilename(String filename) throws IllegalArgumentException {
		Matcher matches = deltaPattern.matcher(filename);
		if (!matches.matches() || matches.groupCount() != 1)
			throw new IllegalArgumentException("Could not extract a change script number from filename: " + filename);

		return Long.parseLong(matches.group(1));
	}

	public String extractReleaseNumberFromFolderName(File file) throws IllegalArgumentException {
		Matcher matches = releasePattern.matcher(file.getPath());
		if (!matches.matches() || matches.groupCount() != 1)
			return file.getName();
		String result = matches.group(1);
		// remove last char if _ or .
		return result.endsWith("_") || result.endsWith(".") ? result.substring(0, result.length() - 1) : result;
	}

}
