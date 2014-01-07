package es.upm.fi.dia.oeg.map4rdf.share;

/**
 * @author Filip
 */
public class RDFResource implements BasicRDFInformation {

	private static final long serialVersionUID = -2504984834219187065L;
	private String localName;
	private String namespace;
	private URLSafety URI;
	
	public RDFResource() {
		//for serliazation
	}
	
	public RDFResource(String LocalName, String namespace, String URI){
		this.setLocalName(LocalName);
		this.setNamespace(namespace);
		this.setURI(new URLSafety(URI));
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public URLSafety getURI() {
		return URI;
	}

	public void setURI(URLSafety uRI) {
		URI = uRI;
	}

	@Override
	public String getText() {
		return URI.getUrl();
	}

	@Override
	public void setText(String text) {
		this.URI = new URLSafety(text);
	}

	@Override
	public Boolean isResource() {
		return true;
	}

	@Override
	public Boolean isLiteral() {
		return false;
	}
}