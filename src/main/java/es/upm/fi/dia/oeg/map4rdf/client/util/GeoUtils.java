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
package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.util.Collection;
import java.util.HashSet;

import org.gwtopenmaps.openlayers.client.util.JSObject;

import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBoxBean;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MultiPolygon;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinateBean;

/**
 * @author Alexander De Leon
 */
public class GeoUtils {
	//Can only be accessed in client mode
	public static BoundingBox computeBoundingBoxFromGeometries(Collection<Geometry> geometries,String projection) {
		HashSet<Point> points = new HashSet<Point>();
		for (Geometry geometry : geometries) {
			points.addAll(geometry.getPoints());
		}
		return computeBoundingBox(points,projection);
	}

	//Can only be accessed in client mode
	public static BoundingBox computeBoundingBox(Collection<Point> points,String projection) {
		double maxX = Double.NEGATIVE_INFINITY;
		double minX = Double.POSITIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;

		for (Point p : points) {
			p.transform(p.getProjection(), projection);
			double x = p.getX();
			double y = p.getY();
			if (x > maxX) {
				maxX = x;
			}
			if (x < minX) {
				minX = x;
			}
			if (y > maxY) {
				maxY = y;
			}
			if (y < minY) {
				minY = y;
			}
		}
		return new BoundingBoxBean(new TwoDimentionalCoordinateBean(minX, minY,projection), new TwoDimentionalCoordinateBean(maxX,
				maxY,projection),projection);
	}
	
	public static Point getCentroid(Geometry geometry){
		switch (geometry.getType()) {
		case CIRCLE:
			//TODO Implement circle getCentroid
			break;
		case MULTIPOLYGON:
			MultiPolygon multiPolygon = (MultiPolygon) geometry;
			org.gwtopenmaps.openlayers.client.geometry.Polygon polygons[]=new org.gwtopenmaps.openlayers.client.geometry.Polygon[multiPolygon.getPolygons().size()];
			int i=0;
			for(Polygon polygon:multiPolygon.getPolygons()){
				org.gwtopenmaps.openlayers.client.geometry.LinearRing rings[] = {new org.gwtopenmaps.openlayers.client.geometry.LinearRing(getPoints(polygon.getPoints()))};
				polygons[i++]= new org.gwtopenmaps.openlayers.client.geometry.Polygon(rings);
			}
			org.gwtopenmaps.openlayers.client.geometry.MultiPolygon openMultiPolygon = new org.gwtopenmaps.openlayers.client.geometry.MultiPolygon(polygons);
			return new PointBean(geometry.getUri(),getGeometryCentroidX(openMultiPolygon.getJSObject()), getGeometryCentroidY(openMultiPolygon.getJSObject()), geometry.getProjection());
		case POINT:
			return (Point) geometry;
		case POLYGON:
			org.gwtopenmaps.openlayers.client.geometry.LinearRing rings[] = {new org.gwtopenmaps.openlayers.client.geometry.LinearRing(getPoints(geometry.getPoints()))};
			org.gwtopenmaps.openlayers.client.geometry.Polygon polygon = new org.gwtopenmaps.openlayers.client.geometry.Polygon(rings);
			return new PointBean(geometry.getUri(),getGeometryCentroidX(polygon.getJSObject()),getGeometryCentroidY(polygon.getJSObject()), geometry.getProjection());
		case POLYLINE:
			org.gwtopenmaps.openlayers.client.geometry.LineString lineString = new org.gwtopenmaps.openlayers.client.geometry.LineString(getPoints(geometry.getPoints()));
			return new PointBean(geometry.getUri(),getGeometryCentroidX(lineString.getJSObject()),getGeometryCentroidY(lineString.getJSObject()), geometry.getProjection());
		default:
			//Make compiler happy.
			break;
		
		}
		
		return null;
	}
	/**
	 * JSObject geometry need to be a org.gwtopenmaps.openlayers.client.geometry.Geometry convert to JSObject
	 * Use geometry.getJSObject();
	 * */
	private native static double getGeometryCentroidX(JSObject geometry) /*-{
		return geometry.getCentroid().x;
	}-*/;
	/**
	 * JSObject geometry need to be a org.gwtopenmaps.openlayers.client.geometry.Geometry convert to JSObject
	 * Use geometry.getJSObject();
	 * */
	private native static double getGeometryCentroidY(JSObject geometry) /*-{
		return geometry.getCentroid().y;
	}-*/;
	private static org.gwtopenmaps.openlayers.client.geometry.Point[] getPoints(
			Collection<Point> points) {
		org.gwtopenmaps.openlayers.client.geometry.Point[] pointsArray = new org.gwtopenmaps.openlayers.client.geometry.Point[points
				.size()];
		int index = 0;
		for (Point p : points) {
			pointsArray[index++] = new org.gwtopenmaps.openlayers.client.geometry.Point(
					p.getX(), p.getY());
		}
		return pointsArray;
	}

}
