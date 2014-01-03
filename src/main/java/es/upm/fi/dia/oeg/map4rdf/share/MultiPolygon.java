package es.upm.fi.dia.oeg.map4rdf.share;

import java.util.List;

public interface MultiPolygon extends Geometry{
	List<Polygon> getPolygons();
}
