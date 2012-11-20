package uk.co.optimisticpanda.conf;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import uk.co.optimisticpanda.util.ConfigurationException;
import uk.co.optimisticpanda.util.Named;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

//A collection of named elements.
public class AbstractNamedCollection<D extends Named, THIS extends AbstractNamedCollection<D, THIS>> implements Iterator<D>, Iterable<D> {

	private LinkedHashMap<String, D> namedElements = new LinkedHashMap<String, D>();
	private transient Iterator<D> iterator;

	public LinkedHashMap<String, D> getElements() {
		return namedElements;
	}

	public int size() {
		return namedElements.size();
	}

	public Iterator<D> iterator() {
		if (iterator == null) {
			iterator = namedElements.values().iterator();
		}
		return iterator;
	}

	public boolean hasNext() {
		return iterator().hasNext();
	}

	public D next() {
		return iterator().next();
	}

	public void remove() {
		throw new UnsupportedOperationException("Does not support remove");
	}

	@SuppressWarnings("unchecked")
	public THIS put(String name, D detail) {
		detail.setName(name);
		namedElements.put(name, detail);
		return (THIS) this;
	}

	public D getElement(Class<D> clazz, String connectionName) {
		for (Entry<String, D> connectionDetails : namedElements.entrySet()) {
			if (clazz.isInstance(connectionDetails.getValue()) && connectionDetails.getKey().equals(connectionName)) {
				return clazz.cast(connectionDetails.getValue());
			}
		}
		throw new ConfigurationException("Could not find connection details named: " + connectionName + " of type: " + clazz);
	}

	public <E extends Connection> List<E> ofType(Class<E> clazz) {
		List<E> list = Lists.newArrayList();
		for (Entry<String, D> element : namedElements.entrySet()) {
			if (clazz.isInstance(element.getValue())) {
				list.add(clazz.cast(element.getValue()));
			}
		}
		return list;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(namedElements);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		final AbstractNamedCollection<D, THIS> other = (AbstractNamedCollection<D, THIS>) obj;
		return Objects.equal(this.namedElements, other.namedElements); //
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("elements", namedElements).toString();
	}

}
