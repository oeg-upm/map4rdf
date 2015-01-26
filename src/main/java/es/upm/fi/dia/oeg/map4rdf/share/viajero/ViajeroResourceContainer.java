package es.upm.fi.dia.oeg.map4rdf.share.viajero;

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
public class ViajeroResourceContainer extends GeoResource {
	private static final long serialVersionUID = -1456193274576736137L;
	private ArrayList<ViajeroTrip> trips;
	private ArrayList<ViajeroGuide> guides;

	public ViajeroResourceContainer() {
		this.guides = new ArrayList<ViajeroGuide>();
		this.trips = new ArrayList<ViajeroTrip>();
	}

	public ViajeroResourceContainer(String uri, Geometry geometry) {
		super(uri, geometry);
		this.guides = new ArrayList<ViajeroGuide>();
		this.trips = new ArrayList<ViajeroTrip>();
	}
	public void addTrip(ViajeroTrip trip){
		trips.add(trip);
	}
	public void addGuide(ViajeroGuide guide){
		guides.add(guide);
	}
	public void addAllGuides(Collection<ViajeroGuide> guides){
		this.guides.addAll(guides);
	}
	public void addAllTrips(Collection<ViajeroTrip> trips){
		this.trips.addAll(trips);
	}
	public ArrayList<ViajeroTrip> getTrips() {
		return trips;
	}

	public ArrayList<ViajeroGuide> getGuides() {
		return guides;
	}
	public boolean haveGuides(){
		return guides!=null && !guides.isEmpty();
	}
	public boolean haveTrips(){
		return trips!=null && !trips.isEmpty();
	}


}
