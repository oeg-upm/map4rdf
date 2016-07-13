package es.upm.fi.dia.oeg.map4rdf.server.bootstrap;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import es.upm.fi.dia.oeg.map4rdf.server.conf.GetServletContext;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserActionHandlerModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserConfigModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserServletModule;

/**
 * @author Alexander De Leon
 */
public class Bootstrapper extends GuiceServletContextListener {

	private static final Logger LOG = Logger.getLogger(Bootstrapper.class.getName());
	private	MultipleConfigurations multipleConfigs;
	private GetServletContext getServletContext;
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try{
			this.getServletContext= new GetServletContext(servletContextEvent.getServletContext());
        	multipleConfigs = new MultipleConfigurations(getServletContext);

        	// add config to servlet context so it can be accessed in JSPs
        	servletContextEvent.getServletContext().setAttribute(MultipleConfigurations.class.getName(), multipleConfigs);
        
			super.contextInitialized(servletContextEvent);
		}catch(Exception e){
			LOG.fatal("Can't obtain configuration.",e);
		}
	}
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().removeAttribute(MultipleConfigurations.class.getName());
	}

	@Override
	protected Injector getInjector() {

		return Guice.createInjector(new BrowserModule(), new BrowserConfigModule(multipleConfigs),
				new BrowserServletModule(), new BrowserActionHandlerModule());
	}
	
}
