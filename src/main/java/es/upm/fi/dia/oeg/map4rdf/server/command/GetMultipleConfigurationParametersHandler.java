package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.util.HashMap;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParameters;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParametersResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetMultipleConfigurationParametersHandler implements ActionHandler<GetMultipleConfigurationParameters, GetMultipleConfigurationParametersResult>{

	private MultipleConfigurations configurations;
	
	@Override
	public Class<GetMultipleConfigurationParameters> getActionType() {
		return GetMultipleConfigurationParameters.class;
	}
	
	@Inject
	public GetMultipleConfigurationParametersHandler(MultipleConfigurations configurations) {
		super();
		this.configurations = configurations;
	}

	@Override
	public GetMultipleConfigurationParametersResult execute(GetMultipleConfigurationParameters action,
			ExecutionContext context) throws ActionException {
		if(!configurations.existsConfiguration(action.getConfigID())){
			throw new ActionException("Bad Config ID");
		}
		HashMap<String, String> result=new HashMap<String,String>();
		for(String param: action.getParameters()){
			String resultParam=configurations.getConfiguration(action.getConfigID()).getConfigurationParamValue(param);
			if(resultParam!=null){
				result.put(param, resultParam);
			}
		}
		return new GetMultipleConfigurationParametersResult(result);
	}




	@Override
	public void rollback(GetMultipleConfigurationParameters action,
			GetMultipleConfigurationParametersResult result,
			ExecutionContext context) throws DispatchException {	
	}
}
