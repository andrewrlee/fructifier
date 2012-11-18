package uk.co.optimisticpanda.db.conf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.optimisticpanda.conf.Connection;
import uk.co.optimisticpanda.db.versioning.ChangeSetAndDeltaVersion;
import uk.co.optimisticpanda.db.versioning.JdbcProvider;
import uk.co.optimisticpanda.versioning.Difference;
import uk.co.optimisticpanda.versioning.Difference.Type;
import uk.co.optimisticpanda.versioning.Version;
import uk.co.optimisticpanda.versioning.VersionProvider;

/**
 * A {@link VersionProvider} for databases. This is strongly tied to the
 * {@link ChangeSetAndDeltaVersion} version type. Other types of version will have to create their own {@link VersionProvider} implementation.
 */
public class DatabaseVersionProvider implements VersionProvider {

	private final JdbcProvider jdbcProvider;
	private final String connectionName;

	public DatabaseVersionProvider(JdbcProvider provider, String connectionName) {
		this.jdbcProvider = provider;
		this.connectionName = connectionName;
	}

	public List<Version> getVersions() {
		DatabaseConnection details = jdbcProvider.getDetails(connectionName);
		JdbcTemplate template = jdbcProvider.getTemplate(connectionName);
		return template.query("select id, change_set from " + details.getChangeLogTableName(), new RowMapper<Version>() {
			public Version mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new ChangeSetAndDeltaVersion(rs.getString(1), rs.getLong(2));
			}
		});
	}

	public List<Difference<Version>> getDifference(VersionProvider other) {
		List<Difference<Version>> result = new ArrayList<Difference<Version>>();
		List<Version> otherVersions = new ArrayList<Version>(other.getVersions());
		
		for (Version version : getVersions()) {
			if (!otherVersions.remove(version)) {
				result.add(new Difference<Version>(version, Type.EXTRA));
			}
		}
		for (Version version : otherVersions) {
			result.add(new Difference<Version>(version, Type.MISSING));
		}
		Collections.sort(result);
		return result;
	}

	public String getConnectionName() {
		return connectionName;
	}

	public Connection getConnection() {
		return jdbcProvider.getDetails(connectionName);
	}

	public static class DatabaseVersionProviderFactory implements VersionProviderFactory{

		@Autowired
		private JdbcProvider jdbcProvider;
		
		public Class<? extends Connection> getConnectionType() {
			return DatabaseConnection.class;
		}

		public VersionProvider createVersionProvider(String connectionName) {
			return new DatabaseVersionProvider(jdbcProvider, connectionName);
		}
		
		
	}
}
