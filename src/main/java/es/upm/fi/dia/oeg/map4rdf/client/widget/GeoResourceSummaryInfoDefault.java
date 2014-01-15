package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
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
	private Stylesheet style;
	private Panel locationPanel;
	private FlowPanel additionalInfoPanel;
	
	public GeoResourceSummaryInfoDefault(BrowserMessages messages, BrowserResources resources){
		style = resources.css();
		mainPanel = new FlowPanel();		
		DOM.setStyleAttribute(mainPanel.getElement(), "textAlign", "left");
		DOM.setStyleAttribute(mainPanel.getElement(), "textSize", "8pt");
		mainPanel.setSize("200px", "130px");
		DOM.setStyleAttribute(mainPanel.getElement(), "border", "1px solid #424242");
		//panel.setBorder("1px solid #424242");
		mainPanel.setStyleName(resources.css().popup());
		label = new Label();
		DOM.setStyleAttribute(label.getElement(), "wordWrap", "break-word");
		label.addStyleName(style.summaryLabelStyle());
		mainPanel.add(label);
		mainPanel.add(new InlineHTML("<br />"));

		locationPanel = new FlowPanel();

		Grid grid = new Grid(3, 2);
		Label latitudeLabel = new Label(messages.latitude() + ": ");
		latitudeLabel.setStyleName(style.summaryPropertyName());
		grid.setWidget(0, 0, latitudeLabel);
		latitude = new Label();
		latitude.setStyleName(style.summaryPropertyValue());
		grid.setWidget(0, 1, latitude);
		Label longitudLabel = new Label(messages.longitude() + ": ");
		longitudLabel.setStyleName(style.summaryPropertyName());
		grid.setWidget(1, 0, longitudLabel);
		longitude = new Label();
		longitude.setStyleName(style.summaryPropertyValue());
		grid.setWidget(1, 1, longitude);
		Label crsLabel = new Label(messages.crs() + ": ");
		crsLabel.setStyleName(style.summaryPropertyName());
		grid.setWidget(2, 0, crsLabel);
		crs = new Label();
		crs.setStyleName(style.summaryPropertyValue());
		grid.setWidget(2, 1, crs);
		locationPanel.add(grid);

		mainPanel.add(locationPanel);
		mainPanel.add(new InlineHTML("<br>"));

		additionalInfoPanel= new FlowPanel();
		mainPanel.add(additionalInfoPanel);
		DOM.setStyleAttribute(mainPanel.getElement(), "position", "absolute");
		//left style attribute in px is equal to: -(((summary.Width+summary.Padding+summary.Border)-mainGrid.Width)/2)
		DOM.setStyleAttribute(mainPanel.getElement(), "left", (-((200+2+1)-105)/2)+"px");
		//top style attribute in px is equal to: -(summary.Height+summary.Padding+summary.Border+ChooseforSpacingpx+extraRadious)
		DOM.setStyleAttribute(mainPanel.getElement(), "top", (-(110+2+1+10+15))+"px");
	}
	@Override
	public void addAdditionalInfo(Map<String,String> additionalsInfo,int extraRadiousPX){
		additionalInfoPanel.clear();
		if(additionalsInfo!=null && !additionalsInfo.isEmpty()){
			mainPanel.setSize(210+"px", 220+"px");
			DOM.setStyleAttribute(mainPanel.getElement(), "position", "absolute");
			//left style attribute in px is equal to: -(((summary.Width+summary.Padding+summary.Border)-mainGrid.Width)/2)
			DOM.setStyleAttribute(mainPanel.getElement(), "left", (-((210+2+1)-105)/2)+"px");
			if(extraRadiousPX<0){extraRadiousPX=0;}
			//top style attribute in px is equal to: -(summary.Height+summary.Padding+summary.Border+ChooseforSpacingpx+extraRadious)
			DOM.setStyleAttribute(mainPanel.getElement(), "top", (-(220+2+1+5+extraRadiousPX))+"px");
			Grid grid=new Grid(additionalsInfo.size(),2);
			int i=0;
			for(String key:additionalsInfo.keySet()){
				Label label=new Label(key+"  ");
				label.setStyleName(style.summaryPropertyName());
				grid.setWidget(i, 0, label);
				//additionalInfoPanel.add(label);
				label=new Label(additionalsInfo.get(key));
				label.setStyleName(style.summaryPropertyValue());
				grid.setWidget(i++, 1, label);
				//additionalInfoPanel.add(label);
			}
			additionalInfoPanel.add(grid);
		}else{
			mainPanel.setSize("200px", "120px");
			DOM.setStyleAttribute(mainPanel.getElement(), "position", "absolute");
			//left style attribute in px is equal to: -(((summary.Width+summary.Padding+summary.Border)-mainGrid.Width)/2)
			DOM.setStyleAttribute(mainPanel.getElement(), "left", (-((200+2+1)-105)/2)+"px");
			if(extraRadiousPX<0){extraRadiousPX=0;}
			//top style attribute in px is equal to: -(summary.Height+summary.Padding+summary.Border+ChooseforSpacingpx+extraRadious)
			DOM.setStyleAttribute(mainPanel.getElement(), "top", (-(120+2+1+5+extraRadiousPX))+"px");
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
}
