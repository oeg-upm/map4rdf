package es.upm.fi.dia.oeg.map4rdf.server.conf;

import javax.servlet.ServletContext;

public class GetServletContext {
	private ServletContext servletContext;
	public GetServletContext(ServletContext servletContext){
		this.servletContext=servletContext;
	}
	public ServletContext getServletContext() {
		return servletContext;
	}
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
