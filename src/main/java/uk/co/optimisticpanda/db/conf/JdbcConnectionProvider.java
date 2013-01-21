package uk.co.optimisticpanda.db.conf;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.optimisticpanda.conf.RunningOrder.ConnectionCollection;
import uk.co.optimisticpanda.db.apply.DatabaseVersionProvider;
import uk.co.optimisticpanda.db.apply.QueryExtractor;
import uk.co.optimisticpanda.db.apply.QueryExtractor.QueryVisitor;
import uk.co.optimisticpanda.versioning.ChangeSetAndDeltaVersion;
import uk.co.optimisticpanda.versioning.VersionUtils;
import uk.co.optimisticpanda.versioning.VersionUtils.Difference;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

public class JdbcConnectionProvider {

	private Map<String, JdbcConnection> providers = Maps.newHashMap();

	public JdbcConnectionProvider(ConnectionCollection collection) {
		for (DatabaseConnectionDefinition connectionDetails : collection.ofType(DatabaseConnectionDefinition.class)) {
			JdbcConnection provider = new JdbcConnection(connectionDetails);
			providers.put(connectionDetails.getName(), provider);
		}
	}

	public JdbcConnection getJdbcConnection(String connectionName) {
		return providers.get(connectionName);
	}

	public static class JdbcConnection{
		private final DataSource dataSource;
		private final DatabaseConnectionDefinition definition;
		
		public JdbcConnection(DatabaseConnectionDefinition connectionDetails) {
			this.dataSource = getDataSource(connectionDetails);
			this.definition = connectionDetails;
		}

		public DatabaseConnectionDefinition getDefinition() {
			return definition;
		}
		
		public JdbcTemplate getTemplate() {
			return new JdbcTemplate(dataSource);
		}
		
		public <D> List<D> executeQuery(String sql, RowMapper<D> mapper){
			return (List<D>) getTemplate().query(sql, mapper);
		} 
		
		private DataSource getDataSource(DatabaseConnectionDefinition details) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(details.getDriver());
			dataSource.setUsername(details.getUser());
			dataSource.setPassword(details.getPassword());
			dataSource.setUrl(details.getConnectionUrl());
			return dataSource;
		}

		public <D> D execute(ConnectionCallback<D> connectionCallback) {
			return getTemplate().execute(connectionCallback);
		}
		
		public void visitScript(Resource scriptTemplate, Reader reader, QueryVisitor visitor) {
			QueryExtractor extractor = new QueryExtractor(definition.getDelimiter(), definition.getSeparator(), definition.getSeparatorLocation());
			extractor.visitQueries(reader, visitor);
		}

		public <A extends ChangeSetAndDeltaVersion> Difference<ChangeSetAndDeltaVersion, A> getDifferences(Supplier<List<A>> resourceVersionProvider) {
			DatabaseVersionProvider provider = new DatabaseVersionProvider(this);
			return VersionUtils.getChanges(provider, resourceVersionProvider);
		}
		
	}
}
