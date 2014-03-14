package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MapShape;
import es.upm.fi.dia.oeg.map4rdf.share.Point;

public class GeoResourceSummaryInfoDefault implements GeoResourceSummaryInfo{
	public interface Stylesheet {
		String summaryLabelStyle();
		String summaryPropertyName();
		String summaryPropertyValue();
		String textButtonStyle();
	}
	private FlowPanel mainPanel;
	private Label longitude;
	private Label label;
	private Label latitude;
	private Label crs;
	private Panel locationPanel;
	private FlowPanel additionalInfoPanel;
	private DialogBox mainWidget;
	public GeoResourceSummaryInfoDefault(BrowserMessages messages, BrowserResources resources){
		mainPanel = new FlowPanel();		
		DOM.setStyleAttribute(mainPanel.getElement(), "textAlign", "left");
		label = new Label();
		DOM.setStyleAttribute(label.getElement(), "wordWrap", "break-word");
		mainPanel.add(label);
		mainPanel.add(new InlineHTML("<br />"));

		locationPanel = new FlowPanel();

		Grid grid = new Grid(3, 2);
		Label latitudeLabel = new Label(messages.latitude() + ": ");
		grid.setWidget(0, 0, latitudeLabel);
		latitude = new Label();
		grid.setWidget(0, 1, latitude);
		Label longitudLabel = new Label(messages.longitude() + ": ");
		grid.setWidget(1, 0, longitudLabel);
		longitude = new Label();
		grid.setWidget(1, 1, longitude);
		Label crsLabel = new Label(messages.crs() + ": ");
		grid.setWidget(2, 0, crsLabel);
		crs = new Label();
		grid.setWidget(2, 1, crs);
		locationPanel.add(grid);

		mainPanel.add(locationPanel);
		mainPanel.add(new InlineHTML("<br>"));

		additionalInfoPanel= new FlowPanel();
		mainPanel.add(additionalInfoPanel);
		mainPanel.setWidth("auto");
		mainPanel.setHeight("auto");
		mainWidget=new DialogBox(false, false);
		mainWidget.setAnimationEnabled(true);
		mainWidget.setGlassEnabled(false);
		DOM.setStyleAttribute(mainWidget.getElement(), "zIndex", "10");
		Button close = new Button(messages.close());
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mainWidget.hide();
			}
		});
		VerticalPanel mainDialogPanel=new VerticalPanel();
		mainDialogPanel.add(mainPanel);
		mainDialogPanel.add(close);
		mainWidget.setWidget(mainDialogPanel);
		mainDialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(mainPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_CENTER);
		mainWidget.setText(messages.informationTittle(""));
	}
	@Override
	public void addAdditionalInfo(Map<String,String> additionalsInfo){
		additionalInfoPanel.clear();
		if(additionalsInfo!=null && !additionalsInfo.isEmpty()){
			Grid grid=new Grid(additionalsInfo.size(),2);
			int i=0;
			for(String key:additionalsInfo.keySet()){
				Label label=new Label(key+"  ");
				grid.setWidget(i, 0, label);
				label=new Label(additionalsInfo.get(key));
				grid.setWidget(i++, 1, label);
			}
			additionalInfoPanel.add(grid);
		}
	}

	@Override
	public Widget getWidget() {
		return mainPanel;
	}
	@Override
	public void setGeoResource(GeoResource resource, Geometry geometry) {
		label.setText(LocaleUtil.getBestLabel(resource, true));
		additionalInfoPanel.clear();
		if (geometry.getType() == MapShape.Type.POINT) {
			locationPanel.setVisible(true);
			Point point = (Point) geometry;
			latitude.setText(Double.toString(point.getY()));
			longitude.setText(Double.toString(point.getX()));
			crs.setText(point.getProjection());
		} else {
			locationPanel.setVisible(false);
		}
	}
	@Override
	public void clearAdditionalInfo() {
		additionalInfoPanel.clear();
	}
	@Override
	public boolean isVisible() {
		return mainWidget.isShowing();
	}
	@Override
	public void show() {
		mainWidget.center();
	}
	@Override
	public void close() {
		mainWidget.hide();
	}
}
