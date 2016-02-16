package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.Collection;


public class WKTGeometryBean implements WKTGeometry,Serializable {

	private static final long serialVersionUID = -3988048652540836855L;
	private String uri;
	private String wkt;
	private String projection;
	
	@SuppressWarnings("unused")
	private WKTGeometryBean(){
		//ONLY for serialization
	}
	
	public WKTGeometryBean(String uri,String wkt,String projection) {
		this.uri=uri;
		this.wkt=wkt;
		this.projection=projection;
	}
	
	@Override
	public Collection<Point> getPoints(){
		System.err.println("ERROR NOT IMPLEMENTED");
		return null;
	}

	@Override
	public String getProjection() {
		return this.projection;
	}

	@Override
	public String getUri() {
		return this.uri;
	}

	@Override
	public Type getType() {
		return Type.WKTGEOMETRY;
	}

	@Override
	public String getWKT() {
		return this.wkt;
	}

}
