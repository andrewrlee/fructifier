package uk.co.optimisticpanda.versioning;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.conf.Connection;

import com.google.common.collect.Maps;

public interface VersionProvider {
	/**
	 * @return the name of the connection that provides the version information
	 */
	String getConnectionName();

	/**
	 * @return the versions that are currently in this source
	 */
	List<Version> getVersions();

	/**
	 * @return the connection details for this version provider
	 */
	Connection getConnection();

	/**
	 * @param versions
	 * @return the difference between the versions stored in 2 version providers
	 */
	List<Difference<Version>> getDifference(VersionProvider versions);

	/**
	 * A factory that creates VersionProviders
	 */
	public static interface VersionProviderFactory {
		/** The type of connection that Versionproviders created by this factory use.*/
		Class<? extends Connection> getConnectionType();

		/** Create a version provider for the connection with this name.*/
		VersionProvider createVersionProvider(String connectionName);
	}

	public static class VersionProviders implements InitializingBean {
		@Autowired
		private RunningOrder configuration;
		
		@Autowired
		private VersionProviderFactory factory;

		private Map<String, VersionProvider> map;
		
		public VersionProvider getProvider(String connectionName) {
			return map.get(connectionName);
		}

		public void afterPropertiesSet() throws Exception {
			this.map = Maps.newHashMap();
			for (Connection connection : configuration.getConnections().ofType(factory.getConnectionType())) {
				map.put(connection.getName(), factory.createVersionProvider(connection.getName()));
			}
		}
	}
}