package uk.co.optimisticpanda.versioning;

import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.util.ResourceUtils;

/**
 * Note: Inherits parents equals/hashcodes to ensure it can be compared with database versions
 */
public class ResourceChangeSetAndDeltaVersion extends ChangeSetAndDeltaVersion{

	private final Resource resource;

	public ResourceChangeSetAndDeltaVersion(String changeSet, Long delta, Resource resource) {
		super(changeSet, delta);
		this.resource = resource;
	}
	
	public Resource getResource() {
		return resource;
	}
	
	public String getContent(){
		return ResourceUtils.toString(resource);
	}
}
