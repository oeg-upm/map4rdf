package es.upm.fi.dia.oeg.map4rdf.client.util;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public class GeoResourceGeometry{
	private GeoResource resource;
	private Geometry geometry;
	public GeoResourceGeometry(){
		
	}
	public GeoResourceGeometry(GeoResource resource, Geometry geometry){
		this.resource=resource;
		this.geometry=geometry;
	}
	public GeoResource getResource() {
		return resource;
	}
	public void setResource(GeoResource resource) {
		this.resource = resource;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
}