package es.upm.fi.dia.oeg.map4rdf.share.conf.util;

import java.io.Serializable;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;

public class QueryParameterResult extends Resource implements Serializable{
	
	private static final long serialVersionUID = 3663987108523708114L;

	String parameter;
	QueryParameterResult() {
		super("");
		this.parameter="";
	}
	
	public QueryParameterResult(String parameter) {
		super(parameter);
		this.parameter=parameter;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	@Override
	public String toString(){
		String toReturn=parameter;
		for(String lang:super.getLangs()){
			toReturn+=" "+super.getLabel(lang)+"@"+lang;
		}
		return toReturn;
	}
	
}
