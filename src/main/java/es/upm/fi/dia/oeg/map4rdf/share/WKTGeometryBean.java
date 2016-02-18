package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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
		List<Point> toReturn = new ArrayList<Point>();
		int init = wkt.indexOf("(");
		int end = wkt.lastIndexOf(")");
		String parsed = removeWhiteSpaces(new String(wkt));
		if(init > 0 && end > 0){
			parsed = parsed.substring(init,end);
		}
		parsed.replaceAll("\\(", "");
		parsed.replaceAll("\\)", "");
		String [] points = parsed.split(",");
		for(String point:points){
			String []coordinates = point.trim().split(" ");
			if(coordinates.length == 2){
				int x=Integer.MIN_VALUE;
				int y=Integer.MIN_VALUE;
				try{
					x = Integer.parseInt(coordinates[0]);
					y = Integer.parseInt(coordinates[1]);
				}catch(Exception e){
				}
				if(x != Integer.MIN_VALUE && y != Integer.MIN_VALUE){
					toReturn.add(new PointBean(this.uri,x,y,this.projection));
				}
			}
		}
		return toReturn;
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
	
	//Helper METHODS
	
	private static String removeWhiteSpaces(String in) {
		String toReturn = "";
		toReturn = in.replaceAll("( )+", " ").trim();
		toReturn = toReturn.replaceAll(" \\(", "(");
		toReturn = toReturn.replaceAll("\\( ", "(");
		toReturn = toReturn.replaceAll(" \\)", ")");
		toReturn = toReturn.replaceAll("\\) ", ")");
		toReturn = toReturn.replaceAll(" ,", ",");
		toReturn = toReturn.replaceAll(", ", ",");
		return toReturn;
	}

}
