package es.upm.fi.dia.oeg.map4rdf.share.webnmasuno;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebNMasUnoGuide extends WebNMasUnoResource implements Serializable {

	private static final long serialVersionUID = 630125676546727117L;
	private String title;
	private String url;
	private String uri;
	private String date;

	private List<WebNMasUnoImage> images=new ArrayList<WebNMasUnoImage>();
	
	public WebNMasUnoGuide() {

	}

	public WebNMasUnoGuide(String title, String url, String uri, String date) {
		if (title.equals("")) {
			String[] urlSplit=url.split("/");
			if(urlSplit.length>=2){
				this.title = urlSplit[urlSplit.length-2];
			}else{
				this.title = "No disponible";
			}
		} else {
			this.title = title;
		}
		if (date.equals("")) {
			this.date = "No disponible";
		} else {
			this.date = date;
		}
		this.url = url;
		this.uri = uri;
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

	public String getDate() {
		return date;
	}

	public List<WebNMasUnoImage> getImages() {
		return images;
	}

	public void addImage(WebNMasUnoImage image) {
		this.images.add(image);
	}
	
	@Override
	public String toString(){
		StringBuffer buffer=new StringBuffer();
		buffer.append("Uri="+uri);
		buffer.append(" | ");
		buffer.append("title="+title);
		buffer.append(" | ");
		buffer.append("url="+url);
		buffer.append(" | ");
		buffer.append("date="+date);
		for(WebNMasUnoImage image:images){
			buffer.append(" | ");
			buffer.append(image);
		}
		
		return buffer.toString();	
	}
}
