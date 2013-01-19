package uk.co.optimisticpanda.runner;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import uk.co.optimisticpanda.conf.ConnectionDefinition;
import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.conf.TypeAdaptorRegistration;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

//A class that gathers all registered extensions
public class RegisteredExtensions implements InitializingBean {

	@Autowired
	private ApplicationContext applicationContext;
	private BiMap<String, Class<? extends ConnectionDefinition>> nameToConnectionType = HashBiMap.create();
	private BiMap<String, Class<? extends Phase>> nameToPhaseType = HashBiMap.create();
	private List<TypeAdaptorRegistration<?>> adaptorList = Lists.newArrayList();
	
	public void afterPropertiesSet() throws Exception {
		for (RegisterExtension extension : applicationContext.getBeansOfType(RegisterExtension.class).values()) {
			for (ConnectionRegistration registration : extension.connections) {
				nameToConnectionType.put(registration.getConnectionName(), registration.getConnectionType());
			}
			for (PhaseRegistration registration : extension.phases) {
				nameToPhaseType.put(registration.getPhaseName(), registration.getPhaseType());
			}
			for (TypeAdaptorRegistration<?> registration : extension.typeAdaptors) {
				adaptorList.add(registration);
			}
		}
	}

	public List<TypeAdaptorRegistration<?>> getAdaptorList() {
		return adaptorList;
	}

	public String getByPhaseType(Class<? extends Phase> type) {
		return nameToPhaseType.inverse().get(type);
	}
	
	public Class<? extends Phase> getPhaseTypeForName(String name) {
		return nameToPhaseType.get(name);
	}
	
	public Set<String> getPhaseNames() {
		return nameToPhaseType.keySet();
	}

	public Class<? extends ConnectionDefinition> getConnectionTypeForName(String name) {
		return nameToConnectionType.get(name);
	}
	public String getByConnectionType(Class<? extends Phase> type) {
		return nameToConnectionType.inverse().get(type);
	}
	
	public Set<String> getConnectionNames() {
		return nameToConnectionType.keySet();
	}

	public static class RegisterExtension {
		public List<PhaseRegistration> phases;
		public List<ConnectionRegistration> connections;
		public List<TypeAdaptorRegistration<?>> typeAdaptors;

		public RegisterExtension phaseTypes(PhaseRegistration... phases) {
			this.phases = Arrays.asList(phases);
			return this;
		}

		public RegisterExtension connectionTypes(ConnectionRegistration... connections) {
			this.connections = Arrays.asList(connections);
			return this;
		}

		public RegisterExtension typeAdaptors(TypeAdaptorRegistration<?>... typeAdaptors) {
			this.typeAdaptors = Arrays.asList(typeAdaptors);
			return this;
		}
	}

	public static class PhaseRegistration {

		private final String phaseName;
		private final Class<? extends Phase> phaseType;

		public PhaseRegistration(String phaseName, Class<? extends Phase> phaseType) {
			this.phaseName = phaseName;
			this.phaseType = phaseType;
		}

		public String getPhaseName() {
			return phaseName;
		}

		public Class<? extends Phase> getPhaseType() {
			return phaseType;
		}
	}

	public static class ConnectionRegistration {

		private final String connectionName;
		private final Class<? extends ConnectionDefinition> connectionType;

		public ConnectionRegistration(String connectionName, Class<? extends ConnectionDefinition> connectionType) {
			this.connectionName = connectionName;
			this.connectionType = connectionType;
		}

		public String getConnectionName() {
			return connectionName;
		}

		public Class<? extends ConnectionDefinition> getConnectionType() {
			return connectionType;
		}
	}
}
