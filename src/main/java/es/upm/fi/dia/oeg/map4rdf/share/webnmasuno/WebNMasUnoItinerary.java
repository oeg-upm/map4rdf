package es.upm.fi.dia.oeg.map4rdf.share.webnmasuno;

import java.io.Serializable;
import java.util.ArrayList;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

/**
 *
 * @author Daniel Garijo
 */
public class WebNMasUnoItinerary extends GeoResource implements Serializable {
	private static final long serialVersionUID = -1215755446891911826L;
	private ArrayList<String> titulosViajes; //titulos de los Viajes a los que pertenece este itinerario.
    public WebNMasUnoItinerary() {
        titulosViajes = new ArrayList<String>();
    }

     public WebNMasUnoItinerary(String uri, Geometry geometry) {
         super(uri, geometry);
         titulosViajes = new ArrayList<String>();
    }

    public ArrayList<String> getViajes() {
        return titulosViajes;
    }

    public void setViajes(ArrayList<String> p){
        titulosViajes = p;
    }

    public void addViaje (String titulo){
        titulosViajes.add(titulo);
    }

}
