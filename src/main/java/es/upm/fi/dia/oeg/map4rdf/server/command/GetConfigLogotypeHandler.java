package es.upm.fi.dia.oeg.map4rdf.server.command;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigLogotype;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigLogotypeResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.ParameterDefaults;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetConfigLogotypeHandler implements ActionHandler<GetConfigLogotype,GetConfigLogotypeResult> {
	
	private MultipleConfigurations configurations;
	
	@Inject
	public GetConfigLogotypeHandler(MultipleConfigurations configurations){
		this.configurations = configurations;
	}
	@Override
	public Class<GetConfigLogotype> getActionType() {
		return GetConfigLogotype.class;
	}

	@Override
	public GetConfigLogotypeResult execute(GetConfigLogotype action,
			ExecutionContext context) throws DispatchException {
		if(!configurations.existsConfiguration(action.getConfigID())){
			throw new ActionException("Bad Config ID");
		}
		String logo = Constants.LOGOS_FOLDER; 
		if(configurations.getConfiguration(action.getConfigID()).containsConfigurationParam(ParameterNames.LOGO_IMG_SRC)){
			logo += configurations.getConfiguration(action.getConfigID()).getConfigurationParamValue(ParameterNames.LOGO_IMG_SRC);
		}else{
			logo += ParameterDefaults.LOGO_IMG_SRC;
		}
		return new GetConfigLogotypeResult(logo);
	}

	@Override
	public void rollback(GetConfigLogotype action,
			GetConfigLogotypeResult result, ExecutionContext context)
			throws DispatchException {
		//Nothing to do
	}

}
