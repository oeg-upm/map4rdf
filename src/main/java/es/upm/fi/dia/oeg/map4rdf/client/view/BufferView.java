package es.upm.fi.dia.oeg.map4rdf.client.view;


import java.util.ArrayList;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetBufferGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetBufferGeoResourcesResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.BufferSetPointEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.BufferSetPointHandler;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.BufferPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.GeoprocessingPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.ResultsPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.widget.PopupGeoprocessingView;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.GeoprocessingType;

public class BufferView extends ResizeComposite implements BufferPresenter.Display,BufferSetPointHandler{
	private DispatchAsync dispatchAsync;
	private MapPresenter mapPresenter;
	private ResultsPresenter resultsPresenter;
	private DashboardPresenter dashboardPresenter;
	private GeoprocessingPresenter.Display geoprocessingPresenterDisplay;
	private EventBus eventBus;
	private BrowserResources browserResources;
	private BrowserMessages browserMessages;
	//private Grid mainGrid;
	private Anchor anchorResource;
	private FlowPanel panelAnchorResource;
	private TextBox searchTextBox;
	private TextBox distanceTextBox;
	private ListBox distanceTypeBox;
	private Panel resourcePanel;
	private Panel addResourcePanel;
	private Panel removeResourcePanel;
	private Panel resultsBufferWidget;
	private final DrawPointStyle pointStyle=new DrawPointStyle(DrawPointStyle.Style.BLUE);
	private GeoResource resource;
	private Geometry geometry;
	private enum DistanceTypes{
		Km,m;
	}
	@Inject
	public BufferView(EventBus eventBus,MapPresenter mapPresenter,ResultsPresenter resultsPresenter, DispatchAsync dispatchAsync, BrowserResources browserResources,
			BrowserMessages browserMessages){
		this.dispatchAsync = dispatchAsync;
		this.mapPresenter=mapPresenter;
		this.resultsPresenter=resultsPresenter;
		this.browserMessages=browserMessages;
		this.browserResources=browserResources;
		this.eventBus=eventBus;
		eventBus.addHandler(BufferSetPointEvent.getType(),this);
		initWidget(createUi());
	}
	
	
	private Widget createUi() {
		
		ScrollPanel mainPanel = new ScrollPanel();
		//mainPanel.setWidth("160px");
		VerticalPanel panel=new VerticalPanel();
		panel.setWidth("140px");
		Label mainLabel = new Label(browserMessages.bufferIntro());
		panel.add(mainLabel);
		resourcePanel= new FlowPanel();
		panel.add(resourcePanel);
		addResourcePanel= new FlowPanel();
		Grid addGrid = new Grid(2,2);
		addGrid.setWidth("140px");
		Label searchLabel = new Label(browserMessages.searchCenter());
		searchTextBox = new TextBox();
		searchTextBox.setStyleName(browserResources.css().searchBox());
		searchTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(dashboardPresenter!=null){
					if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER ){
						openMainPopup();
					}
				}
			}
		});
		Button addButton =  new Button("",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				if(dashboardPresenter!=null){
					openMainPopup();
				}
			}
		});
		addButton.setSize("32px", "28px");
		Image plusIcon = new Image(browserResources.plusIcon());
		plusIcon.setSize("20px", "20px");
		addButton.getElement().appendChild(plusIcon.getElement());
