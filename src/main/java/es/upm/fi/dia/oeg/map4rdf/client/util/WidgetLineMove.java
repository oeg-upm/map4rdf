package es.upm.fi.dia.oeg.map4rdf.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Francisco Siles
 */
public class WidgetLineMove {
	private Widget widget;
	private LeftTopPosition initialWidgetPosition;
	private LeftTopPosition finalWidgetPosition;
	private Integer steps;
	private String unit;
	public WidgetLineMove(Widget widget, LeftTopPosition finalWidgetPosition, String unit){
		this.widget=widget;
		this.finalWidgetPosition=finalWidgetPosition;
		this.unit=unit;
		this.steps=null;
	}
	public void initWidgetLineMove(int steps) {
		this.steps=steps;		
		this.initialWidgetPosition=new LeftTopPosition(
				Integer.parseInt(DOM.getStyleAttribute(widget.getElement(), "left").replace(unit, "")), 
				Integer.parseInt(DOM.getStyleAttribute(widget.getElement(), "top").replace(unit, "")));
	}
	public void doStep(int i) throws Exception{
		if(steps==null){
			throw new Exception("Steps in "+this.getClass().getName()+" wasn't initialized.");
		}
		if(steps<0){
			throw new Exception("Steps in "+this.getClass().getName()+" can't be negative.");
		}
		if(i<=steps){
			double moveLeft=(double)(finalWidgetPosition.getLeft()-initialWidgetPosition.getLeft())/(double)steps;
			int left=(int)(initialWidgetPosition.getLeft()+(i*moveLeft));
			double moveTop=(double)(finalWidgetPosition.getTop()-initialWidgetPosition.getTop())/(double)steps;
			int top=(int)(initialWidgetPosition.getTop()+(i*moveTop));
			DOM.setStyleAttribute(widget.getElement(), "left", left+unit);
			DOM.setStyleAttribute(widget.getElement(), "top", top+unit);
		}
	}
}
