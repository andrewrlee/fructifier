package uk.co.optimisticpanda.runner;

import java.util.Properties;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import com.google.common.base.Optional;

import uk.co.optimisticpanda.conf.RunningOrder;
import uk.co.optimisticpanda.db.conf.DatabaseConfiguration;
import uk.co.optimisticpanda.util.ResourceUtils;

public class Runner {

	private RunningOrder runningOrder;

	public Runner(String jsonResourceLocation, Optional<String> propertyResourceLocation) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		
		Resource json = context.getResource(jsonResourceLocation);
		Properties properties = ResourceUtils.getProperties(context, propertyResourceLocation);
		
		context.getBeanFactory().registerSingleton("json", new JsonProvider(json, properties));
		context.register(BaseConfiguration.class);
		context.register(DatabaseConfiguration.class);
		context.refresh();
		runningOrder = context.getBean(RunningOrder.class);
	}
	
	public Runner(String jsonResourceLocation) {
		this(jsonResourceLocation, Optional.<String>absent());
	}
	
	public void run(String... phasesToRun) {
		runningOrder.execute(phasesToRun);
	}

}
