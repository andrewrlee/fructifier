package uk.co.optimisticpanda.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;

import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.conf.serializing.Serializer;

/**
 * We wire up this and other child contexts. After properties are set we provide
 * all registered plugins to the config. This allows us to deserialize the json
 * into the running order.
 */
@Configuration
public class BaseConfiguration {

	/**
	 * Provides the serialised {@link RunningOrder}
	 */
	@Autowired
	public JsonProvider jsonProvider;
	@Autowired
	public ResourceLoader resourceLoader;

	/**
	 * Deserialises the provided json into a {@link RunningOrder} based on
	 * registered extensions.
	 */
	@Bean
	@DependsOn("registeredExtensions")
	public RunningOrder runningOrder() {
		Serializer serializer = new Serializer(resourceLoader, registeredExtensions());
		return serializer.parseRunningOrder(jsonProvider.get());
	}

	/**
	 * Gathers all registered extensions from child contexts.
	 */
	@Bean(name = "registeredExtensions")
	public RegisteredExtensions registeredExtensions() {
		return new RegisteredExtensions();
	}

}
