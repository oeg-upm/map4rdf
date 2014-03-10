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
	private boolean hasImageLimit=false;
	private String image_parameter="";
	private double inferiorLimit=0.0;
	private double superiorLimit=0.0;
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
	public boolean isHasImageLimit() {
		return hasImageLimit;
	}
	public void setHas_image_limit(boolean hasImageLimit) {
		this.hasImageLimit = hasImageLimit;
	}
	public double getInferiorLimit() {
		return inferiorLimit;
	}
	public void setInferiorLimit(double inferiorLimit) {
		this.inferiorLimit = inferiorLimit;
	}
	public double getSuperiorLimit() {
		return superiorLimit;
	}
	public void setSuperiorLimit(double superiorLimit) {
		this.superiorLimit = superiorLimit;
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
