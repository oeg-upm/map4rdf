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
package es.upm.fi.dia.oeg.map4rdf.server.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.AemetDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.DbPediaDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.GeoLinkedDataDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.GeoSparqlDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.VCardDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.WebNMasUnoImpl;

/**
 * @author Alexander De Leon
 */
public class BrowserModule extends AbstractModule {

	@Override
	protected void configure() {
	};

	@Provides
	Map4rdfDao provideDao(@Named(ParameterNames.GEOMETRY_MODEL) Constants.GeometryModel model,
			@Named(ParameterNames.ENDPOINT_URL) String endpointUri,
			@Named(ParameterNames.DEFAULT_PROJECTION) String defaultProjection,
			@Named(ParameterNames.ENDPOINT_URL_GEOSPARQL) String endpointGeoSparqlUri) {
		switch (model) {
		case OEG:
			return new GeoLinkedDataDaoImpl(endpointUri,defaultProjection);
		case DBPEDIA:
			return new DbPediaDaoImpl(endpointUri,defaultProjection);
		case VCARD:
			return new VCardDaoImpl(endpointUri,defaultProjection);
		case GEOSPARQL:
			return new GeoSparqlDaoImpl(endpointUri,endpointGeoSparqlUri,defaultProjection);
		case AEMET:
			return new AemetDaoImpl(endpointUri,defaultProjection);
		case WEBNMASUNO:
			return new WebNMasUnoImpl(endpointUri,defaultProjection);
		default:
			// make compiler happys
			return null;
		}
	}
}
