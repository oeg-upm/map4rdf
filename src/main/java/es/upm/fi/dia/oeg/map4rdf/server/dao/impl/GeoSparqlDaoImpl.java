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
package es.upm.fi.dia.oeg.map4rdf.server.dao.impl;

import java.util.ArrayList;
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

import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.util.GeoUtils;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Francisco Siles
 */
public class GeoSparqlDaoImpl extends CommonDaoImpl implements Map4rdfDao {

	private static final Logger LOG = Logger.getLogger(GeoSparqlDaoImpl.class);
	private final String geoSparqlEndpoint;
	
	@Inject
	public GeoSparqlDaoImpl(@Named(ParameterNames.ENDPOINT_URL) String endpointUri,@Named(ParameterNames.ENDPOINT_URL_GEOSPARQL)String geoSparqlEndpoointUrl ,String defaultProjection) {
		super(endpointUri,defaultProjection);
		this.geoSparqlEndpoint=geoSparqlEndpoointUrl;
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, int max)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, new Integer(max));
	}

	@Override
	public List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints)
			throws DaoException {
		return getGeoResources(boundingBox, constraints, null);
	}

	private List<GeoResource> getGeoResources(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer max)
			throws DaoException {
		HashMap<String, GeoResource> result = new HashMap<String, GeoResource>();
		QueryExecution execution = null;
		if(geoSparqlEndpoint==null || geoSparqlEndpoint.isEmpty()){
			execution = QueryExecutionFactory.sparqlService(endpointUri,
					createGetResourcesQuery(boundingBox, constraints, max));
		}else if (this.endpointUri == null || this.endpointUri.isEmpty()){
			execution = QueryExecutionFactory.sparqlService(geoSparqlEndpoint,
					createGetALLGeoSparqlResourcesQuery(boundingBox, constraints, max));
		}else{
			execution = QueryExecutionFactory.sparqlService(geoSparqlEndpoint,
					createGetGeoSparqlResourcesQuery(boundingBox, constraints, max));
		}
		try {
			ResultSet queryResult = execution.execSelect();
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String uri = solution.getResource("r").getURI();
					String geoUri = solution.getResource("geosparqlwkt").getURI();
					String wkt = solution.getLiteral("wkt").getString();
					//TODO: Remove this if when we can obtain crs in endpoint.
					if(wkt.toLowerCase().contains("crs84")){
					GeoResource resource = result.get(uri);
					if (resource == null) {
						List<Geometry> geometries=GeoUtils.getWKTGeometries(geoUri, "", wkt,"EPSG:4326");
						if(!geometries.isEmpty()){
							resource = new GeoResource(uri, geometries.get(0));
							for(int i=1;i<geometries.size();i++){
								resource.addGeometry(geometries.get(i));
							}
							result.put(uri, resource);
						}
					} else if (!resource.hasGeometry(geoUri)) {
						List<Geometry> geometries=GeoUtils.getWKTGeometries(geoUri, "", wkt,"EPSG:4326");
						if(!geometries.isEmpty()){
							for(int i=0;i<geometries.size();i++){
								resource.addGeometry(geometries.get(i));
							}
						}
					}
					getOtherInfo(uri, resource, solution);
					if(solution.contains("facetID") && solution.contains("facetValueID")){
						String facetID=solution.getResource("facetID").getURI();
						String facetValueID=solution.getResource("facetValueID").getURI();
						resource.setFacetConstraint(new FacetConstraint(facetID, facetValueID));
					}
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: " + e.getMessage());
				} catch (Exception e) {
					LOG.error(e);
				}
			}
			return new ArrayList<GeoResource>(result.values());
		} catch (Exception e) {
			LOG.error(e);
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}
	
	//TODO implement Wikipedia link in GeoSparql Dao(ALL METHODS)
	@Override
	public GeoResource getGeoResource(String uri) throws DaoException {
		QueryExecution execution=null;
		if(geoSparqlEndpoint==null || geoSparqlEndpoint.isEmpty()){
			execution = QueryExecutionFactory.sparqlService(endpointUri, createGetResourceQuery(uri));
		}else{
			execution = QueryExecutionFactory.sparqlService(geoSparqlEndpoint, createGetResourceQuery(uri));
		}
		try {
			ResultSet queryResult = execution.execSelect();
			GeoResource resource = null;
			while (queryResult.hasNext()) {
				QuerySolution solution = queryResult.next();
				try {
					String geoUri = solution.getResource("geosparql").getURI();
					String wkt = solution.getLiteral("wkt").getString();
					//TODO: Remove this "if" when we can obtain crs in endpoint.
					if(wkt.toLowerCase().contains("crs84")){
						//TODO: Solve problem with only accept EPSG:4326
						List<Geometry> geometries=GeoUtils.getWKTGeometries(geoUri, "", wkt,"EPSG:4326");
						if(!geometries.isEmpty()){
							resource = new GeoResource(uri, geometries.get(0));
							for(int i=1;i<geometries.size();i++){
								resource.addGeometry(geometries.get(i));
							}
						}
						getOtherInfo(uri, resource, solution);
					}
				} catch (NumberFormatException e) {
					LOG.warn("Invalid Latitud or Longitud value: ",e);
				} catch (Exception e) {
					LOG.fatal("Unexpected error ocurred: ",e);
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
	public List<GeoResourceOverlay> getGeoResourceOverlays(StatisticDefinition statisticDefinition,
			BoundingBox boundingBox, Set<FacetConstraint> constraints) throws DaoException {
		//Nothing to do
		return new ArrayList<GeoResourceOverlay>();
	}

	@Override
	public List<Facet> getFacets(String predicateUri, BoundingBox boundingBox) throws DaoException {
		Map<String, Facet> result = new HashMap<String, Facet>();
		StringBuilder queryBuffer = new StringBuilder();
		queryBuffer.append("PREFIX geosparql: <http://www.opengis.net/ont/geosparql#> ");
		queryBuffer.append("select distinct ?class ?label where { ");
		queryBuffer.append("?x geosparql:hasGeometry ?g. ");
		queryBuffer.append("?x <" + predicateUri + "> ?class . ");
		queryBuffer.append("optional {?class <" + RDFS.label + "> ?label . }");
		queryBuffer.append("}");
		QueryExecution execution;
		if(endpointUri==null || endpointUri.isEmpty()){
			execution = QueryExecutionFactory.sparqlService(geoSparqlEndpoint, queryBuffer.toString());
		}else{
			execution = QueryExecutionFactory.sparqlService(endpointUri, queryBuffer.toString());
		}
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
			e.printStackTrace();
			LOG.error(e);
			throw new DaoException("Unable to execute SPARQL query", e);
		} finally {
			execution.close();
		}
	}

	@Override
	public List<Year> getYears(String datasetUri) throws DaoException {
		//Nothing to do.
		return new ArrayList<Year>();
	}

	@Override
	public List<Resource> getStatisticDatasets() throws DaoException {
		//Nothing to do.
		return new ArrayList<Resource>();
	}
	@Override
	public List<GeoResource> getNextPoints(BoundingBox boundingBox, int max) throws DaoException {
		return getGeoResources(boundingBox, null,max);
	}
	private void getOtherInfo(String resourceUri,GeoResource resource,QuerySolution solution)throws DaoException{
		if(geoSparqlEndpoint==null || this.endpointUri == null || geoSparqlEndpoint.isEmpty() || this.endpointUri.isEmpty()){
			if (solution.contains("label")) {
				Literal labelLiteral = solution.getLiteral("label");
				resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
			}
			if(solution.contains("seeAlso")){
				String seeAlso = solution.getResource("seeAlso").getURI();
				if(seeAlso.toString().toLowerCase().contains("wikipedia")){
					resource.addWikipediaURL(seeAlso);
				}
			}
		}else{
			QueryExecution execution = QueryExecutionFactory.sparqlService(endpointUri, createGetResourceOtherInfo(resourceUri));
			try {
				ResultSet queryResult = execution.execSelect();
				while (queryResult.hasNext()) {
					QuerySolution newSolution = queryResult.next();
					if (solution.contains("label")) {
						Literal labelLiteral = newSolution.getLiteral("label");
						resource.addLabel(labelLiteral.getLanguage(), labelLiteral.getString());
					}
					if(solution.contains("seeAlso")){
						String seeAlso = newSolution.getResource("seeAlso").getURI();
						if(seeAlso.toString().toLowerCase().contains("wikipedia")){
							resource.addWikipediaURL(seeAlso);
						}
					}
				}
			} catch (Exception e) {
				throw new DaoException("Unable to execute SPARQL query", e);
			} finally {
				execution.close();
			}
		}
	}
	private String createGetGeoSparqlResourcesQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer limit){
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		query.append("PREFIX geosparql: <http://www.opengis.net/ont/geosparql#> ");
		query.append("PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> ");
		if(constraints!=null){
			query.append("SELECT distinct ?r ?geosparqlwkt ?wkt ?facetID ?facetValueID ");
		}else{
			query.append("SELECT distinct ?r ?geosparqlwkt ?wkt ");
		}
		query.append("WHERE { ");
		query.append("?r geosparql:hasGeometry  ?geosparqlwkt.");
		query.append("?geosparqlwkt geosparql:asWKT  ?wkt.");
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
			query = addGeoSparqlBoundingBoxFilter(query, boundingBox);
		}
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private String createGetALLGeoSparqlResourcesQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer limit) {
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		query.append("PREFIX geosparql: <http://www.opengis.net/ont/geosparql#> ");
		query.append("PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> ");
		if(constraints!=null){
			query.append("SELECT distinct ?r ?label ?geosparqlwkt ?wkt ?seeAlso ?facetID ?facetValueID ");
		}else{
			query.append("SELECT distinct ?r ?label ?geosparqlwkt ?wkt ?seeAlso ");
		}
		query.append("WHERE { ");
		query.append("?r geosparql:hasGeometry  ?geosparqlwkt.");
		query.append("?geosparqlwkt geosparql:asWKT  ?wkt.");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label }. ");
		query.append("OPTIONAL { ?r <" +RDFS.seeAlso + "> ?seeAlso}. ");
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
			query = addGeoSparqlBoundingBoxFilter(query, boundingBox);
		}
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private String createGetResourceOtherInfo(String resourceUri){
		StringBuilder query = new StringBuilder("PREFIX geosparql: <http://www.opengis.net/ont/geosparql#> ");
		query.append("SELECT ?label ?seeAlso ");
		query.append("WHERE { ");
		query.append("OPTIONAL { <" + resourceUri + "> <" + RDFS.label + "> ?label } .");
		query.append("OPTIONAL { <" + resourceUri + "> <" + RDFS.seeAlso + "> ?seeAlso}. ");
		query.append("}");
		return query.toString();
	}
	private String createGetResourcesQuery(BoundingBox boundingBox, Set<FacetConstraint> constraints, Integer limit) {
		StringBuilder query = new StringBuilder("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		query.append("PREFIX geosparql: <http://www.opengis.net/ont/geosparql#> ");
		query.append("PREFIX strdf: <http://strdf.di.uoa.gr/ontology#> ");
		if(constraints!=null){
			query.append("SELECT distinct ?r ?label ?geosparqlwkt ?wkt ?seeAlso ?facetID ?facetValueID ");
		}else{
			query.append("SELECT distinct ?r ?label ?geosparqlwkt ?wkt ?seeAlso ");
		}
		query.append("WHERE { ");
		/*query.append("?r <http://www.opengis.net/ont/geosparql/geometry>  ?geosparqlgml.");
		query.append("?geosparqlgml <http://www.opengis.net/ont/geosparql/asGML>  ?gml.");*/
		query.append("?r geosparql:hasGeometry  ?geosparqlwkt.");
		query.append("?geosparqlwkt geosparql:asWKT  ?wkt.");
		//query.append("?geosparqlwkt <"+RDF.type+"> ?geoType.");
		//query.append("{?geo" + "<"+ Geo.lat + ">" +  " ?lat;"  + "<" + Geo.lng + ">" + " ?lng" + ".}");
		query.append("OPTIONAL { ?r <" + RDFS.label + "> ?label }. ");
		query.append("OPTIONAL { ?r <" +RDFS.seeAlso + "> ?seeAlso}. ");
		if (constraints != null) {
			query.append("?r ?facetID ?facetValueID. ");
			query.append("FILTER(");
			for (FacetConstraint constraint : constraints) {
				query.append("(?facetID IN(");
				query.append("<"+constraint.getFacetId()+">)");
				query.append(" && ?facetValueID IN(");
				query.append("<"+constraint.getFacetValueId()+">)) || ");
			}
			query.delete(query.length() - 3, query.length());
			query.append(").");
		}
		//filters
		//NORMAL endpoint not applicable geosparql filters
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetResourceQuery(String uri) {
		StringBuilder query = new StringBuilder("PREFIX geosparql: <http://www.opengis.net/ont/geosparql#> ");
		query.append("SELECT ?label ?geosparql ?wkt ?seeAlso ");
		query.append("WHERE { ");
		query.append("<" + uri + "> geosparql:hasGeometry ?geosparql. ");
		query.append("?geosparql geosparql:asWKT ?wkt.");
		query.append("OPTIONAL { <" + uri + "> <" + RDFS.label + "> ?label } .");
		query.append("OPTIONAL { <" + uri + "> <" + RDFS.seeAlso + "> ?seeAlso}. ");
		query.append("}");
		return query.toString();
	}
	private StringBuilder addGeoSparqlBoundingBoxFilter(StringBuilder originalQuery, BoundingBox boundingBox) {
		StringBuilder query= new StringBuilder(originalQuery.toString());
		query.append("?r geosparql:hasGeometry  ?geosparqlwkttocompare.");
		query.append("?geosparqlwkttocompare geosparql:asWKT  ?wkttocompare.");
		query.append(" FILTER(");
		query.append("strdf:intersects(\"POLYGON((");
		query.append(boundingBox.getTop().getX()+" "+boundingBox.getTop().getY()+", ");
		query.append(boundingBox.getRight().getX()+" "+boundingBox.getRight().getY()+", ");
		query.append(boundingBox.getBottom().getX()+" "+boundingBox.getBottom().getY()+", ");
		query.append(boundingBox.getLeft().getX()+" "+boundingBox.getLeft().getY()+", ");
		query.append(boundingBox.getTop().getX()+" "+boundingBox.getTop().getY());
		query.append("))\"^^<http://strdf.di.uoa.gr/ontology#WKT>,?wkttocompare)).");
		return query;
	}
}
