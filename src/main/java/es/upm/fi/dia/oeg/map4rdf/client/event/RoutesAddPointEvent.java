package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

import es.upm.fi.dia.oeg.map4rdf.client.util.RoutesAddGeoResourceType;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public class RoutesAddPointEvent extends GwtEvent<RoutesAddPointHandler>{

	private static  GwtEvent.Type<RoutesAddPointHandler> TYPE = null;
	private GeoResource geoResource;
	private Geometry geometry;
	private RoutesAddGeoResourceType type;
	
	public RoutesAddPointEvent(GeoResource geoResource, Geometry geometry,RoutesAddGeoResourceType type){
		this.geoResource=geoResource;
		this.geometry=geometry;
		this.type=type;
		getType();
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<RoutesAddPointHandler> getAssociatedType() {
		
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<RoutesAddPointHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<RoutesAddPointHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(RoutesAddPointHandler handler) {
		handler.addRoutePoint(geoResource, geometry,type);
		
	}

}
