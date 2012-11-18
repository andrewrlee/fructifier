package uk.co.optimisticpanda.config.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.config.db.apply.QueryExtractor;
import uk.co.optimisticpanda.config.serializing.ReflectionUtil.IgnoreForSerializing;
import uk.co.optimisticpanda.versioning.VersionProvider;
import uk.co.optimisticpanda.versioning.VersionProvider.VersionProviders;
import uk.co.optimisticpanda.versioning.db.JdbcProvider;

import com.google.common.base.Objects;

/**
 * A parent class for database phases. Provides a {@link VersionProvider} for a named database connection.
 */
public abstract class DatabasePhase extends Phase {

	private String connectionName;
	private transient VersionProvider provider;
	private transient JdbcProvider jdbcProvider;
	
	@Autowired
	public void setJdbcProvider(JdbcProvider provider) {
		this.jdbcProvider = provider;
	}
	
	@Autowired
	public void setProvider(VersionProviders providers) {
		this.provider = providers.getProvider(getConnectionName());
	}
	
	public void setConnectionName(String connectionName) {
		this.connectionName = connectionName;
	}

	public String getConnectionName() {
		return connectionName;
	}

	@IgnoreForSerializing
	public VersionProvider getVersionProvider() {
		return provider;
	}

	@IgnoreForSerializing
	public JdbcTemplate getJdbcTemplate() {
		return jdbcProvider.getTemplate(getConnectionName());
	}
	
	@IgnoreForSerializing
	public DatabaseConnection getConnection() {
		return jdbcProvider.getDetails(getConnectionName());
	}

	@IgnoreForSerializing
	public QueryExtractor getQueryExtractor() {
		return jdbcProvider.getDetails(getConnectionName()).getQueryExtractor();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), connectionName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DatabasePhase other = (DatabasePhase) obj;
		return super.equals(obj) //
				&& Objects.equal(this.connectionName, other.connectionName);
		}
}
