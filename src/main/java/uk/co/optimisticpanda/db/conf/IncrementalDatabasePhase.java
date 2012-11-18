package uk.co.optimisticpanda.db.conf;

import java.io.File;
import java.util.List;

import uk.co.optimisticpanda.conf.Connection;
import uk.co.optimisticpanda.versioning.Version;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

public class IncrementalDatabasePhase extends DatabasePhase  {

	private File deltaDir;
	private Optional<File> outputFile = Optional.<File> absent();
	private Optional<File> templateDir = Optional.<File> absent();
	private Optional<File> lastChangeToApply = Optional.<File> absent();

	public File getDeltaDir() {
		return deltaDir;
	}

	public void setDeltaDir(File deltaDir) {
		this.deltaDir = deltaDir;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = Optional.fromNullable(outputFile);
	}

	public void setTemplateDir(File templateDir) {
		this.templateDir = Optional.fromNullable(templateDir);
	}

	public void setLastChangeToApply(File lastChangeToApply) {
		this.lastChangeToApply = Optional.fromNullable(lastChangeToApply);
	}

	public void execute() {
		Connection connection = getVersionProvider().getConnection();

		List<Version> versions = getVersionProvider().getVersions();
		System.out.println(versions);
		//work out what to apply
		//build script and write to file
		//optionally execute script
		//find out db version (get db connection)
		
		System.out.println("Executing phase: " + getName() + ", type: "
				+ getPhaseType() + ", against details: " + connection + ", data: "
				+ getData() + ", phase:" + this);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())//
				.add("name", getName()) //
				.add("deltaDir", getDeltaDir()) //
				.add("connectionName", getConnectionName())//
				.add("lastChangeToApply", lastChangeToApply) //
				.add("templateDir", templateDir) //
				.add("outputFile", outputFile) //
				.add("phaseType", getPhaseType()) //
				.add("data", getData())//
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(deltaDir, lastChangeToApply, outputFile,
				templateDir, super.hashCode());
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
				&& Objects.equal(this.templateDir, other.templateDir);
	}

}
