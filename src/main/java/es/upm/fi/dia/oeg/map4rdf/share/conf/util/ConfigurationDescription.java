package es.upm.fi.dia.oeg.map4rdf.share.conf.util;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.dev.util.collect.HashMap;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;

public class ConfigurationDescription extends Resource implements Serializable{
	
	private static final long serialVersionUID = 1174274854533905981L;
	
	private Map<String,String> descriptions = new HashMap<String,String>();
	private String image = "";

	public ConfigurationDescription(String id){
		super(id);
		this.descriptions = new HashMap<String,String>();
	}

	public Map<String, String> getDescription() {
		return descriptions;
	}

	public void addDescription(String locale,String description) {
		this.descriptions.put(locale, description);
	}

	public String getId() {
		return super.getUri();
	}
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public boolean hasImage(){
		return image!=null && !image.isEmpty();
	}
	
	public boolean hasDescription(){
		return descriptions!=null && !descriptions.isEmpty();
	}
}
