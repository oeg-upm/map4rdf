package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class FacetReloadEvent extends GwtEvent<FacetReloadHandler> {
	
	private static GwtEvent.Type<FacetReloadHandler> TYPE;
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<FacetReloadHandler> getAssociatedType() {
		return getType();
	}
	
	public static GwtEvent.Type<FacetReloadHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<FacetReloadHandler>();
		}
		return TYPE;
	}
	
	@Override
	protected void dispatch(FacetReloadHandler handler) {
		handler.onFacetReload();
	}

}
