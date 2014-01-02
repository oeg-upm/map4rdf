package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAddInfoConfig;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAddInfoConfigResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.AdditionalInfo;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.QueryParameterResult;

public class AdditionalInfoExecuter {
	private static GetAddInfoConfigResult addInfoConfig;
	private static Map<Integer,Boolean> doCallbacks=new HashMap<Integer, Boolean>();
	private static int id=0;
	public interface InfoCallback{
		void success(AdditionalInfoSummary additionalInfo);
	}
	private static class AdditionalInfoReturn{
		private Map<String,String> transformedValues=new HashMap<String, String>();
		private Map<String,String> originalValues=new HashMap<String, String>();
		public Map<String, String> getTransformedValues() {
			return transformedValues;
		}
		public Map<String, String> getOriginalValues() {
			return originalValues;
		}
		
	}
	public static void getAdditionalInfo(final DispatchAsync dispatchAsync,final GeoResource resource,final InfoCallback callback){
		addInfoConfig=null;
		final int myId=id++;
		doCallbacks.put(myId, true);
		if(id==100){
			id=0;
		}
		dispatchAsync.execute(new GetAddInfoConfig(resource), new AsyncCallback<SingletonResult<GetAddInfoConfigResult>>() {
			@Override
			public void onFailure(Throwable caught) {
				Map<String,String> toReturn = new HashMap<String, String>();
				AdditionalInfoSummary additionalInfo= new AdditionalInfoSummary(toReturn);
				if(doCallbacks.containsKey(myId) && doCallbacks.get(myId)){
					callback.success(additionalInfo);
				}
				doCallbacks.remove(myId);	
			}
			@Override
			public void onSuccess(SingletonResult<GetAddInfoConfigResult> result) {
				addInfoConfig=result.getValue();
				//Map<String,String> toReturn = new HashMap<String, String>();
				AdditionalInfoReturn toReturn=new AdditionalInfoReturn();
				for(AdditionalInfo addiInfo:addInfoConfig.getAdditionalsInfo()){
					/*toReturn.put("Endpoint:",addiInfo.getEndpoint());
					toReturn.put("Query:", addiInfo.getQuery());
					toReturn.put("Parameters:", addiInfo.getQueryParametersResults().toString());
					toReturn.put("Result", addiInfo.getResult());*/
					toReturn=analizeJSON(addiInfo, toReturn);
				}
				if(doCallbacks.containsKey(myId) && doCallbacks.get(myId)){
					AdditionalInfoSummary additionalInfo= new AdditionalInfoSummary(toReturn.getTransformedValues());
					for(AdditionalInfo addiInfo:addInfoConfig.getAdditionalsInfo()){
						if(addiInfo.isHas_image_limit()){
							if(toReturn.getOriginalValues().containsKey(addiInfo.getImage_parameter())){
								try{
									Double doLimit=Double.parseDouble(toReturn.getOriginalValues().get(addiInfo.getImage_parameter()));
									if(doLimit<addiInfo.getInferior_limit()){
										additionalInfo.setImage(addiInfo.getImages()[0]);
										break;
									}
									if(doLimit>addiInfo.getSuperior_limit()){
										additionalInfo.setImage(addiInfo.getImages()[2]);
										break;
									}
									if(doLimit>=addiInfo.getInferior_limit() && doLimit<=addiInfo.getSuperior_limit()){
										additionalInfo.setImage(addiInfo.getImages()[1]);
										break;
									}
								}catch(Exception e){
								}
							}
						}
					}
					callback.success(additionalInfo);
				}
				doCallbacks.remove(myId);
			}
		});
	}
	public static void cancelAllCallbacks(){
		for(int key:doCallbacks.keySet()){
			doCallbacks.put(key, false);
		}
	}
	public static AdditionalInfoReturn analizeJSON(AdditionalInfo info, AdditionalInfoReturn infoReturn){
		String datasValues= info.getResult();
		if(datasValues!=null && !datasValues.isEmpty()){
			JSONValue value= JSONParser.parseStrict(datasValues);
			if( value!=null && value.isObject()!=null 
					&& value.isObject().get("results")!=null 
					&& value.isObject().get("results").isObject()!=null 
					&& value.isObject().get("results").isObject().get("bindings")!=null
					&& value.isObject().get("results").isObject().get("bindings").isArray()!=null){
				JSONArray array= value.isObject().get("results").isObject().get("bindings").isArray();
				int addValues=0;
				for(int i=0;i<array.size();i++){
					if(array.get(i)!=null && array.get(i).isObject()!=null){
						JSONObject valor=array.get(i).isObject();
						for(QueryParameterResult res:info.getQueryParametersResults()){
							if(valor.get(res.getUri())!=null && valor.get(res.getUri()).isObject()!=null
									&& valor.get(res.getUri()).isObject().get("value")!=null
									&& valor.get(res.getUri()).isObject().get("value").isString()!=null){
								String anadir=valor.get(res.getUri()).isObject().get("value").isString().toString().replace("\"", "");
								
								//x= punto.get("valueX").isObject().get("value").isString().toString().replace("\"", "");
								if(!infoReturn.getOriginalValues().containsKey(res.getParameter())){
									infoReturn.getOriginalValues().put(res.getParameter(), anadir);
								}
								if(!infoReturn.getTransformedValues().containsKey(LocaleUtil.getBestLabel(res))){
									infoReturn.getTransformedValues().put(LocaleUtil.getBestLabel(res), anadir);
									addValues++;
								}
							}
						}
						if(addValues==info.getQueryParametersResults().size()){
							break;
						}
					}
				}
			}
		}
		return infoReturn;
		
	}
}
