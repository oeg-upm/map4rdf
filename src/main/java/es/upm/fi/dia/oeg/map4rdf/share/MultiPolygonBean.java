package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPolygonBean implements MultiPolygon, Serializable {

	private static final long serialVersionUID = -7013129176404871397L;
	private ArrayList<Polygon> polygons;
	private String uri;
	private String projection;
	MultiPolygonBean() {
		
	}

	public MultiPolygonBean(String uri,String projection, Polygon... polygons) {
		this.uri=uri;
		this.polygons = new ArrayList<Polygon>(Arrays.asList(polygons));
		this.projection = projection;
	}

	public MultiPolygonBean(String uri, List<Polygon> polygons, String projection) {
		this.polygons= new ArrayList<Polygon>(polygons);
		this.uri = uri;
		this.projection = projection;
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

	@Override
	public String getProjection() {
		return projection;
	}

}
