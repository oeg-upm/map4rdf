package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public interface GeoResourceSummaryInfo{
	public boolean isVisible();
	public void show();
	public void close();
	public void addAdditionalInfo(Map<String,String> additionalsInfo);
	public Widget getWidget();
	public void setGeoResource(final GeoResource resource, Geometry geometry);
	public void clearAdditionalInfo();
}
