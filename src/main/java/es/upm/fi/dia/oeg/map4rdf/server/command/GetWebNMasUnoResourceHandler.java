package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoImage;
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
		Map<String,WebNMasUnoGuide> guides=new HashMap<String, WebNMasUnoGuide>();
		long start=Calendar.getInstance().getTimeInMillis();
		QueryExecution exec2 = QueryExecutionFactory.sparqlService(endpointUri,
				createGetGuidesTripsQuery(100, uri));
		ResultSet queryResult2 = exec2.execSelect();
		long medium1=Calendar.getInstance().getTimeInMillis();
		long checkURI=0;
		long checkImage=0;
		int imagenes=0;
		while (queryResult2.hasNext()) {
			// guia
			String uriGuide = "", urlGuide = "", titleGuide = "", dateGuia = "";
			// viaje
			String uriTrip = "", titTrip = "", idIt = "", tripURL = "", dateViaje = "";
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
			long tempCheckURIStart=Calendar.getInstance().getTimeInMillis();
			if (!uriGuide.equals("") && !uriGuide.isEmpty() 
					&& !urlGuide.equals("") && !urlGuide.isEmpty() && checkIfURLExists(urlGuide)) {
				long tempCheckURIStop=Calendar.getInstance().getTimeInMillis();
				checkURI+=tempCheckURIStop-tempCheckURIStart;
				WebNMasUnoGuide g;
				if(guides.containsKey(uriGuide) && guides.get(uriGuide)!=null){
					g=guides.get(uriGuide);
				}else{
					g = new WebNMasUnoGuide(titleGuide, urlGuide,
							uriGuide, dateGuia);
					guides.put(uriGuide, g);
				}
				//resource.addGuide(g);
				WebNMasUnoImage image=null;
				if(solution2.contains("uriImage") && solution2.contains("pnameImage")){
					String uriImage=solution2.getResource("uriImage").getURI();
					String pnameImage=solution2.getLiteral("pnameImage").getLexicalForm();
					long tempCheckImageStart=Calendar.getInstance().getTimeInMillis();
					String imageURL=getImageURL(urlGuide,g.getImages().size());
					long tempCheckImageStop=Calendar.getInstance().getTimeInMillis();
					checkImage+=tempCheckImageStop-tempCheckImageStart;
					if(!imageURL.equals("") && !imageURL.isEmpty()){
						if(solution2.contains("titImage")){
							String titImage=solution2.getLiteral("titImage").getLexicalForm();
							image= new WebNMasUnoImage(uriImage, getNewPname(pnameImage),imageURL, titImage);
							imagenes++;
						}else{
							image=new WebNMasUnoImage(uriImage,getNewPname(pnameImage),imageURL);
							imagenes++;
						}
					}
				}
				if(image!=null){
					g.addImage(image);
				}
			} else if (!uriTrip.equals("")) {
				WebNMasUnoTrip t = new WebNMasUnoTrip(titTrip, tripURL,
						uriTrip, idIt, dateViaje);
				resource.addTrip(t);
			}
		}
		resource.addAllGuides(guides.values());
		long end=Calendar.getInstance().getTimeInMillis();
		int recursosSinImagenes=0;
		int imagenesDeMas=0;
		for(WebNMasUnoGuide guide:guides.values()){
			if(guide.getImages().isEmpty()){
				recursosSinImagenes++;
			}else if(guide.getImages().size()>1){
				imagenesDeMas+=guide.getImages().size()-1;
			}
		}
		/*System.out.println("-----------------------------------------------------");
		System.out.println("Punto:"+uri);
		System.out.println("Recursos obtenidos:"+guides.values().size());
		System.out.println("Imagenes obtenidas:"+imagenes);
		System.out.println("Recursos sin imagenes:"+recursosSinImagenes);
		System.out.println("Imagenes de mas en recursos:"+imagenesDeMas);
		System.out.println("Tiempo ejecutar select:"+(medium1-start));
		System.out.println("Tiempo en conseguir todos los datos:"+(end-medium1));
		System.out.println("Tiempo en checkear URIs:"+checkURI);
		System.out.println("Tiempo en obtener las imagenes:"+checkImage);
		System.out.println("Tiempo total:"+(end-start));
		System.out.println("-----------------------------------------------------");*/
		return resource;
	
	}

	private String createGetGuidesTripsQuery(Integer limit, String uri) {
		StringBuilder query = new StringBuilder("");
		query.append("SELECT distinct ?noticia ?title ?url ?dateG ?uriImage ?pnameImage ?titImage ?trip ?tripTitle ?tripURL ?it ?dateV WHERE {");
		query.append("{?noticia <http://www.w3.org/2003/01/geo/wgs84_pos#location> <"+uri+">.");
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
		query.append("}UNION{");
		query.append("?trip <http://webenemasuno.linkeddata.es/ontology/OPMO/hasItinerary> ?it.");
		query.append("?it <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPart> ?part.");
		query.append("?part <http://webenemasuno.linkeddata.es/ontology/OPMO/hasPoint> <http://webenemasuno.linkeddata.es/elviajero/resource/Point/POINT41.65_-4.71666666666667>.");
		query.append("OPTIONAL{?trip <http://openprovenance.org/model/opmo#pname> ?tripURL. }");
		query.append("OPTIONAL {?trip <http://rdfs.org/sioc/ns#created_at> ?dateV . }");
		query.append("OPTIONAL{?trip <http://purl.org/dc/terms/title> ?tripTitle. }");
		query.append("}}");
		if (limit != null) {
			query.append(" LIMIT " + limit);
		}
		return query.toString();
	}
	private String getImageURL(String URL,int prevImages){
		String convertedURL=getRedirectedURL(URL);
    	convertedURL=convertedURL.replace("elviajero.elpais.com","ep01.epimg.net");
    	convertedURL=convertedURL.replace("diario", "diario/imagenes");
    	convertedURL=convertedURL.replace(".html", "_"+String.format("%010d", prevImages)+"_sumario_normal.jpg");
    	if(!checkIfURLExists(convertedURL)){
    		return "";
    	}else{
    		return convertedURL;
    	}
	}
	private String getNewPname(String URL){
		return getRedirectedURL(URL);
	}
	private String getRedirectedURL(String URL){
		String toReturn="";
		HttpURLConnection con=null;
		try {
			con = (HttpURLConnection) new URL(
			        URL).openConnection();
		    con.connect();
		    con.setInstanceFollowRedirects(false);
		    int responseCode = con.getResponseCode();
		    if ((responseCode / 100) == 3) {
		        String newLocationHeader = con.getHeaderField("Location");
		        responseCode = con.getResponseCode();
		        if(isRelativeURL(newLocationHeader)){
		        	String urlFull=con.getURL().toString();
		        	urlFull=urlFull.replaceAll(con.getURL().getPath(), newLocationHeader);
		        	toReturn=urlFull;
		        }else{
		        	URL urlLocation = new URL(newLocationHeader);
		        	String urlFull=urlLocation.toString();
		        	toReturn=urlFull;
		        }
		    }
		    if((responseCode/100) == 2 ){
		    	toReturn=URL;
		    }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			//Nothing
			e.printStackTrace();
		}finally{
			if(con!=null){con.disconnect();}
		}
		return toReturn;
	}
	private boolean isRelativeURL(String newLocationHeader) {
		try {
			URL urlLocation = new URL(newLocationHeader);
			if(urlLocation.getHost()==null 
					|| urlLocation.getHost().isEmpty() 
					|| urlLocation.getHost().equals("")){
				return true;
			}
			return false;
		} catch (MalformedURLException e) {
		} catch (Exception e) {
		}
		return true;
	}

	private boolean checkIfURLExists(String URL){
		boolean toReturn=false;
		HttpURLConnection con=null;
		try {
			 con = (HttpURLConnection) new URL(
			        URL).openConnection();
		    con.connect();
		    con.setInstanceFollowRedirects(false);
		    int responseCode = con.getResponseCode();
		    if ((responseCode / 100) == 2) {
		        toReturn=true;
		    }
		    if((responseCode/100) ==3){
		    	String newURL=getRedirectedURL(URL);
		    	if(newURL!=null && URL!=null && URL.equals(newURL)){
		    		return true;
		    	}
		    	if(newURL!=null && !newURL.equals("") && !newURL.isEmpty()){
		    		return checkIfURLExists(newURL);
		    	}
		    }
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			//Nothing to do
			e.printStackTrace();
		} finally {
			if(con!=null){con.disconnect();}
		}
		return toReturn;
	}
}
