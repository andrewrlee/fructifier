package uk.co.optimisticpanda.db.versioning;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.optimisticpanda.conf.ConnectionCollection;
import uk.co.optimisticpanda.db.conf.DatabaseConnection;

import com.google.common.collect.Maps;

public class JdbcProvider {

	private Map<String, JdbcHolder> providers = Maps.newHashMap();

	public JdbcProvider(ConnectionCollection collection) {
		for (DatabaseConnection connectionDetails : collection.ofType(DatabaseConnection.class)) {
			JdbcHolder provider = new JdbcHolder(connectionDetails);
			providers.put(connectionDetails.getName(), provider);
		}
	}

	public JdbcTemplate getTemplate(String connectionName) {
		return new JdbcTemplate(providers.get(connectionName).dataSource);
	}

	public DatabaseConnection getDetails(String connectionName) {
		return providers.get(connectionName).details;
	}

	
	private class JdbcHolder{
		private final DataSource dataSource;
		private final DatabaseConnection details;
		
		public JdbcHolder(DatabaseConnection connectionDetails) {
			this.dataSource = getDataSource(connectionDetails);
			this.details = connectionDetails;
		}
		
		public DataSource getDataSource(DatabaseConnection details) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(details.getDriver());
			dataSource.setUsername(details.getUser());
			dataSource.setPassword(details.getPassword());
			dataSource.setUrl(details.getConnectionUrl());
			return dataSource;
		}
	}
}
