package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

public class StatisticsSummaryEvent extends GwtEvent<StatisticsSummaryEventHandler>{
	private static GwtEvent.Type<StatisticsSummaryEventHandler> TYPE;
	private GeoResource geoResource;
	private boolean open;
	public StatisticsSummaryEvent(boolean open, GeoResource resource){
		this.open=open;
		this.geoResource=resource;
	}
	public GeoResource getGeoResource() {
		return geoResource;
	}

	public void setGeoResource(GeoResource geoResource) {
		this.geoResource = geoResource;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	public static GwtEvent.Type<StatisticsSummaryEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<StatisticsSummaryEventHandler>();
		}
		return TYPE;
	}
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<StatisticsSummaryEventHandler> getAssociatedType() {
		
		return getType();
	}

	@Override
	protected void dispatch(StatisticsSummaryEventHandler handler) {
		
		handler.onStatisticsSummary(this);
	}

}
