/**
 * Copyright (c) 2011 Alexander De Leon Battista
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
package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObsForProperty;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.AemetIntervalo;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
public class GetAemetObsForPropertyHandler implements ActionHandler<GetAemetObsForProperty, ListResult<AemetObs>> {

	private final String endpointUri;

	@Inject
	public GetAemetObsForPropertyHandler(@Named(ParameterNames.ENDPOINT_URL)String endpointUri) {
		this.endpointUri=endpointUri;
	}

	@Override
	public Class<GetAemetObsForProperty> getActionType() {
		return GetAemetObsForProperty.class;
	}

	@Override
	public ListResult<AemetObs> execute(GetAemetObsForProperty action, ExecutionContext context) throws ActionException {
		return new ListResult<AemetObs>(getObservations(action.getStationUri(), action.getPropertyUri(),
				action.getStart(), action.getEnd()));
	}

	@Override
	public void rollback(GetAemetObsForProperty action, ListResult<AemetObs> result, ExecutionContext context)
			throws ActionException {
		// nothing to do
	}
	public List<AemetObs> getObservations(String stationUri, String propertyUri, AemetIntervalo start, AemetIntervalo end) {
		List<AemetObs> result = new ArrayList<AemetObs>();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createQueryGetObsForProperty(stationUri, propertyUri, start, end));
		ResultSet queryResult2 = exec2.execSelect();
		while (queryResult2.hasNext()) {
			/*
			 * String id,String uriObs,String estacion,String valor, String
			 * calidad, String prop, String feature, String intervalo ?obs
			 * ?nombreEst ?prop ?dato ?q ?intervalo
			 */
			QuerySolution solution2 = queryResult2.next();
			String idObs = solution2.getResource("obs").getURI();
			String nombreEstacion = stationUri;
			//String prop = propertyUri;
			double dato = solution2.getLiteral("dato").getDouble();
			String q = "No disponible";
			if (solution2.contains("q")) {
				q = solution2.getLiteral("q").getLexicalForm();
			}
			int min, h, dia, mes, anno;
			min = solution2.getLiteral("min").getInt();
			h = solution2.getLiteral("h").getInt();
			dia = solution2.getLiteral("dia").getInt();
			mes = solution2.getLiteral("mes").getInt();
			anno = solution2.getLiteral("anno").getInt();
			AemetIntervalo intervalo = new AemetIntervalo(anno, mes, dia, h, min);
			/*
			 * AemetObs observ = new AemetObs(idObs, nombreEstacion, dato, q,
			 * prop, "", intervalo);
			 */
			Resource estation = new Resource(stationUri);
			estation.addLabel("", nombreEstacion);

			Resource propR = new Resource(propertyUri);

			AemetObs observ = new AemetObs(idObs, estation, dato, q, propR, "", intervalo);
			result.add(observ);
		}

		return result;
	}
	private String createQueryGetObsForProperty(String station, String property, AemetIntervalo start, AemetIntervalo end) {
		StringBuilder query = new StringBuilder(
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n\n SELECT distinct ?obs ?dato ?q ?h ?min ?dia ?mes ?anno ");
		query.append("WHERE { ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <" + station + ">. ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <" + property + "> . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/valueOfObservedData> ?dato . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedDataQuality> ?q . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedInInterval> ?inter . ");
		query.append("?inter <http://www.w3.org/2006/time#hasBeginning> ?instant . ");
		query.append("?instant <http://www.w3.org/2006/time#inDateTime> ?tiempoFecha . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#hour> ?h . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#minute> ?min . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#day> ?dia . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#month> ?mes . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#year> ?anno . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#inXSDDateTime> ?dt . ");
		query.append("FILTER(?dt >= xsd:dateTime(\"" + start.asXSDDateTime() + "\")). ");
		query.append("FILTER(?dt <= xsd:dateTime(\"" + end.asXSDDateTime() + "\")). ");
		query.append("}");

		return query.toString();
	}
}
