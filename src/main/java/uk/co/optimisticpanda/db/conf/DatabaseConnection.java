package uk.co.optimisticpanda.db.conf;

import uk.co.optimisticpanda.conf.Connection;
import uk.co.optimisticpanda.db.apply.QueryExtractor;
import uk.co.optimisticpanda.db.apply.QueryExtractor.SeparatorLocation;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * A {@link Connection} to a database resource
 */
public class DatabaseConnection extends Connection {

	private String connectionUrl;
	private String user;
	private String password;
	private String driver;
	private String dbms;
	private Optional<String> encoding = Optional.absent();
	private Optional<String> lineEnding = Optional.absent();
	private Optional<String> delimiter = Optional.absent();
	private Optional<SeparatorLocation> separatorLocation = Optional.absent();
	private Optional<String> seperator = Optional.absent();
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

	public SeparatorLocation getSeparatorLocation() {
		return separatorLocation.orNull();
	}

	public void setSeparatorLocation(SeparatorLocation separatorLocation) {
		this.separatorLocation = Optional.fromNullable(separatorLocation);
	}

	public String getSeperator() {
		return seperator.orNull();
	}

	public void setSeperator(String seperator) {
		this.seperator = Optional.fromNullable(seperator);
	}

	public String getChangeLogTableName() {
		return changeLogTableName.or("changelog");
	}

	public void setChangeLogTableName(String changeLogTableName) {
		this.changeLogTableName = Optional.fromNullable(changeLogTableName);
	}

	public QueryExtractor getQueryExtractor() {
		return new QueryExtractor(getDelimiter(), getSeperator(), getSeparatorLocation());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(password, user, connectionUrl, driver, dbms, delimiter, separatorLocation, lineEnding, encoding, changeLogTableName, seperator);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DatabaseConnection other = (DatabaseConnection) obj;
		return super.equals(obj) && //
				Objects.equal(this.getName(), other.getName()) && //
				Objects.equal(this.connectionUrl, other.connectionUrl) && //
				Objects.equal(this.driver, other.driver) && //
				Objects.equal(this.dbms, other.dbms) && //
				Objects.equal(this.password, other.password)//
				&& Objects.equal(this.changeLogTableName, other.changeLogTableName) //
				&& Objects.equal(this.delimiter, other.delimiter) //
				&& Objects.equal(this.separatorLocation, other.separatorLocation) //
				&& Objects.equal(this.encoding, other.encoding) //
				&& Objects.equal(this.lineEnding, other.lineEnding) //
				&& Objects.equal(this.seperator, other.seperator); //

	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())//
				.add("name", getName()) //
				.add("connectionUrl", connectionUrl) //
				.add("password", password) //
				.add("dbms", dbms) //
				.add("driver", driver).toString();
	}

}
