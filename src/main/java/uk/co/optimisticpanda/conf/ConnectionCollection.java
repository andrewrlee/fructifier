package uk.co.optimisticpanda.conf;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import uk.co.optimisticpanda.util.ConfigurationException;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * Represents a collection of connections
 */
public class ConnectionCollection implements Iterator<Connection>, Iterable<Connection> {

	private LinkedHashMap<String, Connection> details = new LinkedHashMap<String, Connection>();
	private transient Iterator<Connection> iterator;

	public LinkedHashMap<String, Connection> getConnectionDetails() {
		return details;
	}

	public int size() {
		return details.size();
	}

	public Iterator<Connection> iterator() {
		if (iterator == null) {
			iterator = details.values().iterator();
		}
		return iterator;
	}

	public boolean hasNext() {
		return iterator().hasNext();
	}

	public Connection next() {
		return iterator().next();
	}

	public void remove() {
		throw new UnsupportedOperationException("Does not support remove");
	}

	public ConnectionCollection put(String name, Connection detail) {
		detail.setName(name);
		details.put(name, detail);
		return this;
	}

	public <D extends Connection> D getConnectionDetails(Class<D> clazz, String connectionName) {
		for (Entry<String, Connection> connectionDetails : details.entrySet()) {
			if (clazz.isInstance(connectionDetails.getValue()) && connectionDetails.getKey().equals(connectionName)) {
				return clazz.cast(connectionDetails.getValue());
			}
		}
		throw new ConfigurationException("Could not find connection details named: " + connectionName + " of type: " + clazz);
	}

	public <D extends Connection> List<D> ofType(Class<D> clazz) {
		List<D> list = Lists.newArrayList();
		for (Entry<String, Connection> connectionDetails : details.entrySet()) {
			if (clazz.isInstance(connectionDetails.getValue())) {
				list.add(clazz.cast(connectionDetails.getValue()));
			}
		}
		return list;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(details);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ConnectionCollection other = (ConnectionCollection) obj;
		return Objects.equal(this.details, other.details); //
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("details", details).toString();
	}

}
