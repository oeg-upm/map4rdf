package es.upm.fi.dia.oeg.map4rdf.client.action;

import net.customware.gwt.dispatch.shared.Action;

public class GetConfigLogotype extends MultipleConfigurationAction implements Action<GetConfigLogotypeResult>{

	private static final long serialVersionUID = -441750404349582739L;

	public GetConfigLogotype(String configID) {
		super(configID);
	}
	
	private GetConfigLogotype(){
		super("");
	}
}
