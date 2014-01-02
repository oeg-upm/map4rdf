package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.EventHandler;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public interface BufferSetPointHandler extends EventHandler{
	
	void setBufferPoint(GeoResource geoResource,Geometry geometry);
	
}
