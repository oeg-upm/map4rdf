package es.upm.fi.dia.oeg.map4rdf.share.aemet;

import java.util.ArrayList;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

/**
 * 
 * @author Daniel Garijo
 * 
 */
public class AemetResource extends GeoResource {
	
	private static final long serialVersionUID = -2502696820812385994L;
	
	private ArrayList<AemetObs> obs; // prueba, quizas sea mejor en hashmap

	public AemetResource() {
		// for serialization
	}

	public AemetResource(String uri, Geometry geometry) {
		super(uri, geometry);
		obs = new ArrayList<AemetObs>();
	}

	public ArrayList<AemetObs> getObs() {
		return obs;
	}

	public void addObs(AemetObs observ) {
		obs.add(observ);
	}

}
