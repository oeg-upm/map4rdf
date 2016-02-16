package es.upm.fi.dia.oeg.map4rdf.server.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;
import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.share.viajero.ViajeroResourceContainer;

public class ViajeroImpl extends CommonDaoImpl implements Map4rdfDao {

	private static final Logger LOG = Logger.getLogger(ViajeroImpl.class);

	public ViajeroImpl(String endpointUri,String defaultProjection) {
		super(endpointUri,defaultProjection);
	}

	@Override
	public GeoResource getGeoResource(String uri) throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri, createGetResourceQuery(uri));
		try {
			ResultSet queryResult = execution.execSelect();
			GeoResource resource = null;
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();

					if (resource == null) {
						resource = new AemetResource(uri, new PointBean(
								uri, lng, lat,defaultProjection));
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(),
								labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: "
							+ e.getMessage());
				}
			}
			return resource;
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints) throws DaoException {
		return getGeoResources(boundingBox, constraints, null);
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints, int max) throws DaoException {
		return getGeoResources(boundingBox, constraints, new Integer(max));
	}

	@Override
	public List<GeoResourceOverlay> getGeoResourceOverlays(
			StatisticDefinition statisticDefinition, BoundingBox boundingBox,
			Set<FacetConstraint> constraints) throws DaoException {
		// TODO Not statistics in Viajero
		return Collections.emptyList();
	}

	@Override
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox)
			throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("select distinct ?class ?label where { ");
		queryBuffer.append("?x <" + Geo.lat + "> _:lat. ");
		queryBuffer.append("?x <" + Geo.lng + "> _:lng. ");
		queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		queryBuffer.append("optional {?class <" + RDFS.label + "> ?label . }}");

		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri, queryBuffer.toString());

		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				String uri = solution.getResource("class").getURI();
				Facet value = null;
				if (result.containsKey(uri)) {
					value = result.get(uri);
				} else {
					value = new Facet(uri);
					result.put(uri, value);
				}
				if (solution.contains("label")) {
					Literal label = solution.getLiteral("label");
					value.addLabel(label.getLanguage(), label.getString());
				}
			}
			return new ArrayList<Facet>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<Year> getYears(String datasetUri) throws DaoException {
		// TODO Not statistics in Viajero
		return Collections.emptyList();
	}

	@Override
	public List<Resource> getStatisticDatasets() throws DaoException {
		// TODO Not statistics in Viajero
		return Collections.emptyList();
	}

	@Override
	public List<GeoResource> getNextPoints(BoundingBox boundingBox, int max)
			throws DaoException {
		HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri, createGetNextPoints(boundingBox, max));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();
					GeoResource resource = result.get(uri);

					if (resource == null) {
						resource = new ViajeroResourceContainer(uri,
								new PointBean(uri, lng, lat,defaultProjection));
						result.put(uri, resource);
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(),
								labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: "
							+ e.getMessage());
				}
			}
			return new ArrayList<GeoResource>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}

	}

	private List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints, Integer max) throws DaoException {
		HashMap<String, ViajeroResourceContainer> result = new HashMap<String, ViajeroResourceContainer>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				endpointUri,
				createGetResourcesQueryAdaptedViajero(boundingBox,
						constraints, max));
		if(constraints!=null && constraints.isEmpty()){
			return new ArrayList<GeoResource>();
		}
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();
					ViajeroResourceContainer resource = null;
					resource = result.get(uri);
					if (resource == null) {
						if(solution.contains("facetValueID") && solution.getResource("facetValueID").getURI().contains("Trip")){
							resource = new ViajeroResourceContainer(uri,
									getItinerary(uri));
							result.put(uri, resource);
						}else{
							resource = new ViajeroResourceContainer(uri,
									new PointBean(uri, lng, lat,defaultProjection));
							result.put(uri, resource);
						}
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(),
								labelLiteral.getString());
					}
					if(solution.contains("facetID") && solution.contains("facetValueID")){
						String facetID=solution.getResource("facetID").getURI();
						String facetValueID=solution.getResource("facetValueID").getURI();
						resource.setFacetConstraint(new FacetConstraint(facetID, facetValueID));
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: "
							+ e.getMessage());
				}
			}
			return new ArrayList<GeoResource>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	private String createGetResourcesQueryAdaptedViajero(
			BoundingBox boundingBox, Set<FacetConstraint> constraints,
			Integer limit) {
		StringBuilder query = new StringBuilder(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?r ?lat ?lng ?label ?facetID ?facetValueID ");
		query.append("WHERE { ");
		if (constraints != null && !constraints.isEmpty()) {
			for (FacetConstraint constraint : constraints) {
				if (constraint.getFacetValueId().contains("Trip")) {
					query.append("{?t ?facetID ?facetValueID. ");
					query.append("FILTER(");
					query.append("?facetID IN(");
					query.append("<"+constraint.getFacetId()+">)");
					query.append(" && ?facetValueID IN(");
					query.append("<"+constraint.getFacetValueId()+">)). ");
					query.append("?t <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?r.");
					query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
					query.append("?r <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?part.");
					query.append("?part <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> ?point. ");
					query.append("?point <"+Geo.lat+"> ?lat.");
					query.append("?point <"+Geo.lng+"> ?lng.");
					query.append("} UNION");
				} else if (constraint.getFacetValueId().contains("Point")) {
					//Tratamiento especial si es un punto
					query.append("{?r ?facetID ?facetValueID. ");
					query.append("FILTER(");
					query.append("?facetID IN(");
					query.append("<"+constraint.getFacetId()+">)");
					query.append(" && ?facetValueID IN(");
					query.append("<"+constraint.getFacetValueId()+">)). ");
					query.append("?r <" + Geo.lat + "> ?lat. ");
					query.append("?r <" + Geo.lng + "> ?lng . ");
					query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
					query.append("} UNION");
				} else {
					// cualquier cosa con localizacion (guias, aristas)
					query.append("{?g ?facetID ?facetValueID. ");
					query.append("FILTER(");
					query.append("?facetID IN(");
					query.append("<"+constraint.getFacetId()+">)");
					query.append(" && ?facetValueID IN(");
					query.append("<"+constraint.getFacetValueId()+">)). ");
					query.append("?g <"+Geo.location+"> ?r.");
					query.append("?r <" + Geo.lat + "> ?lat. ");
					query.append("?r <" + Geo.lng + "> ?lng . ");
					query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
					query.append(" } UNION");
				}
			}
			query.delete(query.length() - 5, query.length());
		}
		// filters
		if (boundingBox != null) {
			query = addBoundingBoxFilter(query, boundingBox);
		}

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private PolyLine getItinerary(String uriItinerario)
			throws DaoException {
		// query 1. The path & order of the itinerary.
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetItineraryQuery(1000, uriItinerario));// como mucho un
																// itinerario de
																// 1000 puntos
		ResultSet queryResult2 = exec2.execSelect();
		String point = "";
		double lat, longitude;
		ArrayList<Point> puntosOrdenados = new ArrayList<Point>();
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			point = solution2.getResource("point").getURI();
			lat = solution2.getLiteral("lat").getDouble();
			longitude = solution2.getLiteral("long").getDouble();
			PointBean p1 = new PointBean(point, longitude, lat,defaultProjection);
			puntosOrdenados.add(p1);
		}
		// Los puntos vienen ordenados ya por ?order (en la consulta)
		PolyLineBean p = new PolyLineBean(uriItinerario, puntosOrdenados,defaultProjection);
		return p;
	}
	private String createGetItineraryQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?order ?point ?lat ?long ");
		query.append("WHERE{ ");
		query.append("<"
				+ uri
				+ "> <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?path.");
		query.append("?path <http://webenemasuno.linkeddata.es/ontology/OPMO/hasOrder> ?order.");
		query.append("?path <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> ?point.");
		query.append("?point <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat.");
		query.append("?point <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long.");

		query.append("}");
		query.append("ORDER BY ?order");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private String createGetResourceQuery(String uri) {
		StringBuilder query = new StringBuilder(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?position ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("<" + "uri" + "> <" + Geo.lat + "> ?lat. ");
		query.append("<" + "uri" + "> <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { <" + uri + "> <" + RDFS.label + "> ?label } .");
		query.append("}");
		return query.toString();
	}
	private String createGetNextPoints(BoundingBox boundingBox, int limit){
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?r ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("?r <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2003/01/geo/wgs84_pos#Point>.");
		query.append("?r <" + Geo.lat + "> ?lat. ");
		query.append("?r <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		//filters
		if (boundingBox!=null) {
			query = addBoundingBoxFilter(query, boundingBox);
		}
		
		query.append("}");
		if (limit >0) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

}
