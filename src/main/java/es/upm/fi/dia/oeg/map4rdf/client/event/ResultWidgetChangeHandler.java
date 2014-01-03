package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.Widget;

public interface ResultWidgetChangeHandler extends EventHandler{
	void addWidget(Widget widget, String tittle);
	void doSelectedWidget(Widget widget);
	void removeWidget(Widget widget);
}
