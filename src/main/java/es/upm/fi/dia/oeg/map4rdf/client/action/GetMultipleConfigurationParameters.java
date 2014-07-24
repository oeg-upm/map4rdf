package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Action;

public class GetMultipleConfigurationParameters extends MultipleConfigurationAction implements Serializable,Action<GetMultipleConfigurationParametersResult> {

	private static final long serialVersionUID = 8699605548911102161L;
	
	private List<String> parameters=new ArrayList<String>();

	private GetMultipleConfigurationParameters() {
		super("");
		//For serialization
	}

	public GetMultipleConfigurationParameters(String configID,List<String> parameters) {
		super(configID);
		this.parameters.addAll(parameters);
	}
	
	public List<String> getParameters() {
		return this.parameters;
	}
}
