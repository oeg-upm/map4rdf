package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.shared.Result;
import es.upm.fi.dia.oeg.map4rdf.share.Point;

public class GetRoutePointsResult implements Result{

	private List<Point> points;
	public GetRoutePointsResult(){
		points = new ArrayList<Point>();
	}
	public GetRoutePointsResult(List<Point> points){
		this.points = points;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
