package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAddInfoConfigResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAddInfoConfig;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.AddInfoConfigServer;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParametersNamesAddInfo;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.AdditionalInfo;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

public class GetAddInfoConfigHandler implements ActionHandler<GetAddInfoConfig, SingletonResult<GetAddInfoConfigResult>>{
	private AddInfoConfigServer addInfoConfigServer;
	@Inject
	public GetAddInfoConfigHandler(AddInfoConfigServer addInfoConfigServer){
		this.addInfoConfigServer=addInfoConfigServer;
	}
	@Override
	public Class<GetAddInfoConfig> getActionType() {
		return GetAddInfoConfig.class;
	}

	@Override
	public SingletonResult<GetAddInfoConfigResult> execute(GetAddInfoConfig action,
			ExecutionContext context) throws DispatchException {
		List<AdditionalInfo> toReturn= new ArrayList<AdditionalInfo>();
		for(AdditionalInfo info:addInfoConfigServer.getAdditionalsInfo()){
			try {
				URL u = new URL(info.getEndpoint()+parseQuery(info.getQuery(),info.getInputParameters(), action.getResource()));
				URLConnection conn = u.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
				String inputLine;
				StringBuffer result=new StringBuffer();
				while ((inputLine = in.readLine()) != null){
					result.append(inputLine+"\n");
				}
				AdditionalInfo newInfo=AdditionalInfo.newInstance();
				newInfo.setEndpoint(info.getEndpoint());
				newInfo.setQuery(info.getQuery());
				newInfo.setQueryParametersResults(info.getQueryParametersResults());
				newInfo.setResult(result.toString());
				newInfo.setHas_image_limit(info.isHas_image_limit());
				newInfo.setImage_parameter(info.getImage_parameter());
				newInfo.setInferior_limit(info.getInferior_limit());
				newInfo.setSuperior_limit(info.getSuperior_limit());
				newInfo.setImages(info.getImages());
				toReturn.add(newInfo);
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			}
		}
		return new SingletonResult<GetAddInfoConfigResult>(new GetAddInfoConfigResult(toReturn));
	}
	private String parseQuery(String query,String input,GeoResource resource){
		if(query.contains(ParametersNamesAddInfo.RESOURCE) && input.contains(ParametersNamesAddInfo.RESOURCE)){
			query=query.replace(ParametersNamesAddInfo.RESOURCE, resource.getUri());
		}
		try {
			query=URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return query;
	}
	@Override
	public void rollback(GetAddInfoConfig action,
			SingletonResult<GetAddInfoConfigResult> result, ExecutionContext context)
			throws DispatchException {
		
	}

}
