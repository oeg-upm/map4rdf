package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.ConfigurationDescription;

public class GetAllConfigurationsDescriptionResult implements Serializable,Result{
	
	private static final long serialVersionUID = 5533821523613584589L;
	
	private List<ConfigurationDescription> configurations;
	public GetAllConfigurationsDescriptionResult(List<ConfigurationDescription> configurations){
		this.configurations=configurations;
	}
	public GetAllConfigurationsDescriptionResult(){
		this.configurations = new ArrayList<ConfigurationDescription>();
	}
	
	public List<ConfigurationDescription> getConfigurationsDescription(){
		return configurations;
	}
}
