package es.upm.fi.dia.oeg.map4rdf.share.conf.util;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.dev.util.collect.HashMap;

public class ConfigurationDescription implements Serializable{
	
	private static final long serialVersionUID = 1174274854533905981L;
	
	private String id;
	private Map<String,String> labels = new HashMap<String,String>();;
	private Map<String,String> descriptions = new HashMap<String,String>();
	private String image = "";

	public ConfigurationDescription(String id){
		this.id = id;
		this.labels = new HashMap<String,String>();
		this.descriptions = new HashMap<String,String>();
	}

	public Map<String, String> getDescription() {
		return descriptions;
	}

	public void addDescription(String locale,String description) {
		this.descriptions.put(locale, description);
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getLabels() {
		return labels;
	}
	public void addLabel(String locale, String label){
		labels.put(locale, label);
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
	public boolean hasLabels(){
		return labels!=null && !labels.isEmpty();
	}
	public boolean hasDescription(){
		return descriptions!=null && !descriptions.isEmpty();
	}
}
