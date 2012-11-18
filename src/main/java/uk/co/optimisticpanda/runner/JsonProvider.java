package uk.co.optimisticpanda.runner;

import java.util.Properties;

import org.springframework.core.io.Resource;

import com.google.common.base.Supplier;

import uk.co.optimisticpanda.util.ResourceUtils;

public class JsonProvider implements Supplier<String>{
	private final String json;

	public JsonProvider(Resource json, Properties properties) {
		this.json = ResourceUtils.toStringReplacingProperties(json, properties);
	}

	public String get() {
		return json;
	}

}