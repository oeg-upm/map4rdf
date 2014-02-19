package es.upm.fi.dia.oeg.map4rdf.share.webnmasuno;

import java.io.Serializable;

/**
 *
 * @author Daniel Garijo
 */
public class WebNMasUnoImage extends WebNMasUnoResource implements Serializable {

	private static final long serialVersionUID = 956723024219051717L;
	private String title;
    private String URI;
    private String pname;
    private String URL;


    //serialization
    public WebNMasUnoImage (){
    }

    public WebNMasUnoImage(String URI, String pname,String URL){       
        this.URI = URI;
        this.pname = pname;
        this.URL=URL;
        this.title = "";
    }
    
    public WebNMasUnoImage(String URI, String pname,String URL, String title) {       
        this.URI = URI;
        this.pname = pname;
        this.URL=URL;
        this.title = title;
    }
    

    public String getURI() {
        return URI;
    }

    public String getPname() {
        return pname;
    }

	public String getURL() {
		return URL;
	}

	public String getTitle() {
	    return title;
	}
	@Override
	public String toString(){
		StringBuffer buffer=new StringBuffer();
		buffer.append("uriImage="+URI);
		buffer.append(" # ");
		buffer.append("urlImage="+URL);
		buffer.append(" # ");
		buffer.append("pnameImage="+pname);
		buffer.append(" # ");
		buffer.append("titleImage="+title);
		return buffer.toString();
	}
    

}
