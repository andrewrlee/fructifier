package uk.co.optimisticpanda.runner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.conf.Phase;
import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.db.conf.DatabaseConfiguration;
import uk.co.optimisticpanda.util.JsonProvider;
import uk.co.optimisticpanda.util.ResourceUtils;

import com.google.common.base.Optional;

public class Runner {

	private AnnotationConfigApplicationContext context;

	public Runner(String jsonResourceLocation, Optional<String> propertyResourceLocation) {
		context = new AnnotationConfigApplicationContext();
		
		Resource json = context.getResource(jsonResourceLocation);
		Properties properties = ResourceUtils.getProperties(context, propertyResourceLocation);
		
		context.getBeanFactory().registerSingleton("json", new JsonProvider(json, properties));
		context.register(BaseConfiguration.class);
		context.register(DatabaseConfiguration.class);
		context.refresh();
	} 
	
	public Runner(String jsonResourceLocation) {
		this(jsonResourceLocation, Optional.<String>absent());
	}
	
	public void runProfile(String profileName) {
		context.getBean(RunningOrder.class).executeProfile(profileName);
	}
	
	public void run(String... phases) {
		context.getBean(RunningOrder.class).execute(phases);
	}

	public Map<String, List<String>> getProfiles() {
		return context.getBean(RunningOrder.class).getProfiles();
	}
	public LinkedHashMap<String, Phase> getPhases() {
		return context.getBean(RunningOrder.class).getPhases();
	}

	public String getScript() {
		return context.getBean(JsonProvider.class).get();
	}

}
