package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class DashboardDoSelectedResultWidgetEvent extends GwtEvent<DashboardDoSelectedResultWidgetHandler>{

	private static  GwtEvent.Type<DashboardDoSelectedResultWidgetHandler> TYPE = null;
	
	public DashboardDoSelectedResultWidgetEvent(){
		getType();
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DashboardDoSelectedResultWidgetHandler> getAssociatedType() {
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<DashboardDoSelectedResultWidgetHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<DashboardDoSelectedResultWidgetHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(DashboardDoSelectedResultWidgetHandler handler) {
		handler.doSelectedResultWidget();
	}
}
