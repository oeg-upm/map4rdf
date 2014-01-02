package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.event.BufferSetPointEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.RoutesAddPointEvent;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoResourceGeometry;
import es.upm.fi.dia.oeg.map4rdf.client.util.PanelWithGeoResourceGeometry;
import es.upm.fi.dia.oeg.map4rdf.client.util.RoutesAddGeoResourceType;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.GeoprocessingType;

public class PopupGeoprocessingViewWidget extends ResizeComposite {
	private int width;
	private int height;
	private GeoprocessingType type;
	private EventBus eventBus;
	private List<GeoResourceGeometry> searchResources;
	private Map<ClickHandler,PanelWithGeoResourceGeometry> searchRelationHandler;
	private LayoutPanel panel;
	private Panel search;
	private BrowserResources browserResources;
	private ScrollPanel scrollPanel;
	private DashboardPresenter dashboardPresenter;
	public PopupGeoprocessingViewWidget(int width,int height,EventBus eventBus,DashboardPresenter dashboardPresenter,GeoprocessingType type,BrowserResources browserResources){
		this.width=width;
		this.height=height;
		this.browserResources=browserResources;
		this.eventBus=eventBus;
		this.dashboardPresenter=dashboardPresenter;
		this.type=type;
		searchRelationHandler=new HashMap<ClickHandler, PanelWithGeoResourceGeometry>();
		searchResources=new ArrayList<GeoResourceGeometry>();
		initWidget(createUi());
	}
	public void clear(){
		search.clear();
		searchRelationHandler.clear();
		searchResources.clear();
	}
	private Widget createUi() {
		panel = new LayoutPanel();
		panel.setSize(width+"px", height+"px");
		panel.setStyleName(browserResources.css().searchPanel());
		search = new FlowPanel();
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidget(search);
		panel.add(scrollPanel);
		
		return panel;
	}
	
	public void addSearchResultGeoResource(String label,GeoResourceGeometry geoResourceGeometry){
		GeoResource resource= geoResourceGeometry.getResource();
		searchResources.add(geoResourceGeometry);
		Anchor anchor = new Anchor(label, resource.getUri());
		anchor.setTarget("_blank");
		FlowPanel anchorContainer = new FlowPanel();
		Button button = new Button();
		button.setSize("32px","28px");
		button.getElement().appendChild(new Image(browserResources.plusIcon()).getElement());
		anchorContainer.add(button);
		anchorContainer.add(new InlineHTML("<a> </a>"));
		anchorContainer.add(anchor);
		ClickHandler handler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				addOrSetResource(this);
				dashboardPresenter.getDisplay().closeMainPopup();
			}
		};
		button.addClickHandler(handler);
		searchRelationHandler.put(handler, new PanelWithGeoResourceGeometry(geoResourceGeometry,anchorContainer));
		search.add(anchorContainer);
	}
	private void addOrSetResource(ClickHandler handler){
		GeoResource resource=searchRelationHandler.get(handler).getGeoResourceGeometry().getResource();
		Geometry geometry=searchRelationHandler.get(handler).getGeoResourceGeometry().getGeometry();
		switch (type) {
		case Route:
			eventBus.fireEvent(new RoutesAddPointEvent(resource, geometry,RoutesAddGeoResourceType.RoutesPopup));
			break;
		case Buffer:
			eventBus.fireEvent(new BufferSetPointEvent(resource, geometry));
			break;
		default:
			break;
		}
	}
}
