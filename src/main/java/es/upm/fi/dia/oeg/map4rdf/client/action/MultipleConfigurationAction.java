package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;

public class MultipleConfigurationAction implements Serializable{
	private static final long serialVersionUID = 4722755906035894868L;
	private String configID;
	
	public MultipleConfigurationAction(String configID){
		this.configID = configID;
	}
	
	public String getConfigID() {
		return configID;
	}

	public void setConfigID(String configID) {
		this.configID = configID;
	}
}
