package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class CloseMapMainPopupEvent extends GwtEvent<CloseMapMainPopupHandler>{
	private static GwtEvent.Type<CloseMapMainPopupHandler> TYPE;
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CloseMapMainPopupHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(CloseMapMainPopupHandler handler) {
		handler.closeMapMainPopup();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<CloseMapMainPopupHandler> getType(){
		if (TYPE == null) {
			TYPE = new Type<CloseMapMainPopupHandler>();
		}
		return TYPE;
	}
}
