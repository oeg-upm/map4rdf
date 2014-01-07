package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

/**
 * @author Filip
 */
public interface BasicRDFInformation extends Serializable {
	public String getText();
	public void setText(String text);
	public Boolean isResource();
	public Boolean isLiteral();
}
