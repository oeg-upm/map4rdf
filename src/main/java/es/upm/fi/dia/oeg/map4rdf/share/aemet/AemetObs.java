package es.upm.fi.dia.oeg.map4rdf.share.aemet;

import java.io.Serializable;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;

public class AemetObs implements Serializable {

	private static final long serialVersionUID = -977389897535645824L;
	
	// private String idObs;
	private String uriObs;
	private Resource estacion;
	private double valor;
	private String calidad;
	private Resource propiedad;
	private String featureOfInterest;
	private AemetIntervalo intervalo;

	public AemetObs() {

	}

	public AemetObs(String uriObs, Resource est, double valor, String calidad, Resource prop, String feature,
			AemetIntervalo inter) {

		this.uriObs = uriObs;
		estacion = est;
		this.valor = valor;
		this.calidad = calidad;
		propiedad = prop;
		featureOfInterest = feature;
		intervalo = inter;
	}

	public String getUriObs() {
		return uriObs;
	}

	public Resource getEstacion() {
		return estacion;
	}

	public String getCalidad() {
		return calidad;
	}

	public String getFeatureOfInterest() {
		return featureOfInterest;
	}

	public AemetIntervalo getIntervalo() {
		return intervalo;
	}

	public Resource getPropiedad() {
		return propiedad;
	}

	public double getValor() {
		return valor;
	}
}
