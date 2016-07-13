package es.upm.fi.dia.oeg.map4rdf.server.command;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfigurationResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetMapsConfigurationHandler implements ActionHandler<GetMapsConfiguration, GetMapsConfigurationResult>{
	private MultipleConfigurations configurations;
	@Inject
	public GetMapsConfigurationHandler(MultipleConfigurations configurations) {
		this.configurations=configurations;
	}
	
	@Override
	public Class<GetMapsConfiguration> getActionType() {
		return GetMapsConfiguration.class;
	}

	@Override
	public GetMapsConfigurationResult execute(GetMapsConfiguration action,
			ExecutionContext context) throws DispatchException {
		if(!configurations.existsConfiguration(action.getConfigID())){
			throw new ActionException("Bad Config ID");
		}
		return new GetMapsConfigurationResult(configurations.getConfiguration(action.getConfigID()).getMapsConfigurationServer().getMapsConfiguration());
	}

	@Override
	public void rollback(GetMapsConfiguration action,
			GetMapsConfigurationResult result, ExecutionContext context)
			throws DispatchException {
		
	}

}
