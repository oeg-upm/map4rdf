package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetWebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoTrip;

/**
 * @author Daniel Garijo
 * Adapted by: @author Francisco Siles
 */
public class GetWebNMasUnoResourceHandler implements ActionHandler<GetWebNMasUnoResource, SingletonResult<WebNMasUnoResourceContainer>> {

	private String endpointUri;

	@Inject
	public GetWebNMasUnoResourceHandler(@Named(ParameterNames.ENDPOINT_URL) String endpointUri) {
		this.endpointUri = endpointUri;
	}

	@Override
	public SingletonResult<WebNMasUnoResourceContainer> execute(
			GetWebNMasUnoResource action, ExecutionContext context)
			throws ActionException {
		String uri = action.getUri();
		if (uri == null || uri.length() == 0) {
			throw new ActionException("Invalid URI: " + uri);
		}
		WebNMasUnoResourceContainer resource = getDatosGuiasViajes(uri);
		return new SingletonResult<WebNMasUnoResourceContainer>(resource);
	}

	private WebNMasUnoResourceContainer getDatosGuiasViajes(String uri) {
		WebNMasUnoResourceContainer resource = new WebNMasUnoResourceContainer();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetGuidesTripsQuery(100, uri));
		ResultSet queryResult2 = exec2.execSelect();
		// guia
		String uriGuide = "", urlGuide = "", titleGuide = "", dateGuia = "";
		// viaje
		String uriTrip = "", titTrip = "", idIt = "", tripURL = "", dateViaje = "";
		
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			if (solution2.contains("noticia")) {
				uriGuide = solution2.getResource("noticia").getURI();
			} else {
				uriGuide = "";
			}
			if (solution2.contains("title")) {
				titleGuide = solution2.getLiteral("title").getLexicalForm();
			} else {
				titleGuide = "";
			}
			if (solution2.contains("url")) {
				urlGuide = solution2.getLiteral("url").getLexicalForm();
			} else {
				urlGuide = "";
			}
			if (solution2.contains("dateG")) {
				dateGuia = solution2.getLiteral("dateG").getLexicalForm();
			} else {
				dateGuia = "";
			}
			if (solution2.contains("trip")) {
				uriTrip = solution2.getResource("trip").getURI();
			} else {
				uriTrip = "";
			}
			if (solution2.contains("it")) {
				idIt = solution2.getResource("it").getURI();
			} else {
				idIt = "";
			}
			if (solution2.contains("tripTitle")) {
				titTrip = solution2.getLiteral("tripTitle").getLexicalForm();
			} else {
				titTrip = "";
			}
			if (solution2.contains("tripURL")) {
				tripURL = solution2.getLiteral("tripURL").getLexicalForm();
			} else {
				tripURL = "";
			}
			if (solution2.contains("dateV")) {
				dateViaje = solution2.getLiteral("dateV").getLexicalForm();
			} else {
				dateViaje = "";
			}

			if (!uriGuide.equals("")) {
				WebNMasUnoGuide g = new WebNMasUnoGuide(titleGuide, urlGuide,
						uriGuide, dateGuia);
				resource.addGuide(g);
			} else if (!uriTrip.equals("")) {
				WebNMasUnoTrip t = new WebNMasUnoTrip(titTrip, tripURL,
						uriTrip, idIt, dateViaje);
				resource.addTrip(t);
			}

		}
		return resource;

	}

	@Override
	public Class<GetWebNMasUnoResource> getActionType() {
		return GetWebNMasUnoResource.class;
	}

	@Override
	public void rollback(GetWebNMasUnoResource action,
			SingletonResult<WebNMasUnoResourceContainer> result,
			ExecutionContext context) throws ActionException {
		// nothing to do

	}
	
	private String createGetGuidesTripsQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?noticia ?title ?url ?dateG ?trip ?tripTitle ?tripURL ?it ?dateV ");
		query.append("WHERE { ");
		query.append("{?noticia <http://www.w3.org/2003/01/geo/wgs84_pos#location> "
				+ "<" + uri + "> . ");
		query.append("?noticia a <http://webenemasuno.linkeddata.es/ontology/OPMO/Guide>.");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#title> ?title . }");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#created_at> ?dateG . }");
		query.append("OPTIONAL {?noticia <http://openprovenance.org/model/opmo#pname> ?url . }}");
		query.append("UNION");
		query.append("{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it. ");
		query.append("?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?part. ");
		query.append("?part <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> "
				+ "<" + uri + "> .");
		query.append("OPTIONAL{?trip <http://openprovenance.org/model/opmo#pname> ?tripURL. }");
		query.append("OPTIONAL {?trip <http://rdfs.org/sioc/ns#created_at> ?dateV . }");
		query.append("OPTIONAL{?trip <http://purl.org/dc/terms/title> ?tripTitle. }}");
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private String getImageURL(String URL){
		String convertedURL=getRedirectedURL(URL);
    	convertedURL=convertedURL.replace("elviajero.elpais.com","ep01.epimg.net");
    	convertedURL=convertedURL.replace("diario", "diario/imagenes");
    	convertedURL=convertedURL.replace(".html", "_0000000000_sumario_normal.jpg");
        return convertedURL;
	}
	private String getNewPname(String URL){
		return getRedirectedURL(URL);
	}
	private String getRedirectedURL(String URL){
		String toReturn="";
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(
			        URL).openConnection();
		    con.connect();
		    con.setInstanceFollowRedirects(false);
		    int responseCode = con.getResponseCode();
		    if ((responseCode / 100) == 3) {
		        String newLocationHeader = con.getHeaderField("Location");
		        responseCode = con.getResponseCode();
		        String urlFull=con.getURL().toString();
		        urlFull=urlFull.replaceAll(con.getURL().getPath(), newLocationHeader);
		        toReturn=urlFull;
		    }
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		return toReturn;
	}
}
