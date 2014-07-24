package es.upm.fi.dia.oeg.map4rdf.server.conf.multiple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.GetServletContext;

public class MultipleConfigurations {
	
	private Configuration globalConfiguration;
	private Map<String,ConfigurationContainer> configurations;
	private Logger logger = Logger.getLogger(MultipleConfigurations.class);
	
	public MultipleConfigurations(GetServletContext getServletContext){
		try {
			this.globalConfiguration = new Configuration(getServletContext.getServletContext().getResourceAsStream(Constants.GLOBAL_CONFIGURATION));
			this.configurations = new HashMap<String, ConfigurationContainer>();
			for(String id : globalConfiguration.getKeys()){
				if(id!=null && !id.isEmpty()){
					String configFile = globalConfiguration.getConfigurationParamValue(id);
					try{
						ConfigurationContainer configuration = new ConfigurationContainer(getServletContext, configFile);
						configurations.put(id, configuration);
					}catch(Exception e){
						logger.fatal("Can't obtain config file: ID="+id+" FILE="+configFile,e);
					}
				}		
			}	
		} catch (Exception e) {
			logger.fatal("Can't config app",e);
		}
	}
	public ConfigurationContainer getConfiguration(String id){
		return configurations.get(id);
	}
	public Set<String> getConfigurationIDs(){
		return configurations.keySet();
	}
	public Map<String,ConfigurationContainer> getConfigurations(){
		return configurations;
	}
	public boolean existsConfiguration(String id){
		return configurations.containsKey(id) && configurations.get(id)!=null;
	}
	
}
