package es.upm.fi.dia.oeg.map4rdf.client.widget;


import java.util.ArrayList;
import java.util.List;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.services.DirectionsLeg;
import com.google.gwt.maps.client.services.DirectionsRoute;
import com.google.gwt.maps.client.services.DirectionsStep;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoResourceGeometry;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.util.RouteSelectedCallback;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;

public class RoutesDescriptionWidget extends Composite{
	private JsArray<DirectionsRoute> routes;
	private ArrayList<Panel> routePanels;
	private BrowserMessages browserMessages;
	private List<GeoResourceGeometry> geoResources;
	private RouteSelectedCallback callback;
	private ScrollPanel scrollPanel;
	private FlowPanel routesSelect;
	private FlowPanel routesNumbers;
	private ArrayList<Widget> allRoutesNumbers;
	private Widget lastNumberChange;
	//private ListBox routesBox;
	public RoutesDescriptionWidget(JsArray<DirectionsRoute> routes,List<GeoResourceGeometry> geoResources,RouteSelectedCallback callback,BrowserMessages browserMessages){
		this.routes=routes;
		this.geoResources=geoResources;
		this.browserMessages=browserMessages;
		this.callback=callback;
		this.routePanels=new ArrayList<Panel>();
		this.allRoutesNumbers=new ArrayList<Widget>();
		initWidget(createUi());
	}
	private Widget createUi() {
		/*Grid grid=new Grid(2,1);*/
		AbsolutePanel panel=new AbsolutePanel();
		scrollPanel=new ScrollPanel();
		scrollPanel.setSize("100%", "90%");
		initializeRouteSelect();
		panel.add(routesSelect);
		panel.add(scrollPanel);
		/*grid.setWidget(0, 0, routesBox);
		grid.setWidget(1, 0, scrollPanel);*/
		for(int i=0;i<routes.length();i++){
			addRouteSelect(i+1);
			FlowPanel flowPanel= new FlowPanel();
			flowPanel.setSize("100%", "90%");
			DirectionsRoute directionsRoute= routes.get(i);
			if(directionsRoute.getWarnings().length()!=0){
				flowPanel.add(new Label(browserMessages.warning()));
				for(int warn=0;warn<directionsRoute.getWarnings().length();warn++){
					flowPanel.add(new InlineHTML(directionsRoute.getWarnings().get(warn)));
				}
			}
			for(int leg=0;leg<directionsRoute.getLegs().length();leg++){
				int waypoint=leg;
				if(leg>0 && leg <directionsRoute.getLegs().length()){
					waypoint=directionsRoute.getWayPoint_Order().get(leg-1);
					waypoint++;
				}
				addPoint(flowPanel, waypoint);
				DirectionsLeg directionLeg = directionsRoute.getLegs().get(leg);
				flowPanel.add(new Label(browserMessages.distance()));
				flowPanel.add(new InlineHTML(directionLeg.getDistance().getText()));
				flowPanel.add(new Label(browserMessages.estimatedTime()));
				flowPanel.add(new InlineHTML(directionLeg.getDuration().getText()));
				flowPanel.add(new InlineHTML("<br>"));
				for(int step=0;step<directionLeg.getSteps().length();step++){
					DirectionsStep directionsStep = directionLeg.getSteps().get(step);
					flowPanel.add(new InlineHTML("<br>"));
					flowPanel.add(new InlineHTML(directionsStep.getInstructions()));
					flowPanel.add(new InlineHTML("<br>"));
				}
			}
			addPoint(flowPanel, directionsRoute.getLegs().length());
			flowPanel.add(new InlineHTML("<br>"));
			flowPanel.add(new InlineHTML("<br>"));
			flowPanel.add(new Label(browserMessages.copyrights()));
			flowPanel.add(new InlineHTML(directionsRoute.getCopyrights()));
			routePanels.add(flowPanel);
		}
		scrollPanel.setWidget(routePanels.get(0));
		callback.onRouteSelected(routes.get(0));
		return panel;
	}
	private Widget getAnchor(GeoResource resource, Geometry geometry){
		String label=LocaleUtil.getBestLabel(resource);
		Anchor anchor = new Anchor(label, resource.getUri());
		anchor.setTarget("_blank");
		return anchor;
	}
	private void addPoint(Panel panel, int point){
		panel.add(new InlineHTML("<br>"));
		if(DrawPointStyle.getLeterSize()<=point){
			point=DrawPointStyle.getLeterSize()-1;
		}
		char leter=(char)(DrawPointStyle.getMinLeter()+point);
		panel.add(new Image(GWT.getModuleBaseURL()+new DrawPointStyle(leter).getImageURL()));
		//flowPanel.add(new Anchor(routes.get(0).getStartGeocode().getAddress()));
		GeoResourceGeometry resource=geoResources.get(point);
		/*flowPanel.add(new Label(" "));*/
		panel.add(getAnchor(resource.getResource(), resource.getGeometry()));
	}
	private void initializeRouteSelect(){
		routesSelect=new FlowPanel();
		Label label=new Label(browserMessages.nameRoute());
		routesSelect.add(label);
		DOM.setStyleAttribute(label.getElement(), "fontSize", "14px");
		DOM.setStyleAttribute(label.getElement(), "textAlign", "center");
		DOM.setStyleAttribute(label.getElement(), "color", "blue");
		routesNumbers=new FlowPanel();
		DOM.setStyleAttribute(routesNumbers.getElement(), "textAlign", "center");
		routesSelect.add(routesNumbers);
	}
	private void addRouteSelect(final int number){
		Anchor anchor=new Anchor(number+"");
		allRoutesNumbers.add(anchor);
		if(number!=1){
			DOM.setStyleAttribute(anchor.getElement(), "marginLeft","15px");
		}else{
			DOM.setStyleAttribute(anchor.getElement(), "color", "red");
			lastNumberChange=anchor;
		}
		DOM.setStyleAttribute(anchor.getElement(), "fontSize", "12px");
		anchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				DOM.setStyleAttribute(lastNumberChange.getElement(), "color", "blue");
				lastNumberChange=allRoutesNumbers.get(number-1);
				DOM.setStyleAttribute(lastNumberChange.getElement(), "color", "red");
				scrollPanel.setWidget(routePanels.get(number-1));
				callback.onRouteSelected(routes.get(number-1));
			}
		});
		routesNumbers.add(anchor);
	}
	
}
