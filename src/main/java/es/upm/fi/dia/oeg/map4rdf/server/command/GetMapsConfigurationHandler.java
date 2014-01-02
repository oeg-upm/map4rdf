package es.upm.fi.dia.oeg.map4rdf.server.command;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfigurationResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.MapsConfigurationServer;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetMapsConfigurationHandler implements ActionHandler<GetMapsConfiguration, GetMapsConfigurationResult>{
	private MapsConfigurationServer mapsConfigurationServer;
	@Inject
	public GetMapsConfigurationHandler(MapsConfigurationServer mapsConfigurationServer) {
		this.mapsConfigurationServer=mapsConfigurationServer;
	}
	
	@Override
	public Class<GetMapsConfiguration> getActionType() {
		return GetMapsConfiguration.class;
	}

	@Override
	public GetMapsConfigurationResult execute(GetMapsConfiguration action,
			ExecutionContext context) throws DispatchException {
		return new GetMapsConfigurationResult(mapsConfigurationServer.getMapsConfiguration());
	}

	@Override
	public void rollback(GetMapsConfiguration action,
			GetMapsConfigurationResult result, ExecutionContext context)
			throws DispatchException {
		
	}

}
