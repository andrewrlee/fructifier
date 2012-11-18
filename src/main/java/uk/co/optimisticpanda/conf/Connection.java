package uk.co.optimisticpanda.conf;

import java.util.Set;

import uk.co.optimisticpanda.util.ConfigurationException;
import uk.co.optimisticpanda.util.Named;

import com.google.common.base.Objects;

/**
 * Base class that represents a connection to an external datasource.
 */
public abstract class Connection implements Named {

	private String name;
	private String connectionType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public static Connection create(Set<String> availableConnectionTypes, String detailsName, String type, Class<? extends Connection> clazz) {
		try {
			if (clazz == null) {
				throw new ConfigurationException("Do not know what type of connection: " + type + " is, for connection:" + detailsName + ". Possible connection types are: "
						+ availableConnectionTypes);
			}
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new ConfigurationException("Could not instantiate connection:" + detailsName + ", type:" + type + ", class:" + clazz, e);
		} catch (IllegalAccessException e) {
			throw new ConfigurationException("Could not instantiate connection:" + detailsName + ", type:" + type + ", class:" + clazz, e);
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name, connectionType, super.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Connection other = (Connection) obj;
		return Objects.equal(this.name, other.name) //
				&& Objects.equal(this.connectionType, other.connectionType);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())//
				.add("name", name) //
				.add("connectionType", connectionType)//
				.toString();
	}

}
