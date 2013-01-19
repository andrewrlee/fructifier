package uk.co.optimisticpanda.db.conf;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import uk.co.optimisticpanda.conf.ConnectionDefinition;
import uk.co.optimisticpanda.db.apply.QueryExtractor.DelimiterLocation;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
/**
 * A {@link ConnectionDefinition} to a database resource
 */
public class DatabaseConnectionDefinition extends ConnectionDefinition {

	private String connectionUrl;
	private String user;
	private String password;
	private String driver;
	private String dbms;
	private Optional<String> encoding = Optional.absent();
	private Optional<String> lineEnding = Optional.absent();
	private Optional<String> delimiter = Optional.absent();
	private Optional<DelimiterLocation> separatorLocation = Optional.absent();
	private Optional<String> separator = Optional.absent();
	private Optional<String> changeLogTableName = Optional.absent();

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}

	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEncoding() {
		return encoding.or("UTF-8");
	}

	public void setEncoding(String encoding) {
		this.encoding = Optional.fromNullable(encoding);
	}

	public String getLineEnding() {
		return lineEnding.or(System.getProperty("line.separator"));
	}

	public void setLineEnding(String lineEnding) {
		this.lineEnding = Optional.fromNullable(lineEnding);
	}

	public String getDelimiter() {
		return delimiter.orNull();
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = Optional.fromNullable(delimiter);
	}

	public DelimiterLocation getSeparatorLocation() {
		return separatorLocation.orNull();
	}

	public void setSeparatorLocation(DelimiterLocation separatorLocation) {
		this.separatorLocation = Optional.fromNullable(separatorLocation);
	}

	public String getSeparator() {
		return separator.orNull();
	}

	public void setSeparator(String separator) {
		this.separator = Optional.fromNullable(separator);
	}

	public String getChangeLogTableName() {
		return changeLogTableName.or("changelog");
	}

	public void setChangeLogTableName(String changeLogTableName) {
		this.changeLogTableName = Optional.fromNullable(changeLogTableName);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(password, user, connectionUrl, driver, dbms, delimiter, separatorLocation, lineEnding, encoding, changeLogTableName, separator);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DatabaseConnectionDefinition other = (DatabaseConnectionDefinition) obj;
		return super.equals(obj) && //
				equal(this.getName(), other.getName()) && //
				equal(this.connectionUrl, other.connectionUrl) && //
				equal(this.driver, other.driver) && //
				equal(this.dbms, other.dbms) && //
				equal(this.password, other.password) &&//
				equal(this.changeLogTableName, other.changeLogTableName) &&//
				equal(this.delimiter, other.delimiter) &&//
				equal(this.separatorLocation, other.separatorLocation) &&//
				equal(this.encoding, other.encoding) &&//
				equal(this.lineEnding, other.lineEnding) &&//
				equal(this.separator, other.separator); //

	}

	@Override
	public String toString() {
		return toStringHelper(this.getClass())//
				.add("name", getName()) //
				.add("connectionUrl", connectionUrl) //
				.add("password", password) //
				.add("dbms", dbms) //
				.add("driver", driver).toString();
	}

}
