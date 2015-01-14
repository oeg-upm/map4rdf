package es.upm.fi.dia.oeg.map4rdf.server.servlet;

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

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.util.ConfigurationUtil;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MultiPolygon;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;

@Singleton
public class GeoJSONService extends HttpServlet {

	private static final long serialVersionUID = 4940408910832985953L;
	private MultipleConfigurations configurations;
	private static enum GeoJSON_Types{Point,LineString,Polygon,MultiPolygon};
	private static final String[] reservedParameters = { ConfigurationUtil.CONFIGURATION_ID };
	private Logger LOG = Logger.getLogger(GeoJSONService.class);

	@Inject
	public GeoJSONService(MultipleConfigurations configurations) {
		this.configurations = configurations;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Set<FacetConstraint> constraints = getFacetConstraints(req);
		String configID = getConfigurationID(req);
		try {
			List<GeoResource> resources = configurations
					.getConfiguration(configID).getMap4rdfDao()
					.getGeoResources(null, constraints);
			resp.setContentType("application/json");
			String headerKey = "Content-Disposition";
	        String headerValue = String.format("attachment; filename=\"%s\"","resources.json");
	        resp.setHeader(headerKey, headerValue);
			writeGeoJSON(resources, resp.getOutputStream());
		} catch (DaoException daoException) {
			throw new ServletException(daoException);
		}
	}

	private void writeGeoJSON(List<GeoResource> resources,
			ServletOutputStream outputStream) {
		JSONObject featureCollection = new JSONObject();
		try {
			featureCollection.put("type", "FeatureCollection");
			JSONArray featureList = new JSONArray();
			// iterate through your list
			for (GeoResource resource : resources) {
				featureList.put(getJSONofResource(resource));
				featureCollection.put("features", featureList);
			}
		} catch (Exception e) {
			LOG.error("Can't save json object. ", e);
		}
		try {
			outputStream.print(featureCollection.toString());
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			LOG.error("Can't print in output stream the result. ", e);
		}

	}

	private JSONObject getJSONofResource(GeoResource resource) throws Exception{
		JSONObject toReturn = new JSONObject();
		toReturn.put("type", "Feature");
		JSONObject properties = new JSONObject();
		properties.put("uri", resource.getUri());
		JSONObject labels = new JSONObject();
		for(String lang:resource.getLangs()){
			labels.put(lang, resource.getLabel(lang));
		}
		properties.put("labels", labels);
		toReturn.put("properties", properties);
		for (Geometry geometry : resource.getGeometries()) {
			switch (geometry.getType()) {
			case POINT:
				Point point = (Point) geometry;
				toReturn.put("geometry", getJSONofPoint(point));
				break;
			case POLYGON:
				Polygon polygon = (Polygon) geometry;
				toReturn.put("geometry", getJSONofPolygon(polygon));
				break;
			case CIRCLE:
				//Circle circle = (Circle) geometry;
				//TODO Implement GeoJSON circle
				break;
			case MULTIPOLYGON:
				MultiPolygon multiPolygon = (MultiPolygon) geometry;
				toReturn.put("geometry", getJSONofMultiPolygon(multiPolygon));
				break;
			case POLYLINE:
				PolyLine polyLine = (PolyLine) geometry;
				toReturn.put("geometry", getJSONofPolyline(polyLine));
				break;
			}
		}
		return toReturn;
	}

	

	private JSONObject getJSONofPoint(Point geometryPoint) {
		JSONObject point = new JSONObject();
		point.put("type", GeoJSON_Types.Point.toString());
		JSONArray coord = new JSONArray();
		coord.put(geometryPoint.getX());
		coord.put(geometryPoint.getY());
		point.put("coordinates", coord);
		return point;
	}
	
	private JSONObject getJSONofPolygon(Polygon polygon) {
		JSONObject polygonJSON = new JSONObject();
		polygonJSON.put("type", GeoJSON_Types.Polygon.toString());
		JSONArray allPolygons = new JSONArray();
		JSONArray onePolygon = new JSONArray();
		for(Point point: polygon.getPoints()){
			JSONArray coord = new JSONArray();
			coord.put(point.getX());
			coord.put(point.getY());
			onePolygon.put(coord);
		}
		allPolygons.put(onePolygon);
		polygonJSON.put("coordinates", allPolygons);
		return polygonJSON;
	}

	private JSONObject getJSONofMultiPolygon(MultiPolygon multiPolygon) {
		JSONObject multiPolygonJSON = new JSONObject();
		multiPolygonJSON.put("type", GeoJSON_Types.MultiPolygon.toString());
		JSONArray polygonsArray = new JSONArray();
		for(Polygon polygon: multiPolygon.getPolygons()){
			JSONArray allPolygons = new JSONArray();
			JSONArray onePolygon = new JSONArray();
			for(Point point: polygon.getPoints()){
				JSONArray coord = new JSONArray();
				coord.put(point.getX());
				coord.put(point.getY());
				onePolygon.put(coord);
			}
			allPolygons.put(onePolygon);
			polygonsArray.put(allPolygons);
		}
		multiPolygonJSON.put("coordinates", polygonsArray);
		return multiPolygonJSON;
	}

	private JSONObject getJSONofPolyline(PolyLine polyLine) {
		JSONObject polylineJSON = new JSONObject();
		polylineJSON.put("type", GeoJSON_Types.LineString.toString());
		JSONArray lineString = new JSONArray();
		for(Point point: polyLine.getPoints()){
			JSONArray coord = new JSONArray();
			coord.put(point.getX());
			coord.put(point.getY());
			lineString.put(coord);
		}
		polylineJSON.put("coordinates", lineString);
		return polylineJSON;
	}

	private Set<FacetConstraint> getFacetConstraints(HttpServletRequest req) {
		Set<FacetConstraint> constraints = new HashSet<FacetConstraint>();
		Enumeration<String> paramNames = (Enumeration<String>) req
				.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String facetId = paramNames.nextElement();
			boolean isReservedParam = false;
			for (String toTest : reservedParameters) {
				if (toTest.toLowerCase().trim()
						.equals(facetId.toLowerCase().trim())) {
					isReservedParam = true;
					break;
				}
			}
			if (!isReservedParam) {
				String[] valueIds = req.getParameterValues(facetId);
				for (String valueId : valueIds) {
					constraints.add(new FacetConstraint(facetId, valueId));
				}
			}
		}
		return constraints;
	}

	private String getConfigurationID(HttpServletRequest req)
			throws ServletException {
		String value = req.getParameter(ConfigurationUtil.CONFIGURATION_ID);
		if (value == null || value.isEmpty()) {
			throw new ServletException("Bad parameter value of parameter key: "
					+ ConfigurationUtil.CONFIGURATION_ID);
		}
		return value;
	}
}
