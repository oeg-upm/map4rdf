package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPolygonBean implements MultiPolygon, Serializable {

	private static final long serialVersionUID = -7013129176404871397L;
	private ArrayList<Polygon> polygons;
	private String uri;
	MultiPolygonBean() {
		
	}

	public MultiPolygonBean(String uri, Polygon... polygons) {
		this.uri=uri;
		this.polygons = new ArrayList<Polygon>(Arrays.asList(polygons));
	}

	public MultiPolygonBean(String uri, List<Polygon> polygons) {
		this.polygons= new ArrayList<Polygon>(polygons);
		this.uri = uri;
	}

	@Override
	public String getUri() {
		return uri;
	}

	@Override
	public List<Point> getPoints() {
		List<Point> points= new ArrayList<Point>();
		if(polygons!=null){
			for(Polygon polygon:polygons){
				points.addAll(polygon.getPoints());
			}
		}
		return points;
	}

	@Override
	public Type getType() {
		return Type.MULTIPOLYGON;
	}

	@Override
	public List<Polygon> getPolygons() {
		return polygons;
	}

}
