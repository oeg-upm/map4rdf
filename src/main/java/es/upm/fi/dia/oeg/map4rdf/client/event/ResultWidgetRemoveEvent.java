package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class ResultWidgetRemoveEvent extends GwtEvent<ResultWidgetChangeHandler>{
	private static  GwtEvent.Type<ResultWidgetChangeHandler> TYPE = null;
	private Widget widget;
	
	public ResultWidgetRemoveEvent(Widget widget){
		this.widget=widget;
		getType();
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ResultWidgetChangeHandler> getAssociatedType() {
		
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<ResultWidgetChangeHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ResultWidgetChangeHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(ResultWidgetChangeHandler handler) {
		handler.removeWidget(widget);
	}
}
