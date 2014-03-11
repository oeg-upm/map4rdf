package es.upm.fi.dia.oeg.map4rdf.server.bootstrap;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.FacetedBrowserConfiguration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.GetServletContext;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserActionHandlerModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserConfigModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserModule;
import es.upm.fi.dia.oeg.map4rdf.server.inject.BrowserServletModule;

/**
 * @author Alexander De Leon
 */
public class Bootstrapper extends GuiceServletContextListener {

	private static final Logger LOG = Logger.getLogger(Bootstrapper.class.getName());
	private Configuration config;
	private FacetedBrowserConfiguration facetedBrowserConfiguration;
	private GetServletContext getServletContext;
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		this.getServletContext= new GetServletContext(servletContextEvent.getServletContext());
		InputStream propIn = servletContextEvent.getServletContext().getResourceAsStream(Constants.CONFIGURATION_FILE);
        try {
            config = new Configuration(propIn);
        } catch (IOException ex) {
        	LOG.fatal(ex);
        }
		// add config to servlet context so it can be accessed in JSPs
        servletContextEvent.getServletContext().setAttribute(Configuration.class.getName(), config);
		

		InputStream facetConfigIn = servletContextEvent.getServletContext().getResourceAsStream(
				Constants.FACET_CONFIGURATION_FILE);
		try {
			facetedBrowserConfiguration = new FacetedBrowserConfiguration(facetConfigIn);
		} catch (Exception e) {
			LOG.fatal("Unable to load faceted browser configuration file",e);
			throw new RuntimeException(e);
		}
		super.contextInitialized(servletContextEvent);
	}
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		servletContextEvent.getServletContext().removeAttribute(Configuration.class.getName());
	}

	@Override
	protected Injector getInjector() {

		return Guice.createInjector(new BrowserModule(), new BrowserConfigModule(config, facetedBrowserConfiguration,getServletContext),
				new BrowserServletModule(), new BrowserActionHandlerModule());
	}
	
}
