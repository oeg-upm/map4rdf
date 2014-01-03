package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public class BufferSetPointEvent extends GwtEvent<BufferSetPointHandler>{

	private static  GwtEvent.Type<BufferSetPointHandler> TYPE = null;
	private GeoResource geoResource;
	private Geometry geometry;
	
	public BufferSetPointEvent(GeoResource geoResource, Geometry geometry){
		this.geoResource=geoResource;
		this.geometry=geometry;
		getType();
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<BufferSetPointHandler> getAssociatedType() {
		return getType();
	}

	public static com.google.gwt.event.shared.GwtEvent.Type<BufferSetPointHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<BufferSetPointHandler>();
		}
		return TYPE;
	}

	@Override
	protected void dispatch(BufferSetPointHandler handler) {
		handler.setBufferPoint(geoResource, geometry);
		
	}

}
