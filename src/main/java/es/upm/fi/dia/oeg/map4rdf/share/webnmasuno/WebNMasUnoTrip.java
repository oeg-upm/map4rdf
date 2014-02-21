package es.upm.fi.dia.oeg.map4rdf.share.webnmasuno;

import java.io.Serializable;

public class WebNMasUnoTrip extends WebNMasUnoResource implements Serializable {

	private static final long serialVersionUID = 5932164905343611605L;
	private String title;
	private String url;
	private String uri;
	private String idItinerario;
	private String date;
	private String priceLess="";
	private String priceMore="";
	private String durationLess="";
	private String durationMore="";
	private String distanceLess="";
	private String distanceMore="";
	private String description="";
	
	@SuppressWarnings("unused")
	private WebNMasUnoTrip() {
		// For serialization
	}

	public WebNMasUnoTrip(String title, String url, String uri,
			String idItiner, String d) {
		if (title.equals("")) {
			this.title = "No disponible";
		} else {
			this.title = title;
		}
		if (d.equals("")) {
			date = "No disponible";
		} else {
			date = d;
		}
		this.url = url;
		this.uri = uri;
		this.idItinerario = idItiner;
	}

	public String getTitle() {
		return title;
	}

	public String getURL() {
		return url;
	}

	public String getURI() {
		return uri;
	}

	public String getItinerario() {
		return idItinerario;
	}

	public String getDate() {
		return date;
	}

	public String getPriceLess() {
		return priceLess;
	}

	public void setPriceLess(String priceLess) {
		this.priceLess = priceLess;
	}

	public String getPriceMore() {
		return priceMore;
	}

	public void setPriceMore(String priceMore) {
		this.priceMore = priceMore;
	}

	public String getDurationLess() {
		return durationLess;
	}

	public void setDurationLess(String durationLess) {
		this.durationLess = durationLess;
	}

	public String getDurationMore() {
		return durationMore;
	}

	public void setDurationMore(String durationMore) {
		this.durationMore = durationMore;
	}

	public String getDistanceLess() {
		return distanceLess;
	}

	public void setDistanceLess(String distanceLess) {
		this.distanceLess = distanceLess;
	}

	public String getDistanceMore() {
		return distanceMore;
	}

	public void setDistanceMore(String distanceMore) {
		this.distanceMore = distanceMore;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean haveDistanceMore(){
		return distanceMore!=null && !distanceMore.isEmpty();
	}
	public boolean haveDistanceLess(){
		return distanceLess!=null && !distanceLess.isEmpty();
	}
	public boolean haveDurationMore(){
		return durationMore!=null && !durationMore.isEmpty();
	}
	public boolean haveDurationLess(){
		return durationLess!=null && !durationLess.isEmpty();
	}
	public boolean havePriceMore(){
		return priceMore!=null && !priceMore.isEmpty();
	}
	public boolean havePriceLess(){
		return priceLess!=null && !priceLess.isEmpty();
	}
	public boolean haveDescription(){
		return description!=null && !description.isEmpty();
	}

}
