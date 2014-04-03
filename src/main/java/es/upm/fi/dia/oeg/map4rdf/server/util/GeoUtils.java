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

import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolygonBean;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinateBean;

/**
 * @author Alexander De Leon
 */
public class GeoUtils {
	private static final Logger LOG = Logger.getLogger(es.upm.fi.dia.oeg.map4rdf.server.util.GeoUtils.class);
	//If you modify this enum remember to modify validWKTTypes String and transformWKTtoOEG method.
	private static enum WKTTypes{Point, LineString, Polygon}
	private static String validWKTTypes="{Point, LineString, Polygon}";
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
		return transforWKTtoOEG(uri, realWKTText, crs);
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
}