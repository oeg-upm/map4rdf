package es.upm.fi.dia.oeg.map4rdf.client.conf;

import com.google.gwt.user.client.Window;

import es.upm.fi.dia.oeg.map4rdf.client.util.ConfigurationUtil;


public class ConfIDImpl implements ConfIDInterface{
	public String configID="";
	public ConfIDImpl(){
		try{
			String parameters[]=Window.Location.getQueryString().substring(1).split("&");
				for (String param : parameters) {
					final String[] parts = param.split("=");
					if (parts[0].toLowerCase().trim().equals(ConfigurationUtil.CONFIGURATION_ID.toLowerCase().trim()) &&
							parts.length>=2) {
						configID=parts[1];
					}
				}
			}catch(Exception e){
			}
	}
	@Override
	public String getConfigID() {
		return configID;
	}
	@Override
	public void setConfigID(String newConfigID) {
		this.configID=newConfigID;
	}
	@Override
	public boolean existsConfigID() {
		return configID!=null && !configID.isEmpty();
	}
	
}
