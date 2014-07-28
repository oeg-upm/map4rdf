package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAllConfigurationsDescription;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAllConfigurationsDescriptionResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.ConfigurationDescription;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetAllConfigurationsDescriptionsHandler implements ActionHandler<GetAllConfigurationsDescription,GetAllConfigurationsDescriptionResult>{
	
	private MultipleConfigurations configurations;
	private Logger logger = Logger.getLogger(GetAllConfigurationsDescriptionsHandler.class);
			
	@Inject
	public GetAllConfigurationsDescriptionsHandler(MultipleConfigurations configurations){
		this.configurations=configurations;
	}
	
	@Override
	public Class<GetAllConfigurationsDescription> getActionType() {
		return GetAllConfigurationsDescription.class;
	}

	@Override
	public GetAllConfigurationsDescriptionResult execute(
			GetAllConfigurationsDescription action, ExecutionContext context)
			throws DispatchException {
		List<ConfigurationDescription> toReturn = new ArrayList<ConfigurationDescription>();
		for(String id: configurations.getConfigurationIDs()){
			ConfigurationDescription toAdd = new ConfigurationDescription(id);
			addDescriptions(toAdd);	
			addLabels(toAdd);
			addImage(toAdd);
			toReturn.add(toAdd);
		}
		return new GetAllConfigurationsDescriptionResult(toReturn);
	}
	
	private void addImage(ConfigurationDescription configDescription) {
		if(configurations.getConfiguration(configDescription.getId()).containsConfigurationParam(ParameterNames.CONFIGURATION_IMAGE)){
			String image = configurations.getConfiguration(configDescription.getId()).getConfigurationParamValue(ParameterNames.CONFIGURATION_IMAGE);
			configDescription.setImage(image);
		}
	}

	private void addLabels(ConfigurationDescription configDescription) {
		if(configurations.getConfiguration(configDescription.getId()).containsConfigurationParam(ParameterNames.CONFIGURATION_LABELS)){
			String totalDescription = configurations.getConfiguration(configDescription.getId()).getConfigurationParamValue(ParameterNames.CONFIGURATION_LABELS);
			String [] splitDescriptions = totalDescription.split(";");
			if(splitDescriptions.length > 1){
				for(String eachDescription:splitDescriptions){
					String[] splitDescription = eachDescription.split("@");
					if(splitDescription.length == 2){
						configDescription.addLabel(splitDescription[0], splitDescription[1]);
					}else if(splitDescription.length == 1){
						configDescription.addLabel("", eachDescription);
					}else{
						logger.warn("Bad Label: \""+eachDescription+"\" in config id: "+configDescription.getId());
					}
				}
			}else{
				configDescription.addLabel("", totalDescription);
			}
		}
	}

	private void addDescriptions(ConfigurationDescription configDescription){
		if(configurations.getConfiguration(configDescription.getId()).containsConfigurationParam(ParameterNames.CONFIGURATION_DESCRIPTION)){
			String totalDescription = configurations.getConfiguration(configDescription.getId()).getConfigurationParamValue(ParameterNames.CONFIGURATION_DESCRIPTION);
			String [] splitDescriptions = totalDescription.split(";");
			if(splitDescriptions.length > 1){
				for(String eachDescription:splitDescriptions){
					String[] splitDescription = eachDescription.split("@");
					if(splitDescription.length == 2){
						configDescription.addDescription(splitDescription[0], splitDescription[1]);
					}else if(splitDescription.length == 1){
						configDescription.addDescription("", eachDescription);
					}else{
						logger.warn("Bad description: \""+eachDescription+"\" in config id: "+configDescription.getId());
					}
				}
			}else{
				configDescription.addDescription("", totalDescription);
			}
		}
	}
	@Override
	public void rollback(GetAllConfigurationsDescription action,
			GetAllConfigurationsDescriptionResult result,
			ExecutionContext context) throws DispatchException {
		// Nothing to do
		
	}

}
