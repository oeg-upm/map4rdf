package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public interface GeoResourceSummaryInfo{
	public void addAdditionalInfo(Map<String,String> additionalsInfo,int extraRadiousPX);
	public Widget getWidget();
	public void setGeoResource(final GeoResource resource, Geometry geometry);
	public void clearAdditionalInfo();
}
