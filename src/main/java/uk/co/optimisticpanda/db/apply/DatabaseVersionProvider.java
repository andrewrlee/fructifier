package uk.co.optimisticpanda.db.apply;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import uk.co.optimisticpanda.db.conf.JdbcConnectionProvider.JdbcConnection;
import uk.co.optimisticpanda.versioning.ChangeSetAndDeltaVersion;

import com.google.common.base.Supplier;

/**
 * A {@link VersionProvider} for databases. This is strongly tied to the
 * {@link ChangeSetAndDeltaVersion} version type. Other types of version will have to create their own {@link VersionProvider} implementation.
 */
public class DatabaseVersionProvider implements Supplier<List<ChangeSetAndDeltaVersion>> {

	private final JdbcConnection connection;

	public DatabaseVersionProvider(JdbcConnection connection) {
		this.connection = connection;
	}

	public List<ChangeSetAndDeltaVersion> get() {
		return connection.executeQuery("select change_number, change_set from " + connection.getDefinition().getChangeLogTableName(), new RowMapper<ChangeSetAndDeltaVersion>() {
			public ChangeSetAndDeltaVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new ChangeSetAndDeltaVersion(rs.getString(1), rs.getLong(2));
			}
		});
	}

}
