package es.upm.fi.dia.oeg.map4rdf.server.dao.impl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetResource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.vocabulary.Geo;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Alexander De Leon
 */

public class AemetDaoImpl extends CommonDaoImpl implements Map4rdfDao {

	private static final Logger LOG = Logger.getLogger(AemetDaoImpl.class);

	@Inject
	public AemetDaoImpl(@Named(ParameterNames.ENDPOINT_URL) String endpointUri) {
		super(endpointUri);
	}

	@Override
	public GeoResource getGeoResource(String uri) throws DaoException {
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetResourceQuery(uri));

		try {
			ResultSet queryResult = execution.execSelect();
			GeoResource resource = null;
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String positionUri = solution.getResource("position").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();

					if (resource == null) {
						resource = new AemetResource(uri, new PointBean(positionUri, lng, lat));
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
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
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, null);
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, int max)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, new Integer(max));
	}

	@Override
	public List<GeoResourceOverlay> getGeoResourceOverlays(StatisticDefinition statisticDefinition,
			BoundingBox boundingBox, Set<FacetConstraint> constraints) throws DaoException {
		// TODO What can be done here?
		return Collections.emptyList();
	}

	@Override
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox) throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("select distinct ?class ?label where { ");
		queryBuffer.append("?x <" + Geo.lat + "> _:lat. ");
		queryBuffer.append("?x <" + Geo.lng + "> _:lng. ");
		queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		queryBuffer.append("optional {?class <" + RDFS.label + "> ?label . }}");

		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, queryBuffer.toString());

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
		// TODO not applicable
		return Collections.emptyList();
	}

	@Override
	public List<Resource> getStatisticDatasets() throws DaoException {
		// TODO What can be done here?
		return Collections.emptyList();
	}

	/* --------------------- helper methods --- */
	
	private List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer max)
			throws DaoException {
		HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri,
				createGetResourcesQuery(boundingBox, constraints, max));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					String positionUri = solution.getResource("position").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();
					GeoResource resource=result.get(uri);
					
					if (resource == null) {
						resource = new AemetResource(uri, new PointBean(positionUri, lng, lat));
						result.put(uri, resource);
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}
					if(solution.contains("facetID") && solution.contains("facetValueID")){
						String facetID=solution.getResource("facetID").getURI();
						String facetValueID=solution.getResource("facetValueID").getURI();
						resource.setFacetConstraint(new FacetConstraint(facetID, facetValueID));
					}

				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				}
			}

			return new ArrayList<GeoResource>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	/**
	 * @param boundingBox
	 * @param constraints
	 * @param max
	 * @return
	 */
	private String createGetResourcesQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints,
			Integer limit) {
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?r ?position ?lat ?lng ?label ?facetID ?facetValueID ");
		query.append("WHERE { ");
		query.append("?r <http://www.w3.org/2003/01/geo/wgs84_pos#location> ?position. ");
		query.append("?position <" + Geo.lat + "> ?lat. ");
		query.append("?position <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label } .");
		if (constraints != null) {
			for (FacetConstraint constraint : constraints) {
				query.append("{?r <"+constraint.getFacetId()+"> <"+constraint.getFacetValueId()+">.");
				query.append("?r <"+constraint.getFacetId()+"> ?facetValueID.");
				query.append("?r ?facetID <"+constraint.getFacetValueId()+">");
				query.append("} UNION");
			}
			query.delete(query.length() - 5, query.length());
		}
		
		//filters
		if (boundingBox!=null) {
			query = addBoundingBoxFilter(query, boundingBox);
		}
		
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetResourceQuery(String uri) {
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?position ?lat ?lng ?label ");
		query.append("WHERE { ");
		query.append("<"+uri+"> <http://www.w3.org/2003/01/geo/wgs84_pos#location> ?position. ");
		query.append("?position <" + Geo.lat + "> ?lat. ");
		query.append("?position <" + Geo.lng + "> ?lng . ");
		query.append("OPTIONAL { <"+uri+"> <" + RDFS.label + "> ?label } .");
		query.append("}");
		return query.toString();
	}

	@Override
	public List<GeoResource> getNextPoints(BoundingBox boundingBox, int limit)
			throws DaoException {
		HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();
		QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri,
				createGetNextPoints(boundingBox, limit));
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					String positionUri = solution.getResource("position").getURI();
					double lat = solution.getLiteral("lat").getDouble();
					double lng = solution.getLiteral("lng").getDouble();
					GeoResource resource=result.get(uri);
					
					if (resource == null) {
						resource = new AemetResource(uri, new PointBean(positionUri, lng, lat));
						result.put(uri, resource);
					}
					if (solution.contains("label")) {
						Literal labelLiteral = solution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				}
			}

			return new ArrayList<GeoResource>(result.values());
		} catch (Exception e) {
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}
	private String createGetNextPoints(BoundingBox boundingBox, int limit){
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  SELECT distinct ?r ?position ?lat ?lng ?label ?facetID ?facetValueID ");
		query.append("WHERE { ");
		query.append("?r <http://www.w3.org/2003/01/geo/wgs84_pos#location> ?position. ");
		query.append("?position <" + Geo.lat + "> ?lat. ");
		query.append("?position <" + Geo.lng + "> ?lng . ");
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
