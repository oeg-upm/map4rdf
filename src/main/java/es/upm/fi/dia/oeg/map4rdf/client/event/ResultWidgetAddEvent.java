package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;


public class ResultWidgetAddEvent extends GwtEvent<ResultWidgetChangeHandler>{
	private static  GwtEvent.Type<ResultWidgetChangeHandler> TYPE = null;
	private Widget widget;
	private String tittle;
	
	public ResultWidgetAddEvent(Widget widget,String tittle){
		this.widget=widget;
		this.tittle=tittle;
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
		handler.addWidget(widget, tittle);
	}
}
