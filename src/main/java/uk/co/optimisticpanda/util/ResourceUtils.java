package uk.co.optimisticpanda.util;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.PropertyPlaceholderHelper;

import uk.co.optimisticpanda.conf.ConfigurationException;

import com.google.common.base.Optional;

public enum ResourceUtils {
	;
	private static PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

	public static String toString(Resource resource) {
		try {
			return new Scanner(resource.getInputStream()).useDelimiter("\\A").next();
		} catch (IOException e) {
			throw new ConfigurationException("Problem reading resource name: " + resource.getDescription(), e);
		}
	}

	public static String toStringReplacingProperties(final Resource resource, final Properties properties) {
		return helper.replacePlaceholders(toString(resource), properties);
	}

	public static Properties getProperties(ResourcePatternResolver loader, Optional<String> resourceLocation) {
		try {
			if(!resourceLocation.isPresent()) {
				return new Properties();
			}
			Resource resource = loader.getResource(resourceLocation.get());
			Properties properties = new Properties();
			properties.load(resource.getInputStream());
			return properties;
		} catch (IOException e) {
			throw new ConfigurationException("Problem reading property file: " + resourceLocation.get(), e);
		}
	}

}
