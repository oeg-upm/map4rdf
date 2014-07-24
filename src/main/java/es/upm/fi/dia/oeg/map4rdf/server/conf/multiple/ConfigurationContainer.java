package es.upm.fi.dia.oeg.map4rdf.server.conf.multiple;

import org.apache.log4j.Logger;

import es.upm.fi.dia.oeg.map4rdf.server.conf.AddInfoConfigServer;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.FacetedBrowserConfiguration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.GetServletContext;
import es.upm.fi.dia.oeg.map4rdf.server.conf.MapsConfigurationServer;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.AemetDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.DbPediaDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.GeoLinkedDataDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.GeoSparqlDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.VCardDaoImpl;
import es.upm.fi.dia.oeg.map4rdf.server.dao.impl.WebNMasUnoImpl;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

public class ConfigurationContainer {
	
	private AddInfoConfigServer addInfoConfigServer;
	private Configuration configuration;
	private Map4rdfDao map4rdfDao;
	private FacetedBrowserConfiguration facetedBrowserConfiguration;
	private MapsConfigurationServer mapsConfigurationServer;
	private static Logger logger = Logger.getLogger(ConfigurationContainer.class);
	
	public ConfigurationContainer(GetServletContext getServletContext,String configFile){
		try {
			this.configuration=new Configuration(getServletContext.getServletContext().getResourceAsStream(Constants.CONFIGURATIONS_FOLDER+configFile));
			String addInfo = configuration.getConfigurationParamValue(ParameterNames.ADDITIONAL_INFO);
			String endpointURL = configuration.getConfigurationParamValue(ParameterNames.ENDPOINT_URL);
			String sphericalMercator = configuration.getConfigurationParamValue(ParameterNames.SPHERICAL_MERCATOR);
			String defaultProjection = configuration.getConfigurationParamValue(ParameterNames.DEFAULT_PROJECTION);
			String modelString = configuration.getConfigurationParamValue(ParameterNames.GEOMETRY_MODEL);
			String facetsFile = configuration.getConfigurationParamValue(ParameterNames.FACETS_FILE);
			String geoSparqlEndpointURL = configuration.getConfigurationParamValue(ParameterNames.ENDPOINT_URL_GEOSPARQL);
			Constants.GeometryModel model = Constants.GeometryModel.valueOf(modelString);
			if(addInfo!=null && !addInfo.isEmpty()){
				this.addInfoConfigServer=new AddInfoConfigServer(getServletContext, addInfo);
			}else{
				this.addInfoConfigServer=new AddInfoConfigServer();
			}
			this.map4rdfDao=getMap4rdfDao(model, endpointURL,geoSparqlEndpointURL, defaultProjection);
			facetedBrowserConfiguration = new FacetedBrowserConfiguration(getServletContext.getServletContext().getResourceAsStream(Constants.FACETS_FOLDER+facetsFile));
			mapsConfigurationServer = new MapsConfigurationServer(getServletContext, sphericalMercator, configuration,Constants.CONFIGURATIONS_FOLDER+configFile);
		} catch (Exception e) {
			logger.error("Can't create Configuration of file: "+configFile,e);
		}
	}
	
	public String getConfigurationParamValue(String parameter){
		return configuration.getConfigurationParamValue(parameter);
	}
	
	public AddInfoConfigServer getAddInfoConfigServer() {
		return addInfoConfigServer;
	}

	public Map4rdfDao getMap4rdfDao() {
		return map4rdfDao;
	}

	public FacetedBrowserConfiguration getFacetedBrowserConfiguration() {
		return facetedBrowserConfiguration;
	}

	public MapsConfigurationServer getMapsConfigurationServer() {
		return mapsConfigurationServer;
	}

	private Map4rdfDao getMap4rdfDao(Constants.GeometryModel model,String endpointUri,String geosparqlEndpointUri,String defaultProjection){
		switch (model) {
		case OEG:
			return new GeoLinkedDataDaoImpl(endpointUri,defaultProjection);
		case DBPEDIA:
			return new DbPediaDaoImpl(endpointUri,defaultProjection);
		case VCARD:
			return new VCardDaoImpl(endpointUri,defaultProjection);
		case GEOSPARQL:
			return new GeoSparqlDaoImpl(endpointUri,geosparqlEndpointUri,defaultProjection);
		case AEMET:
			return new AemetDaoImpl(endpointUri,defaultProjection);
		case WEBNMASUNO:
			return new WebNMasUnoImpl(endpointUri,defaultProjection);
		default:
			// make compiler happy
			return null;
		}
	}
}
