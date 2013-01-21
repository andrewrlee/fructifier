package uk.co.optimisticpanda.db.phase;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.db.apply.DatabaseApplier;
import uk.co.optimisticpanda.versioning.ResourceChangeSetAndDeltaVersion;
import uk.co.optimisticpanda.versioning.ResourceVersionProvider;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public class IncrementalDatabasePhase extends DatabasePhase  {
	
	@Autowired
	private transient DatabaseApplier applier;
	
	private Resource deltaDir;
	private Optional<File> outputFile = Optional.<File> absent();
	private Optional<Resource> combinedTemplate = Optional.<Resource> absent();
	private Optional<Resource> lastChangeToApply = Optional.<Resource> absent();

	public void execute() {
		Supplier<List<ResourceChangeSetAndDeltaVersion>> provider = new ResourceVersionProvider(deltaDir);
		if (combinedTemplate.isPresent()){
			applier.applyUpgrade(this, provider, outputFile, lastChangeToApply, combinedTemplate.get());
			return;
		}
		String dbms = getConnectionDefinition().getDbms();
		applier.applyUpgrade(this, provider, outputFile, lastChangeToApply, new ClassPathResource("combined." + dbms + ".ftl", this.getClass()));
		
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())//
				.add("name", getName()) //
				.add("deltaDir", deltaDir) //
				.add("connectionName", getConnection())//
				.add("lastChangeToApply", lastChangeToApply) //
				.add("combinedTemplate", combinedTemplate) //
				.add("outputFile", outputFile) //
				.add("phaseType", getPhaseType()) //
				.add("data", getData())//
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(deltaDir, lastChangeToApply, outputFile,
				combinedTemplate, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IncrementalDatabasePhase other = (IncrementalDatabasePhase) obj;
		return super.equals(obj) && Objects.equal(this.deltaDir, other.deltaDir)
				&& Objects.equal(this.lastChangeToApply,
						other.lastChangeToApply)
				&& Objects.equal(this.outputFile, other.outputFile)
				&& Objects.equal(this.combinedTemplate, other.combinedTemplate);
	}

}
