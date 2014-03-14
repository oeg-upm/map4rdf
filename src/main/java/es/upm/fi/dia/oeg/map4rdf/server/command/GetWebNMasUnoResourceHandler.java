package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.ibm.icu.util.Calendar;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetWebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.TripProvenance;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.TripProvenance.TripProvenanceType;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoImage;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoTrip;

/**
 * @author Daniel Garijo Adapted by: @author Francisco Siles
 */
public class GetWebNMasUnoResourceHandler
		implements
		ActionHandler<GetWebNMasUnoResource, SingletonResult<WebNMasUnoResourceContainer>> {

	private String endpointUri;
	private Logger logger = Logger.getLogger(GetWebNMasUnoResourceHandler.class);
	@Inject
	public GetWebNMasUnoResourceHandler(
			@Named(ParameterNames.ENDPOINT_URL) String endpointUri) {
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

	private WebNMasUnoResourceContainer getDatosGuiasViajes(String uri) {
		WebNMasUnoResourceContainer resource = new WebNMasUnoResourceContainer();
		List<WebNMasUnoGuide> guides = getGuides(100, uri);
		if (guides != null && !guides.isEmpty()) {
			resource.addAllGuides(guides);
		}
		List<WebNMasUnoTrip> trips = getTrips(100, uri);
		if (trips != null && !trips.isEmpty()) {
			resource.addAllTrips(trips);
		}
		return resource;

	}

	private List<WebNMasUnoGuide> getGuides(int i, String uri) {
		/*
		 * TODO modify this method, wait for Ocorcho to change the model. The
		 * images are property in endpoint. The application does not need to get
		 * redirected urls and does not need to check urls.
		 */
		Map<String, WebNMasUnoGuide> guides = new HashMap<String, WebNMasUnoGuide>();
		long start = Calendar.getInstance().getTimeInMillis();
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpointUri,
				createGetGuidesQuery(100, uri));
		ResultSet queryResult = exec.execSelect();
		long medium1 = Calendar.getInstance().getTimeInMillis();
		long checkURI = 0;
		long checkImage = 0;
		int imagenes = 0;
		while (queryResult.hasNext()) {
			// guia
			String uriGuide = "", urlGuide = "", titleGuide = "", dateGuia = "";
			QuerySolution solution = queryResult.next();
			if (solution.contains("noticia")) {
				uriGuide = solution.getResource("noticia").getURI();
			} else {
				uriGuide = "";
			}
			if (solution.contains("title")) {
				titleGuide = solution.getLiteral("title").getLexicalForm();
			} else {
				titleGuide = "";
			}
			if (solution.contains("url")) {
				urlGuide = solution.getLiteral("url").getLexicalForm();
			} else {
				urlGuide = "";
			}
			if (solution.contains("dateG")) {
				dateGuia = solution.getLiteral("dateG").getLexicalForm();
			} else {
				dateGuia = "";
			}
			long tempCheckURIStart = Calendar.getInstance().getTimeInMillis();
			if (!uriGuide.equals("") && !uriGuide.isEmpty()
					&& !urlGuide.equals("") && !urlGuide.isEmpty()
					&& checkIfURLExists(urlGuide)) {
				long tempCheckURIStop = Calendar.getInstance()
						.getTimeInMillis();
				checkURI += tempCheckURIStop - tempCheckURIStart;
				WebNMasUnoGuide g;
				if (guides.containsKey(uriGuide)
						&& guides.get(uriGuide) != null) {
					g = guides.get(uriGuide);
				} else {
					g = new WebNMasUnoGuide(titleGuide, urlGuide, uriGuide,
							dateGuia);
					guides.put(uriGuide, g);
				}
				WebNMasUnoImage image = null;
				if (solution.contains("uriImage")
						&& solution.contains("pnameImage")) {
					String uriImage = solution.getResource("uriImage").getURI();
					String pnameImage = solution.getLiteral("pnameImage")
							.getLexicalForm();
					long tempCheckImageStart = Calendar.getInstance()
							.getTimeInMillis();
					String imageURL = getImageURL(urlGuide, g.getImages()
							.size());
					long tempCheckImageStop = Calendar.getInstance()
							.getTimeInMillis();
					checkImage += tempCheckImageStop - tempCheckImageStart;
					if (!imageURL.equals("") && !imageURL.isEmpty()) {
						if (solution.contains("titImage")) {
							String titImage = solution.getLiteral("titImage")
									.getLexicalForm();
							image = new WebNMasUnoImage(uriImage,
									getNewPname(pnameImage), imageURL, titImage);
							imagenes++;
						} else {
							image = new WebNMasUnoImage(uriImage,
									getNewPname(pnameImage), imageURL);
							imagenes++;
						}
					}
				}
				if (image != null) {
					g.addImage(image);
				}
			}
		}
		long end = Calendar.getInstance().getTimeInMillis();
		int recursosSinImagenes = 0;
		int imagenesDeMas = 0;
		for (WebNMasUnoGuide guide : guides.values()) {
			if (guide.getImages().isEmpty()) {
				recursosSinImagenes++;
			} else if (guide.getImages().size() > 1) {
				imagenesDeMas += guide.getImages().size() - 1;
			}
		}
		/*
		 * System.out.println(
		 * "-----------------------------------------------------");
		 * System.out.println("Punto:"+uri);
		 * System.out.println("Recursos obtenidos:"+guides.values().size());
		 * System.out.println("Imagenes obtenidas:"+imagenes);
		 * System.out.println("Recursos sin imagenes:"+recursosSinImagenes);
		 * System.out.println("Imagenes de mas en recursos:"+imagenesDeMas);
		 * System.out.println("Tiempo ejecutar select:"+(medium1-start));
		 * System.
		 * out.println("Tiempo en conseguir todos los datos:"+(end-medium1));
		 * System.out.println("Tiempo en checkear URIs:"+checkURI);
		 * System.out.println("Tiempo en obtener las imagenes:"+checkImage);
		 * System.out.println("Tiempo total:"+(end-start)); System.out.println(
		 * "-----------------------------------------------------");
		 */
		return new ArrayList<WebNMasUnoGuide>(guides.values());
	}

	private List<WebNMasUnoTrip> getTrips(int i, String uri) {
		// viaje
		QueryExecution exec = QueryExecutionFactory.sparqlService(endpointUri,
				createGetTripsQuery(i, uri));
		Map<String, WebNMasUnoTrip> tripsMaps = new HashMap<String, WebNMasUnoTrip>();
		ResultSet queryResult = exec.execSelect();
		while (queryResult.hasNext()) {
			QuerySolution solution = queryResult.next();
			String uriTrip = "", titTrip = "", tripURL = "", dateViaje = "";
			if (solution.contains("trip")) {
				uriTrip = solution.getResource("trip").getURI();
			} else {
				uriTrip = "";
			}
			if (solution.contains("tripTitle")) {
				titTrip = solution.getLiteral("tripTitle").getLexicalForm();
			} else {
				titTrip = "";
			}
			if (solution.contains("tripURL")) {
				tripURL = solution.getLiteral("tripURL").getLexicalForm();
			} else {
				tripURL = "";
			}
			if (solution.contains("created")) {
				dateViaje = solution.getLiteral("created").getLexicalForm();
			} else {
				dateViaje = "";
			}
			if (!uriTrip.equals("")) {
				WebNMasUnoTrip t = new WebNMasUnoTrip(titTrip, tripURL,
						uriTrip, uri, dateViaje);
				addOtherTripsVariables(t, solution);
				if (!tripsMaps.containsKey(uriTrip)) {
					tripsMaps.put(uriTrip, t);
					addProvenanceTrip(uriTrip, t);
				}
			}
		}
		return new ArrayList<WebNMasUnoTrip>(tripsMaps.values());
	}

	private void addOtherTripsVariables(WebNMasUnoTrip trip,
			QuerySolution solution) {
		// ADD ?pL ?pH ?dL ?dH ?tD ?prL ?prH
		// pL = price less than
		// pH = price more than
		// dL = duration less than
		// dH = duration more than
		// tD = description
		// prL = distance less km than
		// prh = dsitance more km than
		if (solution.contains("pL")) {
			trip.setPriceLess((solution.getLiteral("pL").getLexicalForm()));
		}
		if (solution.contains("pH")) {
			trip.setPriceMore((solution.getLiteral("pH").getLexicalForm()));
		}
		if (solution.contains("dL")) {
			trip.setDurationLess((solution.getLiteral("dL").getLexicalForm()));
		}
		if (solution.contains("dH")) {
			trip.setDurationMore((solution.getLiteral("dH").getLexicalForm()));
		}
		if (solution.contains("tD")) {
			trip.setDescription((solution.getLiteral("tD").getLexicalForm()));
		}
		if (solution.contains("prL")) {
			trip.setDistanceLess((solution.getLiteral("prL").getLexicalForm()));
		}
		if (solution.contains("prH")) {
			trip.setDistanceMore((solution.getLiteral("prH").getLexicalForm()));
		}
	}
	private void addProvenanceTrip(String uriTrip, WebNMasUnoTrip trip){
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetTripProvenance(1000, uriTrip));
		ResultSet queryResult2 = exec2.execSelect();
		String nextV = "";
		while (queryResult2.hasNext()) {
			QuerySolution solution2 = queryResult2.next();
			if (solution2.contains("nextV")) {
				nextV = solution2.getResource("nextV").getURI();
			}
			TripProvenance provenance = createTripProvenance(solution2,trip.getDate());
			if(provenance!=null){
				trip.addTripProvenance(provenance);
			}
		}
		int maxProvenances=100;
		while (!nextV.trim().isEmpty() && maxProvenances>0) {
			exec2 = QueryExecutionFactory.sparqlService(endpointUri, createGetTripProvenance(1000, nextV));
			maxProvenances--;
			queryResult2 = exec2.execSelect();
			nextV = "";
			while (queryResult2.hasNext()) {
				QuerySolution solution2 = queryResult2.next();
				if (solution2.contains("nextV")) {
					nextV = solution2.getResource("nextV").getURI();
				}
				TripProvenance provenance = createTripProvenance(solution2,trip.getDate());
				if(provenance!=null){
					trip.addTripProvenance(provenance);
				}
			}
		}
	}

	private TripProvenance createTripProvenance(QuerySolution qs,String refTime) {
		String reference = "", time = "";
		reference = qs.getResource("reference").getURI();
		if (qs.contains("time")) {
			time = qs.getLiteral("time").getLexicalForm();
		} else {
			time = refTime;
		}
		TripProvenanceType provenanceType=TripProvenanceType.GUIDE;
		if (reference.contains("/Post/")) {
			provenanceType=TripProvenanceType.POST;
		} else if (reference.contains("/Image/")){
			provenanceType=TripProvenanceType.IMAGE;
		} else if (reference.contains("/Video/")) {
			provenanceType=TripProvenanceType.VIDEO;
		}else if (reference.contains("/Guide/")) {
			provenanceType=TripProvenanceType.GUIDE;
		}else{
			return null;
		}
		TripProvenance provenance= new TripProvenance(reference,provenanceType);
		provenance.setTime(time);
		if (qs.contains("pname")) {
			provenance.setUrl(qs.getLiteral("pname").getLexicalForm());
		}
		if(qs.contains("tit")){
			provenance.setTitle(qs.getLiteral("tit").getLexicalForm());
		}
		if(qs.contains("sub")){
			provenance.setSubTitle(qs.getLiteral("sub").getLexicalForm());
		}
		if(qs.contains("lt")){
			provenance.setTitle(qs.getLiteral("lt").getLexicalForm());
		}
		if(qs.contains("la")){
			provenance.setSubTitle(qs.getLiteral("la").getLexicalForm());
		}
		if(qs.contains("blog")){
			provenance.setBlog(qs.getResource("blog").getURI());
		}
		return provenance;
	}

	private String createGetGuidesQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder("");
		query.append("SELECT distinct ?noticia ?title ?url ?dateG ?uriImage ?pnameImage ?titImage WHERE {");
		query.append("?noticia <http://www.w3.org/2003/01/geo/wgs84_pos#location> <"
				+ uri + ">.");
		query.append("?noticia a <http://webenemasuno.linkeddata.es/ontology/OPMO/Guide>.");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#title> ?title . }");
		query.append("OPTIONAL {?noticia <http://rdfs.org/sioc/ns#created_at> ?dateG . }");
		query.append("OPTIONAL {?noticia <http://openprovenance.org/model/opmo#pname> ?url . }");
		query.append("OPTIONAL {?gen <http://openprovenance.org/model/opmo#effect> ?noticia.");
		query.append("?gen <http://openprovenance.org/model/opmo#cause> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#effect> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#cause> ?uriImage.");
		query.append("?uriImage a <http://webenemasuno.linkeddata.es/ontology/OPMO/Image>.");
		query.append("?uriImage <http://openprovenance.org/model/opmo#pname> ?pnameImage.}");
		query.append("OPTIONAL{?uriImage <http://metadata.net/mpeg7/mpeg7.owl#title> ?tittleImageUri.");
		query.append("?tittleImageUri <http://www.w3.org/2000/01/rdf-schema#label> ?titImage.}");
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}

	private String createGetTripsQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?trip ?tripTitle ?tripURL ?created ?pL ?pH ?dL ?dH ?tD ?prL ?prH WHERE{ ");
		query.append("?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> <"+uri+">.");
		// URL
		query.append("OPTIONAL{?trip <http://openprovenance.org/model/opmo#pname> ?tripURL. }");
		// title
		query.append("OPTIONAL{?trip <http://purl.org/dc/terms/title> ?tripTitle. }");
		// created
		query.append("?trip <http://rdfs.org/sioc/ns#created_at> ?created.");
		// price
		query.append("OPTIONAL{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPrice> ?p}.");
		query.append("OPTIONAL{?p <http://webenemasuno.linkeddata.es/ontology/OPMO/lessEurosThan> ?pL}.");
		query.append("OPTIONAL{?p <http://webenemasuno.linkeddata.es/ontology/OPMO/moreEurosThan> ?pH }.");
		// duration
		query.append("OPTIONAL{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasDuration> ?d}.");
		query.append("OPTIONAL{?d <http://webenemasuno.linkeddata.es/ontology/OPMO/lessWeeksThan> ?dL}.");
		query.append("OPTIONAL{?d <http://webenemasuno.linkeddata.es/ontology/OPMO/moreWeeksThan> ?dH }.");
		// description (if any)
		query.append("OPTIONAL{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/tripDescription> ?tD}.");
		// distance
		query.append("OPTIONAL{?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasDistance> ?pr}.");
		query.append("OPTIONAL{?pr <http://webenemasuno.linkeddata.es/ontology/OPMO/lessKmThan> ?prL}.");
		query.append("OPTIONAL{?pr <http://webenemasuno.linkeddata.es/ontology/OPMO/moreKmThan> ?prH }.");
		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private String createGetTripProvenance(Integer limit, String uri) {
		
		StringBuilder query = new StringBuilder(
				"SELECT distinct ?reference ?time ?pname ?tit ?sub ?lt ?la ?blog ?nextV WHERE{ ");
		query.append("?gen <http://openprovenance.org/model/opmo#effect> <"
				+ uri + ">.");
		query.append("?gen <http://openprovenance.org/model/opmo#cause> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#effect> ?process.");
		query.append("?used <http://openprovenance.org/model/opmo#cause> ?reference.");
		// date para el timeline
		query.append("OPTIONAL{?used <http://openprovenance.org/model/opmo#time> ?t.");
		query.append("?t <http://openprovenance.org/model/opmo#exactlyAt> ?time.}");
		// pname es comun
		query.append("OPTIONAL{?reference <http://openprovenance.org/model/opmo#pname> ?pname}");
		// GUIDE: title, description, rights, creator, RDF
		query.append("OPTIONAL{?reference <http://rdfs.org/sioc/ns#title> ?tit}");
		query.append("OPTIONAL{?reference <http://webenemasuno.linkeddata.es/ontology/OPMO/subtitle> ?sub}");
		// IMAGE-VIDEO: title, description, rights, creator, RDF
		query.append("OPTIONAL{?reference <http://metadata.net/mpeg7/mpeg7.owl#title> ?tI."
				+ "?tI <http://www.w3.org/2000/01/rdf-schema#label> ?lt}.");
		query.append("OPTIONAL{?reference <http://metadata.net/mpeg7/mpeg7.owl#abstract> ?a."
				+ "?a <http://www.w3.org/2000/01/rdf-schema#label> ?la}.");
		// POST: blog link
		query.append("OPTIONAL{?reference <http://rdfs.org/sioc/ns#has_container> ?blog}.");
		// title ya viene de guide
		// next Version
		query.append("OPTIONAL{?b <http://openprovenance.org/model/opmo#cause> <"
				+ uri + ">.");
		query.append("?b a <http://webenemasuno.linkeddata.es/ontology/OPMO/LaterVersionThan>.");
		query.append("?b <http://openprovenance.org/model/opmo#effect> ?nextV }");

		query.append("}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	

	private String getImageURL(String URL, int prevImages) {
		String convertedURL = getRedirectedURL(URL);
		convertedURL = convertedURL.replace("elviajero.elpais.com",
				"ep01.epimg.net");
		convertedURL = convertedURL.replace("diario", "diario/imagenes");
		convertedURL = convertedURL.replace(".html",
				"_" + String.format("%010d", prevImages)
						+ "_sumario_normal.jpg");
		if (!checkIfURLExists(convertedURL)) {
			return "";
		} else {
			return convertedURL;
		}
	}

	private String getNewPname(String URL) {
		return getRedirectedURL(URL);
	}

	private String getRedirectedURL(String URL) {
		String toReturn = "";
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(URL).openConnection();
			con.connect();
			con.setInstanceFollowRedirects(false);
			int responseCode = con.getResponseCode();
			if ((responseCode / 100) == 3) {
				String newLocationHeader = con.getHeaderField("Location");
				responseCode = con.getResponseCode();
				if (isRelativeURL(newLocationHeader)) {
					String urlFull = con.getURL().toString();
					urlFull = urlFull.replaceAll(con.getURL().getPath(),
							newLocationHeader);
					toReturn = urlFull;
				} else {
					URL urlLocation = new URL(newLocationHeader);
					String urlFull = urlLocation.toString();
					toReturn = urlFull;
				}
			}
			if ((responseCode / 100) == 2) {
				toReturn = URL;
			}
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			// Nothing
			logger.error(e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return toReturn;
	}

	private boolean isRelativeURL(String newLocationHeader) {
		try {
			URL urlLocation = new URL(newLocationHeader);
			if (urlLocation.getHost() == null
					|| urlLocation.getHost().isEmpty()
					|| urlLocation.getHost().equals("")) {
				return true;
			}
			return false;
		} catch (MalformedURLException e) {
		} catch (Exception e) {
		}
		return true;
	}

	private boolean checkIfURLExists(String URL) {
		boolean toReturn = false;
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(URL).openConnection();
			con.connect();
			con.setInstanceFollowRedirects(false);
			int responseCode = con.getResponseCode();
			if ((responseCode / 100) == 2) {
				toReturn = true;
			}
			if ((responseCode / 100) == 3) {
				String newURL = getRedirectedURL(URL);
				if (newURL != null && URL != null && URL.equals(newURL)) {
					return true;
				}
				if (newURL != null && !newURL.equals("") && !newURL.isEmpty()) {
					return checkIfURLExists(newURL);
				}
			}
		} catch (MalformedURLException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} catch (Exception e) {
			// Nothing to do
			logger.error(e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
		return toReturn;
	}
}
