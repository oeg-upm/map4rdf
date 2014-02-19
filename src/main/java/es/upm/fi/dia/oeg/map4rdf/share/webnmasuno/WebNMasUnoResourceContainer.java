package es.upm.fi.dia.oeg.map4rdf.share.webnmasuno;

import java.util.ArrayList;
import java.util.Collection;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

/**
 * 
 * @author Daniel Garijo
 * Adapted by: @author Francisco Siles
 * 
 */
public class WebNMasUnoResourceContainer extends GeoResource {
	private static final long serialVersionUID = -1456193274576736137L;
	private ArrayList<WebNMasUnoTrip> trips;
	private ArrayList<WebNMasUnoGuide> guides;

	public WebNMasUnoResourceContainer() {
		this.guides = new ArrayList<WebNMasUnoGuide>();
		this.trips = new ArrayList<WebNMasUnoTrip>();
	}

	public WebNMasUnoResourceContainer(String uri, Geometry geometry) {
		super(uri, geometry);
		this.guides = new ArrayList<WebNMasUnoGuide>();
		this.trips = new ArrayList<WebNMasUnoTrip>();
	}
	public void addTrip(WebNMasUnoTrip trip){
		trips.add(trip);
	}
	public void addGuide(WebNMasUnoGuide guide){
		guides.add(guide);
	}
	public void addAllGuides(Collection<WebNMasUnoGuide> guides){
		this.guides.addAll(guides);
	}

	public ArrayList<WebNMasUnoTrip> getTrips() {
		return trips;
	}

	public ArrayList<WebNMasUnoGuide> getGuides() {
		return guides;
	}
	public boolean haveGuides(){
		return guides!=null && !guides.isEmpty();
	}
	public boolean haveTrips(){
		return trips!=null && !trips.isEmpty();
	}


}
