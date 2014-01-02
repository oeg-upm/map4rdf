package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.EventHandler;

import es.upm.fi.dia.oeg.map4rdf.client.util.RoutesAddGeoResourceType;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public interface RoutesAddPointHandler extends EventHandler{
	
	void addRoutePoint(GeoResource geoResource,Geometry geometry, RoutesAddGeoResourceType type);
	
}
