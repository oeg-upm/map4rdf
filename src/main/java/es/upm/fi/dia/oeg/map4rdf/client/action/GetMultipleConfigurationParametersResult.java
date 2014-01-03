package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.shared.Result;

public class GetMultipleConfigurationParametersResult implements Result {
	
	private Map<String,String> results= new HashMap<String,String>();
	
	GetMultipleConfigurationParametersResult() {
		//for serialization
	}
	
	public GetMultipleConfigurationParametersResult(Map<String,String> results){
		this.results.putAll(results);
	}
	
	public Map<String,String> getResults() {
		return results;
	}
	
}
