package uk.co.optimisticpanda.util;

import java.util.Properties;

import org.springframework.core.io.Resource;

import com.google.common.base.Supplier;


public class JsonProvider implements Supplier<String>{
	private final String json;

	public JsonProvider(Resource json, Properties properties) {
		this.json = ResourceUtils.toStringReplacingProperties(json, properties);
	}

	public String get() {
		return json;
	}

}