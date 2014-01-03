package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.util.List;

import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import net.customware.gwt.dispatch.shared.Result;

public class GetBufferGeoResourcesResult implements Result{
	private List<GeoResource> listGeoResources;
	private BoundingBox boundingBox;
	public GetBufferGeoResourcesResult() {
		
	}
	public GetBufferGeoResourcesResult(List<GeoResource> listGeoResources,BoundingBox boundingBox){
		this.listGeoResources=listGeoResources;
		this.boundingBox=boundingBox;
	}
	public List<GeoResource> getListGeoResources() {
		return listGeoResources;
	}
	public void setListGeoResources(List<GeoResource> listGeoResources) {
		this.listGeoResources = listGeoResources;
	}
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	
}
