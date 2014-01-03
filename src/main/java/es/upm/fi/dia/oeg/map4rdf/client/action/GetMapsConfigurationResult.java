package es.upm.fi.dia.oeg.map4rdf.client.action;

import java.util.ArrayList;

import es.upm.fi.dia.oeg.map4rdf.share.MapConfiguration;

import net.customware.gwt.dispatch.shared.Result;

public class GetMapsConfigurationResult implements Result{
	private ArrayList<MapConfiguration> mapsConfiguration= new ArrayList<MapConfiguration>();
	GetMapsConfigurationResult() {
		//for serialization
	}
	public GetMapsConfigurationResult(
			ArrayList<MapConfiguration> mapsConfiguration) {
		this.mapsConfiguration.addAll(mapsConfiguration);
	}
	public ArrayList<MapConfiguration> getMapsConfiguration() {
		return mapsConfiguration;
	}
	public void setMapsConfiguration(ArrayList<MapConfiguration> mapsConfiguration) {
		this.mapsConfiguration.addAll(mapsConfiguration);
	}
	
}
