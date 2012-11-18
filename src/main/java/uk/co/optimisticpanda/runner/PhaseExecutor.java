package uk.co.optimisticpanda.runner;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import uk.co.optimisticpanda.conf.Phase;

public class PhaseExecutor implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public void execute(Phase phase) {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(phase);
		phase.execute();
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
