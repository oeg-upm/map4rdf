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
package es.upm.fi.dia.oeg.map4rdf.server.dao;

import java.util.List;
import java.util.Set;

import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResourceOverlay;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.SubjectDescription;
import es.upm.fi.dia.oeg.map4rdf.share.StatisticDefinition;
import es.upm.fi.dia.oeg.map4rdf.share.Year;

/**
 * @author Alexander De Leon
 */
public interface Map4rdfDao {
	//TODO to remove. Dont use.
	List<GeoResource> getGeoResources(BoundingBox boundingBox)
			throws DaoException;
	
	//For get the resource in "uri" query param.
	GeoResource getGeoResource(String uri) throws DaoException;
	
	//For KML service
	List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints) throws DaoException;
	
	//For facet changed
	List<GeoResource> getGeoResources(BoundingBox boundingBox,
			Set<FacetConstraint> constraints, int max) throws DaoException;
	
	//When user select a statistic dataset and time to view, this method get the values
	//for draw statistics.
	List<GeoResourceOverlay> getGeoResourceOverlays(
			StatisticDefinition statisticDefinition, BoundingBox boundingBox,
			Set<FacetConstraint> constraints) throws DaoException;

	//For get facets
	List<Facet> getFacets(String predicateUri, BoundingBox boundingBox)
			throws DaoException;
	
	//Get years of statistic dataset. Different years of measurement.
	//Years for slidebar. This method is called when user select a statistic dataset.
	List<Year> getYears(String datasetUri) throws DaoException;
	
	//Get statistics datasets.
	//This method is called on client app startup. This get the different types of statistics
	List<Resource> getStatisticDatasets() throws DaoException;
	
	//This method get all properties of URI. EditResource use this method.
	List<SubjectDescription> getSubjectDescription(String subject) throws DaoException;
	
	//This method is for get labels of a URI. EditResource use this method.
	String getLabel(String uri) throws DaoException;
	
	//This method is for get resources nearby a point. This is similar to
	//List<GeoResource> getGeoResources(BoundingBox boundingBox,Set<FacetConstraint> constraints, int max)
	//We can use getResources instead of this.
	//TODO: Analyze if this method will can remove. 
	List<GeoResource> getNextPoints(BoundingBox boundingBox, int max) throws DaoException;

}
