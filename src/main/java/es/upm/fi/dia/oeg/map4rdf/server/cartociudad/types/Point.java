package es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types;

public class Point {

	String lat;
	String lon;

	public Point(String lat, String lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
	}
	
	@Override
	public String toString(){
		return lat+","+lon;
		
	}

}
