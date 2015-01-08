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
package es.upm.fi.dia.oeg.map4rdf.server.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import es.upm.fi.dia.oeg.map4rdf.client.util.ConfigurationUtil;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.share.Circle;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MultiPolygon;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;

/**
 * @author Alexander De Leon
 */
@Singleton
public class KmlService extends HttpServlet {
	private static final long serialVersionUID = 4451922424233735357L;
	private MultipleConfigurations configurations;
	private static final String [] reservedParameters={ConfigurationUtil.CONFIGURATION_ID};
	//TODO Repair KML service with multiple configurations
	@Inject
	public KmlService(MultipleConfigurations configurations) {
		this.configurations = configurations;;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Set<FacetConstraint> constraints = getFacetConstraints(req);
		String configID = getConfigurationID(req);
		try {
			List<GeoResource> resources = configurations.getConfiguration(configID)
					.getMap4rdfDao().getGeoResources(null, constraints,100);
			resp.setContentType("application/vnd.google-earth.kml+xml");
			writeKml(resources, resp.getOutputStream());
		} catch (DaoException e) {
			throw new ServletException(e);
		}
	}

	private void writeKml(List<GeoResource> resources, ServletOutputStream outputStream) {
		Kml kml = new Kml();
		Document doc = kml.createAndSetDocument();
		for (GeoResource resource : resources) {
			Placemark placemark = doc.createAndAddPlacemark().withName(resource.getUri());
			if (resource.isMultiGeometry()) {
				MultiGeometry multiGeometry = new MultiGeometry();
				for (Geometry geometry : resource.getGeometries()) {
					multiGeometry.addToGeometry(getKmlGeometry(geometry));
				}
				placemark.setGeometry(multiGeometry);
			} else {
				placemark.setGeometry(getKmlGeometry(resource.getFirstGeometry()));
			}
		}
		// write XML
		try {
			kml.marshal(outputStream);
		} catch (FileNotFoundException e) {
			// ignore this is applicable for outputstream
		}
	}

	private de.micromata.opengis.kml.v_2_2_0.Geometry getKmlGeometry(Geometry geometry) {
		switch (geometry.getType()) {
		case POINT:
			Point point = (Point) geometry;
			de.micromata.opengis.kml.v_2_2_0.Point kmlPoint = new de.micromata.opengis.kml.v_2_2_0.Point();
			kmlPoint.addToCoordinates(point.getX(), point.getY());
			return kmlPoint;

		case POLYLINE:
			PolyLine polyline = (PolyLine) geometry;
			LineString kmlPolyline = new LineString();
			for (Point linePoint : polyline.getPoints()) {
				kmlPolyline.addToCoordinates(linePoint.getX(), linePoint.getY());
			}
			return kmlPolyline;

		case POLYGON:
			Polygon polygon = (Polygon) geometry;
			de.micromata.opengis.kml.v_2_2_0.LinearRing kmlPolygon = new de.micromata.opengis.kml.v_2_2_0.LinearRing();
			for (Point polygonPoint : polygon.getPoints()) {
				kmlPolygon.addToCoordinates(polygonPoint.getX(), polygonPoint.getY());
			}
			return kmlPolygon;
		case CIRCLE:
			Circle circle = (Circle) geometry;
			de.micromata.opengis.kml.v_2_2_0.LinearRing kmlCircle= new de.micromata.opengis.kml.v_2_2_0.LinearRing();
			double distanceStep=0.001;
			double deegreeStep=360/((2*Math.PI*circle.getRadius())/distanceStep);
			for(double degree=0;degree<360;degree+=deegreeStep){
				double plusX = Math.sin(Math.toRadians(degree))*circle.getRadius();
				double plusY = Math.cos(Math.toRadians(degree))*circle.getRadius();
				kmlCircle.addToCoordinates(circle.getCenter().getX()+plusX, circle.getCenter().getY()+plusY);
			}
			return kmlCircle;
		case MULTIPOLYGON:
			MultiPolygon multiPolygon = (MultiPolygon) geometry;
			de.micromata.opengis.kml.v_2_2_0.MultiGeometry kmlMultiPolygon = new MultiGeometry();
			for(Polygon polygonM:multiPolygon.getPolygons()){
				de.micromata.opengis.kml.v_2_2_0.LinearRing kmlPolygonM = new de.micromata.opengis.kml.v_2_2_0.LinearRing();
				for (Point polygonPoint : polygonM.getPoints()) {
					kmlPolygonM.addToCoordinates(polygonPoint.getX(), polygonPoint.getY());
				}
				kmlMultiPolygon.addToGeometry(kmlPolygonM);
			}
			return kmlMultiPolygon;
		}
		return null; // make compiler happy
	}

	private Set<FacetConstraint> getFacetConstraints(HttpServletRequest req) {
		Set<FacetConstraint> constraints = new HashSet<FacetConstraint>();
		Enumeration<String> paramNames = (Enumeration<String>)req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String facetId = paramNames.nextElement();
			boolean isReservedParam = false;
			for(String toTest : reservedParameters){
				if(toTest.toLowerCase().trim().equals(facetId.toLowerCase().trim())){
					isReservedParam=true;
					break;
				}
			}
			if(!isReservedParam){
				String[] valueIds = req.getParameterValues(facetId);
				for (String valueId : valueIds) {
					constraints.add(new FacetConstraint(facetId, valueId));
				}
			}
		}
		if(constraints.isEmpty()){
			return null;
		}
		return constraints;
	}

	private String getConfigurationID(HttpServletRequest req) throws ServletException {
		String value = req.getParameter(ConfigurationUtil.CONFIGURATION_ID);
		if(value == null || value.isEmpty()){
			throw new ServletException("Bad parameter value of parameter key: "+ConfigurationUtil.CONFIGURATION_ID);
		}
		return value;
	}
}
