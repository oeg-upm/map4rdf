package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;
import net.customware.gwt.dispatch.shared.Action;

public class GetMapsConfiguration extends MultipleConfigurationAction implements Serializable,Action<GetMapsConfigurationResult>{
	
	private static final long serialVersionUID = 9215320988022251054L;

	private GetMapsConfiguration() {
		super("");
		//For seralization
	}
	
	public GetMapsConfiguration(String configID){
		super(configID);
	}
}
