package es.upm.fi.dia.oeg.map4rdf.share.conf.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.upm.fi.dia.oeg.map4rdf.share.conf.util.QueryParameterResult;

public class AdditionalInfo implements Serializable {
	private static final long serialVersionUID = -7815537906348788150L;
	private String endpoint;
	private String query;
	private String result;
	private String inputParameters;
	private boolean has_image_limit=false;
	private String image_parameter="";
	private double inferior_limit=0.0;
	private double superior_limit=0.0;
	private String[] images={"","",""};
	
	private List<QueryParameterResult> queryParametersResults;
	AdditionalInfo(){
		endpoint="";
		query="";
		result="";
		inputParameters="";
		queryParametersResults=new ArrayList<QueryParameterResult>();
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	public void addQueryResult(QueryParameterResult result){
		if(!queryParametersResults.contains(result)){
			queryParametersResults.add(result);
		}
	}
	public List<QueryParameterResult> getQueryParametersResults() {
		return queryParametersResults;
	}
	public void setQueryParametersResults(
			List<QueryParameterResult> queryParametersResults) {
		this.queryParametersResults = queryParametersResults;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getInputParameters() {
		return inputParameters;
	}
	public void setInputParameters(String inputParameters) {
		this.inputParameters = inputParameters;
	}
	public boolean isHas_image_limit() {
		return has_image_limit;
	}
	public void setHas_image_limit(boolean has_image_limit) {
		this.has_image_limit = has_image_limit;
	}
	public double getInferior_limit() {
		return inferior_limit;
	}
	public void setInferior_limit(double inferior_limit) {
		this.inferior_limit = inferior_limit;
	}
	public double getSuperior_limit() {
		return superior_limit;
	}
	public void setSuperior_limit(double superior_limit) {
		this.superior_limit = superior_limit;
	}
	public String[] getImages() {
		return images;
	}
	public void setImages(String[] images) {
		this.images = images;
	}
	
	public String getImage_parameter() {
		return image_parameter;
	}
	public void setImage_parameter(String image_parameter) {
		this.image_parameter = image_parameter;
	}
	public static AdditionalInfo newInstance() {
		return new AdditionalInfo();
	}

	
	
}
