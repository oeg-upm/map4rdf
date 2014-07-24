package es.upm.fi.dia.oeg.map4rdf.client.action;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import net.customware.gwt.dispatch.shared.Action;



public class GetAddInfoConfig extends MultipleConfigurationAction implements Action<SingletonResult<GetAddInfoConfigResult>>{
	private GeoResource resource;
	public GetAddInfoConfig(String configID, GeoResource resource){
		super(configID);
		this.resource=resource;
	}
	private GetAddInfoConfig(){	
		super("");
	}
	public GeoResource getResource() {
		return resource;
	}
	public void setResource(GeoResource resource) {
		this.resource = resource;
	}
}
