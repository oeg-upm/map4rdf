package es.upm.fi.dia.oeg.map4rdf.client.place;

import net.customware.gwt.presenter.client.place.PlaceRequest;

public class Place extends net.customware.gwt.presenter.client.place.Place{
	String name;
	public Place(String name){
		this.name=name;
	}
	@Override
	public String getName() {
		
		return name;
	}

	@Override
	protected void handleRequest(PlaceRequest request) {
		
	}

	@Override
	protected PlaceRequest prepareRequest(PlaceRequest request) {
		
		PlaceRequest toReturn= new PlaceRequest(name);
		return toReturn;
	}

	@Override
	protected void reveal() {
		
		
	}

}
