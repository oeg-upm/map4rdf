package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.google.inject.Inject;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParameters;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParametersResult;
import es.upm.fi.dia.oeg.map4rdf.server.bootstrap.Bootstrapper;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration;
import es.upm.fi.dia.oeg.map4rdf.server.conf.Constants;
import es.upm.fi.dia.oeg.map4rdf.server.conf.GetServletContext;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetMultipleConfigurationParametersHandler implements ActionHandler<GetMultipleConfigurationParameters, GetMultipleConfigurationParametersResult>{

	private ServletContext servletContext;
	private Configuration config;
	
	@Override
	public Class<GetMultipleConfigurationParameters> getActionType() {
		return GetMultipleConfigurationParameters.class;
	}
	
	@Inject
	public GetMultipleConfigurationParametersHandler(GetServletContext getServletContext) {
		super();
		servletContext = getServletContext.getServletContext();
		InputStream propIn = servletContext.getResourceAsStream(Constants.CONFIGURATION_FILE);
        try {
            config = new Configuration(propIn);
        } catch (IOException ex) {
            Logger.getLogger(Bootstrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
		
	}

	@Override
	public GetMultipleConfigurationParametersResult execute(GetMultipleConfigurationParameters action,
			ExecutionContext context) throws ActionException {
		HashMap<String, String> result=new HashMap<String,String>();
		for(String param: action.getParameters()){
			String resultParam=config.getConfigurationParamValue(param);
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
