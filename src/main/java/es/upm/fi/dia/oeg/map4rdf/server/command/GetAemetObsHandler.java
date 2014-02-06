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

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObs;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetIntervalo;
import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetObs;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Daniel Garijo
 * @author Francisco Siles
 */
public class GetAemetObsHandler implements ActionHandler<GetAemetObs, ListResult<AemetObs>> {

	private final String endpointUri;
	@Inject
	public GetAemetObsHandler(@Named(ParameterNames.ENDPOINT_URL)String endpointUri) {
		this.endpointUri=endpointUri;
	}

	@Override
	public ListResult<AemetObs> execute(GetAemetObs action, ExecutionContext context) throws ActionException {
		String uri = action.getUri();
		if (uri == null || uri.length() == 0) {
			throw new ActionException("Invalid URI: " + uri);
		}
		try {
			return getDatosObservacion(uri);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ActionException("Data access error", e);	
		}
	}

	@Override
	public Class<GetAemetObs> getActionType() {
		return GetAemetObs.class;
	}

	@Override
	public void rollback(GetAemetObs action, ListResult<AemetObs> result, ExecutionContext context)
			throws ActionException {
		// nothing to do

	}
	
	private ListResult<AemetObs> getDatosObservacion(String uri) throws Exception {
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri, createGetMaxDate(uri)); // cogemos
		ResultSet queryResult2 = exec2.execSelect();
		String date = null;
		while (queryResult2.hasNext()) {
			QuerySolution sol = queryResult2.next();
                        if(sol.contains("date")){
                            date = sol.getLiteral("date").getString();
                        }

		}
		if (date == null) {
			return null;
		}
		return getDatosObservacion(uri, date);
	}
	private String createGetMaxDate(String uri) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT (max(?dt) AS ?date)");
		//StringBuilder query = new StringBuilder("SELECT ?dt) ");
		query.append("WHERE { ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+uri+"> . ");
		query.append("?obs <http://aemet.linkeddata.es/ontology/observedInInterval> ?inter . ");
		query.append("?inter <http://www.w3.org/2006/time#hasBeginning> ?instant . ");
		query.append("?instant <http://www.w3.org/2006/time#inDateTime> ?tiempoFecha . ");
		query.append("?tiempoFecha <http://www.w3.org/2006/time#inXSDDateTime> ?dt . }");

		return query.toString();
	}
	private ListResult<AemetObs> getDatosObservacion(String uri, String date) throws DaoException {
		// throw new UnsupportedOperationException("Not supported yet.");
		// Date d = new Date();
		List<AemetObs> obs=new ArrayList<AemetObs>();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri, createGetObs(100, uri, date)); // cogemos
																												// las
																												// 100
																												// ultimas
		ResultSet queryResult2 = exec2.execSelect();
		while (queryResult2.hasNext()) {
			/*
			 * String id,String uriObs,String estacion,String valor, String
			 * calidad, String prop, String feature, String intervalo ?obs
			 * ?nombreEst ?prop ?dato ?q ?intervalo
			 */
			QuerySolution solution2 = queryResult2.next();
			String sttionUri = uri;
			String idObs = solution2.getResource("obs").getURI();
			String nombreEstacion = solution2.getLiteral("est").getLexicalForm();
			String prop = solution2.getResource("prop").getURI();
			String propLabel = solution2.getResource("prop").getLocalName();
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
			
			Resource	station = new Resource(sttionUri);
			station.addLabel("", nombreEstacion);
			
			Resource propR = new Resource(prop);
			propR.addLabel("", propLabel);
			AemetObs observ = new AemetObs(idObs, station, dato, q, propR, "", intervalo);
			obs.add(observ);
			//(aemetR).addObs(observ);
		}

		return new ListResult<AemetObs>(obs);

	}
	private String createGetObs(Integer limit, String uri, String date) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?obs ?est ?prop ?dato ?q ?h ?min ?dia ?mes ?anno ");
		query.append("WHERE { ");
		query.append("<"+uri+"> <http://aemet.linkeddata.es/ontology/stationName> ?est . ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+uri+"> . ");
		query.append("?obs <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> ?prop . ");
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
		query.append("?tiempoFecha <http://www.w3.org/2006/time#inXSDDateTime> \"" + date
				+ "\"^^<http://www.w3.org/2001/XMLSchema#dateTime> . }");

		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

}
