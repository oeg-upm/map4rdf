package es.upm.fi.dia.oeg.map4rdf.client.action;


import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public class GetBufferGeoResources implements Action<GetBufferGeoResourcesResult> {
	private String geoResourceUri;
	private double radiousKM;
	private Geometry geometry;
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public GetBufferGeoResources(){
		
	}
	public GetBufferGeoResources(String geoResourceUri, Geometry geometry, double radiousKM){
		this.geoResourceUri=geoResourceUri;
		this.geometry=geometry;
		this.radiousKM=radiousKM;
	}
	public String getGeoResourceUri() {
		return geoResourceUri;
	}
	public void setGeoResourceUri(String geoResourceUri) {
		this.geoResourceUri = geoResourceUri;
	}
	public double getRadiousKM() {
		return radiousKM;
	}
	public void setRadiousKM(double radiousKM) {
		this.radiousKM = radiousKM;
	}


}
