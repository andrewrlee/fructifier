package uk.co.optimisticpanda.db.conf;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import uk.co.optimisticpanda.versioning.ResourceVersionProvider;
import static org.fest.assertions.api.Assertions.*;

public class ResourceVersionProviderTest {

	@Test
	public void anEmptyFolderContainsNoVersions(){
		ResourceVersionProvider versionProvider = createVersionProvider("incremental.database.scripts/setEmpty");
		assertThat(versionProvider.get()).isEmpty();
	}
	
	@Test
	public void simpleFlatFolderStructure(){
		ResourceVersionProvider versionProvider = createVersionProvider("incremental.database.scripts/set0");
		assertThat(versionProvider.get()).hasSize(3);
		try{
			assertThat(versionProvider.get().get(0).toString()).isEqualTo("set0/1");
			assertThat(versionProvider.get().get(1).toString()).isEqualTo("set0/2");
			assertThat(versionProvider.get().get(2).toString()).isEqualTo("set0/3");
		}catch(Error e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void nonFlatFolderStructureWithNumericChangeset(){
		ResourceVersionProvider versionProvider = createVersionProvider("incremental.database.scripts/set1");
		assertThat(versionProvider.get()).hasSize(3);
		try{
			assertThat(versionProvider.get().get(0).toString()).isEqualTo("1/1");
			assertThat(versionProvider.get().get(1).toString()).isEqualTo("1/2");
			assertThat(versionProvider.get().get(2).toString()).isEqualTo("2/1");
		}catch(Error e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void nonFlatFolderStructureWithChangesetInterspersedWithDots(){
		ResourceVersionProvider versionProvider = createVersionProvider("incremental.database.scripts/set2");
		assertThat(versionProvider.get()).hasSize(3);
		try{
			assertThat(versionProvider.get().get(0).toString()).isEqualTo("0.0.1/1");
			assertThat(versionProvider.get().get(1).toString()).isEqualTo("0.0.1/2");
			assertThat(versionProvider.get().get(2).toString()).isEqualTo("0.0.2/1");
		}catch(Error e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private ResourceVersionProvider createVersionProvider(String resourceName){
		ClassPathResource resource = new ClassPathResource(resourceName);
		return new ResourceVersionProvider(resource);
	}
	
}
