package es.upm.fi.dia.oeg.map4rdf.client.util;

import org.gwtopenmaps.openlayers.client.LonLat;

import com.google.gwt.event.dom.client.ClickEvent;

public class FeatureClickEvent extends ClickEvent{
	private LonLat lonLat;
	public FeatureClickEvent(LonLat lonLat) {
		super();
		this.lonLat = lonLat;
	}
	
	public LonLat getClickedLonLat(){
		return lonLat;
	}
}
