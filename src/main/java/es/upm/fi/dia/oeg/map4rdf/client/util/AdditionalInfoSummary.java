package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.util.Map;

public class AdditionalInfoSummary {
	private Map<String,String> additionalInfo;
	private boolean haveImage;
	private String image;
	public AdditionalInfoSummary(Map<String,String> additionalInfo){
		this.additionalInfo=additionalInfo;
		this.haveImage=false;
		this.image="";
	}
	public Map<String, String> getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(Map<String, String> additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public boolean haveImage() {
		return haveImage;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
		this.haveImage = true;
	}
	
}