;
		addGrid.setWidget(0, 0, searchLabel);
		addGrid.setWidget(1, 0, searchTextBox);
		addGrid.setWidget(1, 1, addButton);
		addResourcePanel.add(addGrid);
		resourcePanel.add(addResourcePanel);
		removeResourcePanel=new FlowPanel();
		Grid removeResourceGrid= new Grid(2,2);
		Label center = new Label(browserMessages.currentCenter());
		Button buttonErase = new Button();
		buttonErase.setSize("32px", "28px");
		buttonErase.getElement().appendChild(new Image(browserResources.eraserIcon()).getElement());
		ClickHandler handler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				removePoint();
			}
		};
		buttonErase.addClickHandler(handler);
		anchorResource = new Anchor();
		anchorResource.setSize("140px", "40px");
		DOM.setStyleAttribute(anchorResource.getElement(), "wordWrap", "break-word");
		panelAnchorResource= new FlowPanel();
		panelAnchorResource.add(anchorResource);
		panelAnchorResource.setWidth("142px");
		//panelAnchorResource.setSize("142px", "42px");
		panelAnchorResource.setStyleName(browserResources.css().searchPanel());
		removeResourceGrid.setWidget(0, 0, center);
		removeResourceGrid.setWidget(1, 0, panelAnchorResource);
		removeResourceGrid.setWidget(1, 1, buttonErase);
		removeResourcePanel.add(removeResourceGrid);
		
		Grid distanceGrid= new Grid(2,2);
		distanceTextBox = new TextBox();
		distanceTextBox.setStyleName(browserResources.css().distanceBox());
		distanceTextBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(event.getNativeEvent().getCharCode() == KeyCodes.KEY_ENTER ){
					drawPoints();
				}
			}
		});
		distanceTypeBox = new ListBox(false);
		for(DistanceTypes i:DistanceTypes.values()){
			distanceTypeBox.addItem(i.name());
		}
		distanceGrid.setWidget(0, 0, new Label(browserMessages.distance()));
		distanceGrid.setWidget(0, 1, new Label(browserMessages.unit()));
		distanceGrid.setWidget(1, 0, distanceTextBox);
		distanceGrid.setWidget(1, 1, distanceTypeBox);
		panel.add(distanceGrid);
		Button drawPoints=new Button(browserMessages.drawPoints(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				drawPoints();
			}
		});
		panel.add(drawPoints);
		mainPanel.add(panel);
		return mainPanel;
	}
	
	private void removePoint() {
		
		mapPresenter.removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ORANGE));
		anchorResource.setText("");
		anchorResource.setHref("");
		this.resource=null;
		this.geometry=null;
		mapPresenter.removePointsStyle(pointStyle);
		resourcePanel.clear();
		resourcePanel.add(addResourcePanel);
		if(resultsBufferWidget!=null){
			resultsPresenter.removeWidget(resultsBufferWidget);
			resultsBufferWidget=null;
		}
	}


	private void drawPoints() {
		
		if(resource!=null && geometry!=null){
			mapPresenter.getDisplay().getDefaultLayer().getMapView().closeWindow();
			mapPresenter.removePointsStyle(pointStyle);
			double radiousKM=convertStringToDoubleRadiousKM(distanceTextBox.getValue(), DistanceTypes.valueOf(distanceTypeBox.getValue(distanceTypeBox.getSelectedIndex())));
			if(radiousKM<0.0){
				throwErrorMenssageOfConvertion(radiousKM);
				return;
			}
			mapPresenter.getDisplay().startProcessing();
			GetBufferGeoResources action= new GetBufferGeoResources(resource.getUri(), geometry, radiousKM);
			dispatchAsync.execute(action, new AsyncCallback<GetBufferGeoResourcesResult>() {

				@Override
				public void onFailure(Throwable caught) {
					
					mapPresenter.getDisplay().stopProcessing();
					Window.alert(browserMessages.errorCommunication());
				}
			
				@Override
				public void onSuccess(GetBufferGeoResourcesResult result) {
					
					mapPresenter.drawGeoResouces(result.getListGeoResources(),pointStyle);
					mapPresenter.getDisplay().stopProcessing();
					mapPresenter.setVisibleBox(result.getBoundingBox());
					if(resultsBufferWidget!=null){
						resultsPresenter.removeWidget(resultsBufferWidget);
					}
					generateWidgetResults(result.getListGeoResources());
					resultsPresenter.addWidget(resultsBufferWidget, browserMessages.buffer());
					resultsPresenter.doSelectedWidget(resultsBufferWidget);
					dashboardPresenter.getDisplay().doSelectedWestWidget(resultsPresenter.getDisplay().asWidget());
				}
			});
		}
		//mapPresenter.drawGeoResouces(resources, pointStyle);
	}
	private void generateWidgetResults(List<GeoResource> results){
		FlowPanel flowPanel=new FlowPanel();
		for (GeoResource resource : results) {
			String label = resource.getLabel(LocaleUtil.getClientLanguage());
			if (label == null) {
				resource.getDefaultLabel();
			}
			for (String lang : LocaleUtil.getFallbackLanguages()) {
				label = resource.getLabel(lang);
				if (label != null) {
					break;
				}
			}
			if(label==null){
				label=resource.getUri();
			}
			Anchor a = new Anchor(label, resource.getUri());
			a.setTarget("_blank");
			FlowPanel anchorContainer = new FlowPanel();
			anchorContainer.add(a);
			flowPanel.add(anchorContainer);
		}
		resultsBufferWidget=new ScrollPanel(flowPanel);
	}
	private double convertStringToDoubleRadiousKM(String text,DistanceTypes type){
		double result;
		try{
			result=Double.parseDouble(text);
		}catch(NumberFormatException e){
			return -1.0;
		}
		if(result<0.0){
			return -2.0;
		}
		switch (type) {
		case Km:
			break;
		case m:
			result=result/1000;
			break;
		default:
			result=-3.0;
			break;
		}
		return result;
	}
	private void throwErrorMenssageOfConvertion(double number){
		if(number==-1.0){
			Window.alert(browserMessages.errorConvertDistance());
		}
		if(number==-2.0){
			Window.alert(browserMessages.errorDistanceNegative());
		}
		if(number==-3.0){
			Window.alert(browserMessages.errorDistanceUnit());
		}
	}
	
	private void setGeoResource(GeoResource resource, Geometry geometry){
		mapPresenter.removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ORANGE));
		List<GeoResource> geoResources=new ArrayList<GeoResource>();
		geoResources.add(resource);
		mapPresenter.drawGeoResouces(geoResources,new DrawPointStyle(DrawPointStyle.Style.ORANGE));
		String label=LocaleUtil.getBestLabel(resource);
		anchorResource.setText(label);
		anchorResource.setHref(resource.getUri());
		/*anchorResource.setTarget("_blank");
		System.out.println("AcnchorResource height: "+anchorResource.getOffsetHeight() );
		panelAnchorResource.setHeight(anchorResource.getOffsetHeight()+"px");*/
		this.resource=resource;
		this.geometry=geometry;
		resourcePanel.clear();
		resourcePanel.add(removeResourcePanel);
	}
	
	private void openMainPopup() {
		
		int width=dashboardPresenter.getDisplay().getMapPanel().getOffsetWidth();
		int height=dashboardPresenter.getDisplay().getMapPanel().getOffsetHeight();
		width=width/3;
		height=height/2;
		if(width<500){
			width=500;
		}
		if(height<410){
			height=410;
		}
		PopupGeoprocessingView popup=new PopupGeoprocessingView(width,height,dashboardPresenter,browserMessages,browserResources,eventBus,GeoprocessingType.Buffer);
		popup.search(searchTextBox.getText());
		if(width>500){
			width=width-20;
		}
		dashboardPresenter.getDisplay().setMainPopup(width,height ,popup,"Geoprocessing");
	}

	public void doSelectedView() {
		geoprocessingPresenterDisplay.doSelectedView(this);
	}


	@Override
	public Widget asWidget() {
		
		return this;
	}



	@Override
	public void setDashboardPresenter(DashboardPresenter dashboardPresenter) {
		
		this.dashboardPresenter=dashboardPresenter;
	}


	@Override
	public void setGeoprocessingDisplay(GeoprocessingPresenter.Display geoprocessingPresenterDisplay) {
		
		this.geoprocessingPresenterDisplay=geoprocessingPresenterDisplay;
	}


	@Override
	public void setBufferPoint(GeoResource geoResource, Geometry geometry) {
		
		doSelectedView();
		setGeoResource(geoResource, geometry);
	}

}
