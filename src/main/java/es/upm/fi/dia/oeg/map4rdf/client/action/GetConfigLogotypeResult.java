package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;

import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterDefaults;
import net.customware.gwt.dispatch.shared.Result;

public class GetConfigLogotypeResult implements Serializable,Result{
	
	private static final long serialVersionUID = 6755062633007068017L;
	
	private String logo;
	
	public GetConfigLogotypeResult(String logo){
		this.logo = logo;
	}
	
	@SuppressWarnings("unused")
	private GetConfigLogotypeResult(){
		//For serialization
		this.logo = ParameterDefaults.LOGO_IMG_SRC;
	}
	
	public String getLogo(){
		return logo;
	}
}
