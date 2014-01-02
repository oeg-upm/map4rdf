package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.upm.fi.dia.oeg.map4rdf.share.conf.util.AdditionalInfo;

public class GetAddInfoConfigResult implements Serializable{
	private static final long serialVersionUID = -1754647571246175239L;
	private List<AdditionalInfo> additionalsInfo;
	GetAddInfoConfigResult(){
		additionalsInfo=new ArrayList<AdditionalInfo>();
	}
	public void setAdditionalsInfo(List<AdditionalInfo> additionalsInfo) {
		this.additionalsInfo = additionalsInfo;
	}
	public GetAddInfoConfigResult(List<AdditionalInfo> additionalsInfo){
		this.additionalsInfo=additionalsInfo;
	}
	public List<AdditionalInfo> getAdditionalsInfo() {
		return additionalsInfo;
	}
}
