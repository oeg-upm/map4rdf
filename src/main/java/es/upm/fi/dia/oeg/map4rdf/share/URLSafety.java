package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

import com.google.gwt.http.client.URL;

public class URLSafety implements Serializable{
	
	private String url;
	
	
	public URLSafety() {
		
	}
	
	public URLSafety(String url) {
		this.url = url;
	}
	

	public String getUrlSafty() {
		return url.replaceAll(",","%2C").replaceAll("\\^", "%5E").replaceAll(" ","%20");
	}

	public String getUrl() {
		return URL.decode(url).toString().replaceAll("%2C",",").replaceAll("%5E", "\\^");
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
