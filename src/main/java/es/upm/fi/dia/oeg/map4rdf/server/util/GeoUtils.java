/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.server.util;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTReader;

import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MultiPolygonBean;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.PolygonBean;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinateBean;

/**
 * @author Alexander De Leon
 */
public class GeoUtils {
	private static final Logger LOG = Logger.getLogger(es.upm.fi.dia.oeg.map4rdf.server.util.GeoUtils.class);
	//If you modify this enum remember to modify validWKTTypes String and transformWKTtoOEG method.
	private static enum WKTTypes{Point, LineString, Polygon,MultiPoint,MultiPolygon,MultiLineString}
	private static String validWKTTypes="{Point, LineString, Polygon,MultiPoint,MultiPolygon,MultiLineString}";
	public static double getDistance(TwoDimentionalCoordinate point1, TwoDimentionalCoordinate point2) {
		return Math.sqrt(Math.pow(point1.getX() - point2.getX(), 2) + Math.pow(point1.getY() - point2.getY(), 2));
	}
	public static List<Geometry> getWKTGeometries(String uri ,String GMLText, String WKTText,String projection){
		String crs=projection;
		/*if(GMLText.contains("srsName")){
			String parseText=GMLText.substring(GMLText.indexOf("srsName"), GMLText.indexOf(" ", GMLText.indexOf("srsName")));
			parseText=parseText.replace(" ", "");
			parseText=parseText.replace("srsName=", "");
			parseText=parseText.replace("\"", "");
			crs=parseText;
		}else{
			String error="GML not contains \"srsName\" for use specific CRS. Use default CRS";
			printGMLError(uri, error, GMLText);
		}*/
		/*if(!crs.contains("EPSG")){
			String error="CRS: "+crs+". Is not valid. Valid CRS are EPSG:XXXX. Use default CRS";
			printGMLError(uri, error, GMLText);
			crs=TwoDimentionalCoordinateBean.getDefaultProjection();
		}*/
		
		String realWKTText="";
		int firtsIndex=-1;
		for(WKTTypes i: WKTTypes.values()){
			int index=WKTText.toLowerCase().indexOf(i.toString().toLowerCase());
			if(index>=0 && (index<firtsIndex || firtsIndex==-1)){
				firtsIndex=index;
			}
		}
		
		if(firtsIndex==-1){
			printWKTError(uri,"Not found valid WKTType. Valid types are:"+validWKTTypes, WKTText);
			return null;
		}
		realWKTText=WKTText.substring(firtsIndex, WKTText.length());
		int count=1;
		int lastIndex=-1;
		int searchIndex=realWKTText.indexOf("(");
		if(searchIndex==-1 || searchIndex==realWKTText.length()-1){
			printWKTError(uri, "Not found '(' character.", WKTText);
			return null;
		}
		for(int i=searchIndex+1;i<realWKTText.length();i++){
			if(realWKTText.charAt(i)=='('){
				count++;
			}
			if(realWKTText.charAt(i)==')'){
				count--;
			}
			if(count==0){
				lastIndex=i+1;
				break;
			}
		}
		if(lastIndex==-1){
			printWKTError(uri, "Not balanced '(' and ')' characters.", WKTText);
			return null;
		}
		realWKTText=realWKTText.substring(0,lastIndex);
		/*parseWKTGeotools(uri, GMLText, realWKTText, projection);
		//parseWKTGeotools(uri, GMLText, WKTText, projection);
		parseWKTGeotools("", "", "POINT(1 45)", "");
		parseWKTGeotools("", "", "MULTIPOINT(40 35, 20 10)", "");
		parseWKTGeotools("", "", "LINESTRING(1 1, 2 2, 3 3, 4 4, 5 5, 6 6)", "");
		parseWKTGeotools("", "", "MULTILINESTRING((1 1, 2 2),(3 3, 4 4))", "");
		parseWKTGeotools("", "", "POLYGON ( (0 0, 10 0, 10 10, 0 10, 0 0),( 20 20, 20 40, 40 40, 40 20, 20 20) )", "");
		parseWKTGeotools("", "", "MULTIPOLYGON(((1 1, 2 1,2 0, 1 1)),((3 3, 4 3,4 2, 3 3)))", "");
		parseWKTGeotools("", "", "POLYGON((1 1, 2 1,2 0, 1 1))", "");*/
		return parseWKTGeotools(uri, GMLText, realWKTText, crs);
	}
	private static List<Geometry> transforWKTtoOEG(String uri,String realWKTText,String crs){
		if(realWKTText.toLowerCase().contains(WKTTypes.Point.toString().toLowerCase())){
			String stringPoints=realWKTText.toLowerCase().replace(WKTTypes.Point.toString().toLowerCase(), "");
			List<TwoDimentionalCoordinate> points=extractTwoDimentionalCoordinate(uri, crs, stringPoints);
			if(!points.isEmpty()){
				List<Geometry> geometries= new ArrayList<Geometry>();
				for(TwoDimentionalCoordinate point:points){
					geometries.add(new PointBean(uri, point.getX(), point.getY(),point.getProjection()));
				}
				return geometries;
			}
			return null;
		}
		if(realWKTText.toLowerCase().contains(WKTTypes.LineString.toString().toLowerCase())){
			String stringLineString=realWKTText.toLowerCase().replace(WKTTypes.LineString.toString().toLowerCase(), "");
			stringLineString=stringLineString.replaceAll(" +", " ");;
			stringLineString=stringLineString.replace(')', 'z');
			stringLineString=stringLineString.replaceAll("z ,", "z,");
			List<Geometry> geometries=new ArrayList<Geometry>();
			for(String lineStringSplit:stringLineString.split("z,")){
				List<TwoDimentionalCoordinate> coordinates=extractTwoDimentionalCoordinate(uri, crs, lineStringSplit);
				if(!coordinates.isEmpty()){
					List<Point> points=new ArrayList<Point>();
					for(TwoDimentionalCoordinate point:coordinates){
						points.add(new PointBean(uri, point.getX(), point.getY(),point.getProjection()));
					}
					if(!points.isEmpty()){
						geometries.add(new PolyLineBean(uri, points));
					}
				}
			}
			return geometries;
			
		}
		if(realWKTText.toLowerCase().contains(WKTTypes.Polygon.toString().toLowerCase())){
			String stringLineString=realWKTText.toLowerCase().replace(WKTTypes.Polygon.toString().toLowerCase(), "");
			stringLineString=stringLineString.replaceAll(" +", " ");;
			stringLineString=stringLineString.replace(')', 'z');
			stringLineString=stringLineString.replaceAll("z ,", "z,");
			List<Geometry> geometries=new ArrayList<Geometry>();
			for(String lineStringSplit:stringLineString.split("z,")){
				List<TwoDimentionalCoordinate> coordinates=extractTwoDimentionalCoordinate(uri, crs, lineStringSplit);
				if(!coordinates.isEmpty()){
					List<Point> points=new ArrayList<Point>();
					for(TwoDimentionalCoordinate point:coordinates){
						points.add(new PointBean(uri, point.getX(), point.getY(),point.getProjection()));
					}
					if(!points.isEmpty()){
						geometries.add(new PolygonBean(uri, points));
					}
				}
			}
			return geometries;
		}
		return null;
	}
	private static List<TwoDimentionalCoordinate> extractTwoDimentionalCoordinate(String uri,String crs,String points){
		List<TwoDimentionalCoordinate> toReturn= new ArrayList<TwoDimentionalCoordinate>();
		points=points.replace('(', 'z');
		points=points.replace(')', 'z');
		points=points.replace("z","");
		points=points.trim();
		points=points.replaceAll(" +", " ");
		for(String WKTPoint:points.split(",")){
			WKTPoint=WKTPoint.trim();
			String [] coordenates=WKTPoint.split(" ");
			try{
				double x=Double.parseDouble(coordenates[0]);
				double y=Double.parseDouble(coordenates[1]);
				//double[] xy=convertPoint(x,y,crs,"EPSG:4326");
				TwoDimentionalCoordinateBean point= new TwoDimentionalCoordinateBean(x, y,crs);
				toReturn.add(point);
			}catch(NumberFormatException e){
				LOG.error(e);
				LOG.error("NumberFormatException in parse point:"+WKTPoint);
				LOG.error("In URI:"+uri);
			}
		}
		return toReturn;
	}
	private static void printWKTError(String uri, String error, String WKTText){
		LOG.warn("Error WKT is malformed");
		LOG.warn(error);
		LOG.warn("In resource:"+uri);
		LOG.warn("WKT Text:"+"\""+WKTText+"\"");
	}
	/*private static void printGMLError(String uri,String error, String GMLText){
		LOG.warn("Error GML is malformed");
		LOG.warn(error);
		LOG.warn("In resource:"+uri);
		LOG.warn("GML Text:"+"\""+GMLText+"\"");
	}*/
	/*private static double[] convertPoint(double x,double y, String epsgFrom, String epsgTo){
		double toReturnX=0.0;
		double toReturnY=0.0;
		try {			
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:25830");
			CoordinateReferenceSystem targetCRS = CRS.decode(epsgTo);
			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, false);
			GeometryBuilder builder = new GeometryBuilder( sourceCRS );
			Point point = (Point) builder.createPoint( x, y );
			Point targetPoint = (Point) JTS.transform((com.vividsolutions.jts.geom.Geometry)point, transform);
			toReturnX=targetPoint.getX();
			toReturnY=targetPoint.getY();
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new double[]{toReturnX, toReturnY};
	}*/
	
	
	private static List<Geometry> parseWKTGeotools(String uri ,String GMLText, String WKTText,String projection){
		try{
			/*GeoTools.addClassLoader(ClassLoader.getSystemClassLoader());
			CoordinateReferenceSystem crs = CRS.decode("EPSG:23030");
            GeometryFactory geometryFactory = new GeometryFactoryImpl(crs);
            PositionFactory positionFactory = new PositionFactoryImpl();
            PrimitiveFactory primitiveFactory = new PrimitiveFactoryImpl();
            AggregateFactory aggregateFactory = new AggregateFactoryImpl();
            WKTParser parser = new WKTParser( geometryFactory, primitiveFactory, positionFactory, aggregateFactory);
            org.opengis.geometry.Geometry geometry = parser.parse(WKTText);*/
			//CoordinateReferenceSystem crs = CRS.decode("EPSG:23030");
			com.vividsolutions.jts.geom.GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		    WKTReader reader = new WKTReader(geometryFactory);
		    com.vividsolutions.jts.geom.Geometry geometry = reader.read(WKTText);
		    if(geometry==null){
		    	printWKTError(uri, "Error in GeoTools INVALID WKT of resource: "+uri, WKTText);
		    	return null;
		    }
		    return transformGeoToolsGeometryToOEG(uri,WKTText,projection,geometry);
		    
		    
		    
		}catch(Exception e){
			printWKTError(uri, "Error in GeoTools parse WKT of resource: "+uri, WKTText);
			LOG.error("",e);
		}
		return null;
	}
	private static List<Geometry> transformGeoToolsGeometryToOEG(String uri, String WKTText, String projection,
			com.vividsolutions.jts.geom.Geometry geometry) {
		// TODO Auto-generated method stub
		List<Geometry> geometrias = new ArrayList<Geometry>();
		switch (geometry.getGeometryType().toLowerCase()) {
		case "multipolygon":
			List<Polygon> polygons= new ArrayList<Polygon>();
			for(int i=0; i<geometry.getNumGeometries();i++){
		    	Coordinate [] coordinates = geometry.getGeometryN(i).getCoordinates();
		    	List<Point> points= new ArrayList<Point>();
		    	for(int j=0;j<coordinates.length;j++){
		    		points.add(new PointBean(uri,coordinates[j].x, coordinates[j].y, projection));
		    	}
		    	polygons.add(new PolygonBean(uri, points));
		    }
		    geometrias.add(new MultiPolygonBean(uri, polygons));
			break;
		case "polygon":
			for(int i=0; i<geometry.getNumGeometries();i++){
		    	Coordinate [] coordinates = geometry.getGeometryN(i).getCoordinates();
		    	List<Point> points= new ArrayList<Point>();
		    	for(int j=0;j<coordinates.length;j++){
		    		points.add(new PointBean(uri,coordinates[j].x, coordinates[j].y, projection));
		    	}
		    	geometrias.add(new PolygonBean(uri, points));
		    }
			break;
		case "multilinestring":
		case "linestring":
			for(int i=0; i<geometry.getNumGeometries();i++){
		    	Coordinate [] coordinates = geometry.getGeometryN(i).getCoordinates();
		    	List<Point> points= new ArrayList<Point>();
		    	for(int j=0;j<coordinates.length;j++){
		    		points.add(new PointBean(uri,coordinates[j].x, coordinates[j].y, projection));
		    	}
		    	geometrias.add(new PolyLineBean(uri, points));
		    }
			break;
		case "point":
		case "multipoint":
			for(int i=0; i<geometry.getNumGeometries();i++){
		    	Coordinate [] coordinates = geometry.getGeometryN(i).getCoordinates();
		    	for(int j=0;j<coordinates.length;j++){
		    		geometrias.add(new PointBean(uri,coordinates[j].x, coordinates[j].y, projection));
		    	}
		    }
			break;
		default:
			printWKTError(uri, "GeoTools return a invalid type of geometry.", WKTText);
			return null;
		}
		return geometrias;
	}
}