package uk.co.optimisticpanda.util;

import java.io.FileNotFoundException;
import java.io.Writer;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import uk.co.optimisticpanda.conf.ConfigurationException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateApplier {
	private Configuration configuration;
	private StringTemplateLoader stringTemplateLoader;
	private Logger log = Logger.getLogger(TemplateApplier.class);
	
	public TemplateApplier() {
		this.configuration = new Configuration();
		this.stringTemplateLoader = new StringTemplateLoader();
		this.configuration.setTemplateLoader(stringTemplateLoader);
	}

	public void addTemplate(String name, Resource resource) {
		String template = ResourceUtils.toString(resource);
		if(stringTemplateLoader.findTemplateSource(name)!= null) {
			log.warn("Overiding template named:" + name);
		}
		stringTemplateLoader.putTemplate(name, template);
	}
	
	public void apply(String templateName, Writer writer, Map<String, Object> model){
		try {
			try {
				Template template = configuration.getTemplate(templateName);
				template.process(model, writer);
			} finally {
				writer.close();
			}
		} catch (TemplateException ex) {
			throw new ConfigurationException(ex.getMessage(), ex);
		} catch (FileNotFoundException ex) {
			throw new ConfigurationException("Could not find template named "
					+ templateName, ex);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

}
