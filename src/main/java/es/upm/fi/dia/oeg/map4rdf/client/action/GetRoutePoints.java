package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.util.List;

import es.upm.fi.dia.oeg.map4rdf.share.Point;

import net.customware.gwt.dispatch.shared.Action;

public class GetRoutePoints implements Action<GetRoutePointsResult>{
	private List<Point> points;
	public GetRoutePoints(){
		
	}
	public GetRoutePoints(List<Point> points){
		this.points = points;
	}
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
}
