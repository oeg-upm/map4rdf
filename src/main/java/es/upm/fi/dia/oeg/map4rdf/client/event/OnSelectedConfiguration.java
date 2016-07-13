package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class OnSelectedConfiguration extends GwtEvent<OnSelectedConfigurationHandler>{
	
	private static GwtEvent.Type<OnSelectedConfigurationHandler> TYPE;
	private String configID;
	
	public OnSelectedConfiguration(String configID){
		this.configID=configID;
	}
	
	public static GwtEvent.Type<OnSelectedConfigurationHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<OnSelectedConfigurationHandler>();
		}
		return TYPE;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<OnSelectedConfigurationHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(OnSelectedConfigurationHandler handler) {
		handler.onSelectecConfiguration(configID);
	}

}
