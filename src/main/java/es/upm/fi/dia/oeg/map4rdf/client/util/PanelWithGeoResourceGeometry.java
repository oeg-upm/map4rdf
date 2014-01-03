package es.upm.fi.dia.oeg.map4rdf.client.util;

import com.google.gwt.user.client.ui.Panel;

public class PanelWithGeoResourceGeometry{
	private Panel panel;
	private int row;
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	private GeoResourceGeometry geoResourceGeometry;
	public PanelWithGeoResourceGeometry(){}
	public PanelWithGeoResourceGeometry(GeoResourceGeometry geoResourceGeometry, Panel panel){
		this.geoResourceGeometry=geoResourceGeometry;
		this.panel=panel;
	}
	public PanelWithGeoResourceGeometry(GeoResourceGeometry geoResourceGeometry, int row){
		this.geoResourceGeometry=geoResourceGeometry;
		this.row=row;
	}
	public Panel getPanel() {
		return panel;
	}
	public void setPanel(Panel panel) {
		this.panel = panel;
	}
	public GeoResourceGeometry getGeoResourceGeometry() {
		return geoResourceGeometry;
	}
	public void setGeoResourceGeometry(GeoResourceGeometry geoResourceGeometry) {
		this.geoResourceGeometry = geoResourceGeometry;
	}

}
