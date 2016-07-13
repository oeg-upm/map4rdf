package es.upm.fi.dia.oeg.map4rdf.client.util;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Francisco Siles
 */
public class WidgetLineMove {
	private Widget widget;
	private LeftTopPosition initialWidgetPosition;
	private LeftTopPosition finalWidgetPosition;
	private Integer steps;
	private Unit unit;
	public WidgetLineMove(Widget widget, LeftTopPosition finalWidgetPosition, Unit unit){
		this.widget=widget;
		this.finalWidgetPosition=finalWidgetPosition;
		this.unit=unit;
		this.steps=null;
	}
	public void initWidgetLineMove(int steps) {
		this.steps=steps;
		this.initialWidgetPosition=new LeftTopPosition(
				Integer.parseInt(widget.getElement().getStyle().getLeft().toLowerCase().replace(unit.toString().toLowerCase(), "")), 
				Integer.parseInt(widget.getElement().getStyle().getTop().toLowerCase().replace(unit.toString().toLowerCase(), "")));
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
			widget.getElement().getStyle().setLeft(left, unit);
			widget.getElement().getStyle().setTop(top, unit);
		}
	}
}
