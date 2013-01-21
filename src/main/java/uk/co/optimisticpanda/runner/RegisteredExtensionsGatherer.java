package uk.co.optimisticpanda.runner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.google.common.collect.Maps;

//A class that gathers all registered extensions
public class RegisteredExtensionsGatherer implements InitializingBean {

	@Autowired
	private ApplicationContext applicationContext;
	private BiMap<String, Class<? extends ConnectionDefinition>> nameToConnectionType = HashBiMap.create();
	private BiMap<String, Class<? extends Phase>> nameToPhaseType = HashBiMap.create();
	private List<TypeAdaptorRegistration<?>> adaptorList = Lists.newArrayList();
	
	public void afterPropertiesSet() throws Exception {
		for (RegisterExtension extension : applicationContext.getBeansOfType(RegisterExtension.class).values()) {
			nameToConnectionType.putAll(extension.getConnectionTypes());
			nameToPhaseType.putAll(extension.getPhaseTypes());
			adaptorList.addAll(extension.typeAdaptors);
		}
	}

	public List<TypeAdaptorRegistration<?>> getAdaptorList() {
		return adaptorList;
	}

	public String getByPhaseType(Class<? extends Phase> type) {
		return nameToPhaseType.inverse().get(type);
	}
	
	public Class<? extends Phase> getPhaseTypeForName(String phaseName) {
		return nameToPhaseType.get(phaseName);
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
		public List<RegisteredComponent<Phase>> phases;
		public List<RegisteredComponent<ConnectionDefinition>> connections;
		public List<TypeAdaptorRegistration<?>> typeAdaptors;

		public RegisterExtension phaseTypes(RegisteredComponent<Phase>... phases) {
			this.phases = Arrays.asList(phases);
			return this;
		}

		public RegisterExtension connectionTypes(RegisteredComponent<ConnectionDefinition>... connections) {
			this.connections = Arrays.asList(connections);
			return this;
		}

		public RegisterExtension typeAdaptors(TypeAdaptorRegistration<?>... typeAdaptors) {
			this.typeAdaptors = Arrays.asList(typeAdaptors);
			return this;
		}
		
		public Map<String,Class<ConnectionDefinition>> getConnectionTypes(){
			return getMap(connections);
		}
		
		public Map<String,Class<Phase>> getPhaseTypes(){
			return getMap(phases);
		}
		
		@SuppressWarnings("unchecked")
		private <D> Map<String, Class<D>> getMap(List<RegisteredComponent<D>> elems){
			HashMap<String, Class<D>> map = Maps.newHashMap();
			for (RegisteredComponent<D> registeredComponent : elems) {
				map.put(registeredComponent.name, (Class<D>) registeredComponent.type);
			}
			return map;
		}
	}

	public static class RegisteredComponent<T> {

		private final String name;
		private final Class<? extends T> type;

		public RegisteredComponent(String name, Class<? extends T> type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public Class<? extends T> getType() {
			return type;
		}
	}
}
