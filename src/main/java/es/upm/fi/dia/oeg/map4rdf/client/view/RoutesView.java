package es.upm.fi.dia.oeg.map4rdf.client.view;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtopenmaps.openlayers.client.LonLat;



import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.services.DirectionsRequest;
import com.google.gwt.maps.client.services.DirectionsResult;
import com.google.gwt.maps.client.services.DirectionsResultHandler;
import com.google.gwt.maps.client.services.DirectionsRoute;
import com.google.gwt.maps.client.services.DirectionsService;
import com.google.gwt.maps.client.services.DirectionsStatus;
import com.google.gwt.maps.client.services.DirectionsWaypoint;
import com.google.gwt.maps.client.services.TravelMode;
/*import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.geocode.DirectionQueryOptions;
import com.google.gwt.maps.client.geocode.DirectionResults;
import com.google.gwt.maps.client.geocode.Directions;
import com.google.gwt.maps.client.geocode.DirectionsCallback;
import com.google.gwt.maps.client.geocode.DirectionsPanel;
import com.google.gwt.maps.client.geocode.Waypoint;
import com.google.gwt.maps.client.geom.LatLng;*/
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetRoutePoints;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetRoutePointsResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.RoutesAddPointEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.RoutesAddPointHandler;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.GeoprocessingPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.MapPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.ResultsPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.RoutesPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoResourceGeometry;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.util.PanelWithGeoResourceGeometry;
import es.upm.fi.dia.oeg.map4rdf.client.util.RouteSelectedCallback;
import es.upm.fi.dia.oeg.map4rdf.client.util.RoutesAddGeoResourceType;
import es.upm.fi.dia.oeg.map4rdf.client.widget.PopupGeoprocessingView;
import es.upm.fi.dia.oeg.map4rdf.client.widget.RoutesDescriptionWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.RoutesWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.GeoprocessingType;
import es.upm.fi.dia.oeg.map4rdf.share.OpenLayersAdapter;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLineBean;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;


public class RoutesView extends ResizeComposite implements RoutesPresenter.Display, RoutesAddPointHandler{
	private DispatchAsync dispatchAsync;
	private EventBus eventBus;
	private MapPresenter mapPresenter;
	private GeoprocessingPresenter.Display geoprocessingPresenterDisplay;
	private DashboardPresenter dashboardPresenter;
	private ResultsPresenter resultsPresenter;
	private BrowserResources browserResources;
	private BrowserMessages browserMessages;
	private WidgetFactory widgetFactory;
	private ScrollPanel scrollPanel;
	private Panel panel;
	private InlineHTML addPointMessage;
	private RoutesWidget routesWidget;
	private Grid gridSearchTextAndAddButton;
	private TextBox searchTextBox;
	private Button addButton;
	private TextBox searchTextBox1;
	private Button addButton1;
	private Button traceRoute;
	private int rows;
	private int lastTypeOpen=0;
	private boolean[] isSearchFree;
	private final int minPixelHeightOfRoutesWidget=100;
	private List<GeoResourceGeometry> route;
	private Map<ClickHandler,PanelWithGeoResourceGeometry> relationHandler;
	private RoutesDescriptionWidget routeDescriptionWidget=null;
	private MyBoolean routeAlternatives=new MyBoolean(true);
	private MyBoolean avoidHighways=new MyBoolean(false);
	private MyBoolean avoidTolls=new MyBoolean(false);
	private MyBoolean optimizeWaypoints=new MyBoolean(true);
	private Map<String,TravelMode> travelModes;
	private TravelMode travelMode;
	private String[] travelsModeInOrder;
	private ListBox travelListBox;
	private RouteSelectedCallback routeSelectedCallback;
	private DisclosurePanel optionsDisPanel;
	private List<Widget> disableWidgetsIfNoDriving;
	
	@Inject
	public RoutesView(EventBus eventBus,MapPresenter mapPresenter,ResultsPresenter resultsPresenter, DispatchAsync dispatchAsync, BrowserResources browserResources,
			BrowserMessages browserMessages, WidgetFactory widgetFactory) {
		this.dispatchAsync = dispatchAsync;
		this.eventBus=eventBus;
		this.mapPresenter = mapPresenter;
		this.resultsPresenter = resultsPresenter;
		this.browserResources=browserResources;
		this.browserMessages=browserMessages;
		this.widgetFactory = widgetFactory;
		relationHandler=new HashMap<ClickHandler, PanelWithGeoResourceGeometry>();
		route=new ArrayList<GeoResourceGeometry>();
		rows=0;
		isSearchFree=new boolean[]{true,true};
		travelModes=new HashMap<String, TravelMode>();
		disableWidgetsIfNoDriving=new ArrayList<Widget>();
		initTravelModes();
		initWidget(createUi());
		initRouteSelectedCallback();
		eventBus.addHandler(RoutesAddPointEvent.getType(),this);
	}
	
	private void initRouteSelectedCallback() {
		
		routeSelectedCallback=new RouteSelectedCallback() {
			
			@Override
			public void onRouteSelected(DirectionsRoute route) {
				
				drawSelectedRoute(route);
			}
		};
	}

	private Widget createUi() {
		panel = new FlowPanel();
		//panel.setSize("100%", "100%");
		/*pointsPanel = new FlowPanel();
		gridPointsPanel= new Grid(rows,columns);
		pointsPanel.add(gridPointsPanel);
		panel.add(pointsPanel);*/
		addPointMessage=new InlineHTML("<p>"+browserMessages.messageAddRoutePoint()+"</p>");
		panel.add(addPointMessage);
		routesWidget = new RoutesWidget("99%",minPixelHeightOfRoutesWidget+"px", browserResources);
		panel.add(routesWidget);
		initializeSearchTextBoxsAndAddButtons();
		routesWidget.resizeRows(2);
		routesWidget.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle(DrawPointStyle.getMinLeter()).getImageURL()));
		routesWidget.setWidget(0, 1,searchTextBox);
		routesWidget.setWidget(0, 2,addButton);
		routesWidget.setWidget(1, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+1)).getImageURL()));
		routesWidget.setWidget(1, 1,searchTextBox1);
		routesWidget.setWidget(1, 2,addButton1);
		gridSearchTextAndAddButton= new Grid(1, 3);
		/*gridSearchTextAndAddButton.setWidget(0, 0, searchTextBox);
		gridSearchTextAndAddButton.setWidget(0, 1, addButton);*/
		panel.add(gridSearchTextAndAddButton);
		//panel.add(new InlineHTML("<br>"));
		traceRoute=new Button(browserMessages.buttonTraceRoute(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				traceRoute();
			}
		});
		DOM.setStyleAttribute(traceRoute.getElement(), "position", "absolute");
		DOM.setStyleAttribute(traceRoute.getElement(), "right", "20px");
		//panel.add(traceRoute);
		Grid optionsGrid=initializeOptionsGrid();
		//panel.add(optionsGrid);
		optionsDisPanel=new DisclosurePanel(browserMessages.moreOptions());
		optionsDisPanel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				resize();
			}
		});
		optionsDisPanel.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				resize();
			}
		});
		optionsDisPanel.add(optionsGrid);
		panel.add(optionsDisPanel);
		panel.add(new InlineHTML("<br>"));
		panel.add(traceRoute);
		panel.add(new InlineHTML("<br>"));
		scrollPanel = new ScrollPanel();
		scrollPanel.setWidget(panel);
		scrollPanel.setWidth("260px");
		return scrollPanel;
	}

	private void initTravelModes() {
		
		travelsModeInOrder=new String[3];
		travelsModeInOrder[0]=browserMessages.driving();
		travelsModeInOrder[1]=browserMessages.bicycling();
		travelsModeInOrder[2]=browserMessages.walking();
		travelModes.put(browserMessages.driving(), TravelMode.DRIVING);
		travelModes.put(browserMessages.bicycling(), TravelMode.BICYCLING);
		travelModes.put(browserMessages.walking(), TravelMode.WALKING);
		travelMode=travelModes.get(travelsModeInOrder[0]);
	}

	private Grid initializeOptionsGrid() {
		Grid grid=new Grid(5,2);
		grid.setWidget(0, 0, new Label(browserMessages.travelMode()));
		grid.setWidget(1, 0, new Label(browserMessages.routeAlternatives()));
		grid.setWidget(2, 0, new Label(browserMessages.optimizeWaypoints()));
		Label label=new Label(browserMessages.avoidHighways());
		disableWidgetsIfNoDriving.add(label);
		grid.setWidget(3, 0,label);
		label= new Label(browserMessages.avoidTolls());
		disableWidgetsIfNoDriving.add(label);
		grid.setWidget(4, 0,label);
		travelListBox = new ListBox(false);
		for(int i=0;i<travelsModeInOrder.length;i++){
			travelListBox.addItem(travelsModeInOrder[i]);
		}
		travelListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				if(travelModes.containsKey(travelListBox.getItemText(travelListBox.getSelectedIndex()))){
					travelMode=travelModes.get(travelListBox.getItemText(travelListBox.getSelectedIndex()));
					changeOptions();
				} else {
					widgetFactory.getDialogBox().showError(
							browserMessages.travelModeDoesntExists());
				}
			}
		});
		grid.setWidget(0, 1, travelListBox);
		ListBox listBox=new ListBox(false);
		BooleanBoxChangeHandler changeHandler=new BooleanBoxChangeHandler(listBox, browserMessages.yes(), routeAlternatives);
		listBox.addItem(browserMessages.yes());
		listBox.addItem(browserMessages.no());
		listBox.addChangeHandler(changeHandler);
		grid.setWidget(1, 1, listBox);
		if(routeAlternatives.getBoolean()){
			listBox.setSelectedIndex(0);
		}else{
			listBox.setSelectedIndex(1);
		}
		listBox=new ListBox(false);
		changeHandler=new BooleanBoxChangeHandler(listBox, browserMessages.yes(), optimizeWaypoints);
		listBox.addItem(browserMessages.yes());
		listBox.addItem(browserMessages.no());
		listBox.addChangeHandler(changeHandler);
		grid.setWidget(2, 1, listBox);
		if(optimizeWaypoints.getBoolean()){
			listBox.setSelectedIndex(0);
		}else{
			listBox.setSelectedIndex(1);
		}
		listBox=new ListBox(false);
		changeHandler=new BooleanBoxChangeHandler(listBox, browserMessages.yes(), avoidHighways);
		disableWidgetsIfNoDriving.add(listBox);
		listBox.addItem(browserMessages.yes());
		listBox.addItem(browserMessages.no());
		listBox.addChangeHandler(changeHandler);
		grid.setWidget(3, 1, listBox);
		if(avoidHighways.getBoolean()){
			listBox.setSelectedIndex(0);
		}else{
			listBox.setSelectedIndex(1);
		}
		listBox=new ListBox(false);
		disableWidgetsIfNoDriving.add(listBox);
		changeHandler=new BooleanBoxChangeHandler(listBox, browserMessages.yes(), avoidTolls);
		listBox.addItem(browserMessages.yes());
		listBox.addItem(browserMessages.no());
		listBox.addChangeHandler(changeHandler);
		grid.setWidget(4, 1, listBox);
		if(avoidTolls.getBoolean()){
			listBox.setSelectedIndex(0);
		}else{
			listBox.setSelectedIndex(1);
		}
		return grid;
	}
	private void changeOptions() {
		
		boolean doVisible=false;
		if(travelMode==TravelMode.DRIVING){
			doVisible=true;
		}
		for(Widget widget:disableWidgetsIfNoDriving){
			widget.setVisible(doVisible);
		}
	}
	private void initializeSearchTextBoxsAndAddButtons() {
		KeyPressHandler keyPressHandler=new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(dashboardPresenter!=null){
					if(event.getNativeEvent().getCharCode() == KeyCodes.KEY_ENTER ){
						lastTypeOpen=0;
						openMainPopup(0);
					}
				}
			}
		};
		searchTextBox = new TextBox();
		searchTextBox.setSize("170px", "20px");
		/*searchTextBox.setStyleName(browserResources.css().searchBox());*/
		searchTextBox.addKeyPressHandler(keyPressHandler);
		keyPressHandler=new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(dashboardPresenter!=null){
					if(event.getNativeEvent().getCharCode() == KeyCodes.KEY_ENTER ){
						lastTypeOpen=1;
						openMainPopup(1);
					}
				}
			}
		};
		searchTextBox1 = new TextBox();
		searchTextBox1.setSize("170px", "20px");
		searchTextBox1.addKeyPressHandler(keyPressHandler);
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				if(dashboardPresenter!=null){
					lastTypeOpen=0;
					openMainPopup(0);
				}
			}
		};
		addButton =  new Button("",clickHandler);
		addButton.setSize("32px", "28px");
		Image plusIcon = new Image(browserResources.plusIcon());
		plusIcon.setSize("20px", "20px");
		addButton.getElement().appendChild(plusIcon.getElement());
		clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				if(dashboardPresenter!=null){
					lastTypeOpen=1;
					openMainPopup(1);
				}
			}
		};
		addButton1 =  new Button("",clickHandler);
		addButton1.setSize("32px", "28px");
		plusIcon = new Image(browserResources.plusIcon());
		plusIcon.setSize("20px", "20px");
		addButton1.getElement().appendChild(plusIcon.getElement());
	}

	@Override
	public Widget asWidget() {
		
		return this;
	}
	private void traceRoute(){
		dashboardPresenter.getDisplay().closeMainPopup();
		mapPresenter.getDisplay().getDefaultLayer().getMapView().closeWindow();
		List<Point> points = new ArrayList<Point>();
		for(GeoResourceGeometry geoRG : route){
			for(Point point:geoRG.getGeometry().getPoints()){
				LonLat openPoint= OpenLayersAdapter.getLatLng(point);
				openPoint.transform(point.getProjection(), "EPSG:4326");
				TwoDimentionalCoordinate twoCoor=OpenLayersAdapter.getTwoDimentionalCoordinate(openPoint);
				points.add(new PointBean(point.getUri(), twoCoor.getX(),twoCoor.getY(),"EPSG:4326"));
			}
		}
		if(route.size()<2){
			widgetFactory.getDialogBox().showError(browserMessages.error2OrMorePoints());
			mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
			mapPresenter.getDisplay().stopProcessing();
			return;
		}
		mapPresenter.getDisplay().startProcessing();
		GetRoutePoints action = new GetRoutePoints(points);
		dispatchAsync.execute(action, new AsyncCallback<GetRoutePointsResult>() {

			@Override
			public void onFailure(Throwable caught) {
				
				widgetFactory.getDialogBox().showError(browserMessages.errorCommunication());
				mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
				mapPresenter.getDisplay().stopProcessing();
			}

			@Override
			public void onSuccess(GetRoutePointsResult result) {
				mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
				List<GeoResource> listGeoResource = new ArrayList<GeoResource>();
				List<Point> points = result.getPoints();
				if(points.isEmpty()){
					//widgetFactory.getDialogBox().showError(browserMessages.errorNotRouteTo());
					/*for(GeoResourceGeometry geoRG : route){
						points.addAll(geoRG.getGeometry().getPoints());
					}*/
					executeGoogleDirections();
				}else{
					Geometry geometry = new PolyLineBean(null, points);
					GeoResource geoResource = new GeoResource(null,geometry);
					listGeoResource.add(geoResource);
					mapPresenter.getDisplay().stopProcessing();
					mapPresenter.drawGeoResouces(listGeoResource, new DrawPointStyle(DrawPointStyle.Style.ROUTES));
					if(points.size()>=2){
						mapPresenter.getDisplay().changeZoom(points);
					}
				}
			}
		});
	}
	public void setDashboardPresenter(DashboardPresenter dashboardPresenter){
		this.dashboardPresenter=dashboardPresenter;
	}

	@Override
	public void setGeoprocessingDisplay(GeoprocessingPresenter.Display geoprocessingPresenterDisplay) {
		this.geoprocessingPresenterDisplay=geoprocessingPresenterDisplay;
	}

	@Override
	public void onResize(){
		resizeRoutesWidget();
		scrollPanel.onResize();
	}

	@Override
	public void addRoutePoint(GeoResource geoResource, Geometry geometry,RoutesAddGeoResourceType type) {
		
		addGeoResource(geoResource, geometry,type);
		doSelectedView();
	}

	private void removePoint(ClickHandler handler){
		/*if(routeDescriptionWidget!=null){
			//geoprocessingPresenterDisplay.removeWidget(routeDescriptionWidget);
			resultsPresenter.removeWidget(routeDescriptionWidget);
			routeDescriptionWidget=null;
		}*/
		route.remove(relationHandler.get(handler).getGeoResourceGeometry());
		/*GeoResource resource=relationHandler.get(handler).getGeoResourceGeometry().getResource();
		if(changedPoints.containsKey(resource)){
			mapPresenter.changeStylePointGeoResource(resource,new DrawPointStyle(changedPoints.get(resource)));
		}*/
		int row=relationHandler.get(handler).getRow();
		//gridPointsPanel.removeRow(row);
		if(rows<=2){
			gridSearchTextAndAddButton.clear();
			if(row==0){
				routesWidget.setWidget(row, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle(DrawPointStyle.getMinLeter()).getImageURL()));
				routesWidget.setWidget(row, 1, searchTextBox);
				routesWidget.setWidget(row, 2, addButton);
			}
			if(row==1){
				searchTextBox1.setText(searchTextBox.getText());
				searchTextBox.setText("");
				routesWidget.setWidget(row, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+1)).getImageURL()));
				routesWidget.setWidget(row, 1, searchTextBox1);
				routesWidget.setWidget(row, 2, addButton1);
			}
			isSearchFree[row]=true;
		}else{
			for(int i=row+1;i<rows;i++){
				for(PanelWithGeoResourceGeometry tempPanel:relationHandler.values()){
					if(tempPanel.getRow()==i){
						tempPanel.setRow(tempPanel.getRow()-1);
					}
				}
				char leter=DrawPointStyle.getMinLeter();
				if(i-1<DrawPointStyle.getLeterSize()){
					leter=(char)(DrawPointStyle.getMinLeter()+i-1);
				}else{
					leter=DrawPointStyle.getMaxLeter();
				}
				routesWidget.setWidget(i, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle(leter).getImageURL()));
			}
			routesWidget.removeRow(row);
			char leter=DrawPointStyle.getMinLeter();
			if(rows-1<DrawPointStyle.getLeterSize()){
				leter=(char)(DrawPointStyle.getMinLeter()+rows-1);
			}else{
				leter=DrawPointStyle.getMaxLeter();
			}
			gridSearchTextAndAddButton.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle(leter).getImageURL()));
		}
		relationHandler.remove(handler);
		rows--;
		resizeRoutesWidget();
		mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
		if(routeDescriptionWidget!=null){
			resultsPresenter.removeWidget(routeDescriptionWidget);
			routeDescriptionWidget=null;
		}
		mapPresenter.removePointsStyle(new DrawPointStyle(DrawPointStyle.getMinLeter()));
		if(rows!=1){
			for(GeoResourceGeometry i:route){
				if(i!=null){
					List<GeoResource> resources=new ArrayList<GeoResource>();
					resources.add(i.getResource());
					mapPresenter.drawGeoResouces(resources, new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+route.indexOf(i))));
				}
			}
		}else{
			int number=0;
			if(!isSearchFree[1]){
				number=1;
			}if(!isSearchFree[0]){
				number=0;
			}
			List<GeoResource> resources=new ArrayList<GeoResource>();
			resources.add(route.get(0).getResource());
			mapPresenter.drawGeoResouces(resources, new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+number)));
		}
		/*if(route.size()>=2){
			traceRoute();
		} else {
			mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.GREEN));
		}*/
	}
	private void addGeoResource(GeoResource resource, Geometry geometry,RoutesAddGeoResourceType type){
		switch (type) {
		case RoutesPopup:
			if(lastTypeOpen==0){
				if(rows<2){
					addGeoResourceInRow(resource, geometry, 0);
					if(isSearchFree[lastTypeOpen]){
						isSearchFree[lastTypeOpen]=false;
						rows++;
						if(rows==2){
							//A�adir el serachBox0 en otro panel;
							gridSearchTextAndAddButton.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+2)).getImageURL()));
							gridSearchTextAndAddButton.setWidget(0, 1, searchTextBox);
							gridSearchTextAndAddButton.setWidget(0, 2, addButton);
						}
					}
				}else{
					routesWidget.resizeRows(rows+1);
					addGeoResourceInRow(resource, geometry, rows);
					rows++;
					char leter=DrawPointStyle.getMinLeter();
					if(rows<DrawPointStyle.getLeterSize()){
						leter=(char)(DrawPointStyle.getMinLeter()+rows-1);
					}else{
						leter=DrawPointStyle.getMaxLeter();
					}
					gridSearchTextAndAddButton.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle(leter).getImageURL()));
				}
			}
			if(lastTypeOpen==1){
				if(rows<2){
					addGeoResourceInRow(resource, geometry, 1);
					if(isSearchFree[lastTypeOpen]){
						isSearchFree[lastTypeOpen]=false;
						rows++;
						if(rows==2){
							//A�adir el serachBox0 en otro panel;
							gridSearchTextAndAddButton.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+2)).getImageURL()));
							gridSearchTextAndAddButton.setWidget(0, 1, searchTextBox);
							gridSearchTextAndAddButton.setWidget(0, 2, addButton);
						}
					}
				}
			}
			break;
		case OtherPopup:
			int row=-1;
			for(int i=0;i<isSearchFree.length;i++){
				if(isSearchFree[i]){
					row=i;
					isSearchFree[i]=false;
					break;
				}
			}
			if(row==-1){
				routesWidget.resizeRows(rows+1);
				addGeoResourceInRow(resource, geometry, rows);
				rows++;
				char leter=DrawPointStyle.getMinLeter();
				if(rows<DrawPointStyle.getLeterSize()){
					leter=(char)(DrawPointStyle.getMinLeter()+rows);
				}else{
					leter=DrawPointStyle.getMaxLeter();
				}
				gridSearchTextAndAddButton.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle(leter).getImageURL()));
			}else{
				addGeoResourceInRow(resource, geometry, row);
				if(lastTypeOpen==row){
					dashboardPresenter.getDisplay().closeMainPopup();
				}
				rows++;
				if(rows==2){
					//Añadir el searchBox0 en otro panel;
					gridSearchTextAndAddButton.setWidget(0, 0, new Image(GWT.getModuleBaseURL()+new DrawPointStyle((char)(DrawPointStyle.getMinLeter()+2)).getImageURL()));
					gridSearchTextAndAddButton.setWidget(0, 1, searchTextBox);
					gridSearchTextAndAddButton.setWidget(0, 2, addButton);
				}
			}
			/*addGeoResourceInRow(resource, geometry, rows);
			rows++;*/
			//Aqui abria que meter el punto en el primer cuadrado libre y si estan todos ocupados agregar uno nuevo y agregar el 0;
			break;
		}
		resizeRoutesWidget();
	}
	private void addGeoResourceInRow(GeoResource resource, Geometry geometry,int row){
		GeoResourceGeometry geoResourceGeometry = new GeoResourceGeometry(resource, geometry);
		if(rows==1 && row==0){
			GeoResourceGeometry temp=route.get(0);
			route.clear();
			route.add(geoResourceGeometry);
			route.add(temp);
		}else{
			route.add(geoResourceGeometry);
		}
		//route.set(row, geoResourceGeometry);
		String label=LocaleUtil.getBestLabel(resource);
		FlowPanel anchorContainer=new FlowPanel();
		Anchor anchor = new Anchor(label, resource.getUri());
		anchor.setTarget("_blank");
		anchorContainer.add(anchor);
		anchorContainer.setSize("170px","100%");
		DOM.setStyleAttribute(anchorContainer.getElement(), "wordWrap", "break-word");
		DOM.setStyleAttribute(anchorContainer.getElement(), "textAlign", "center");
		DOM.setStyleAttribute(anchor.getElement(), "textAlign","center");
		//routesWidget.resizeRows(rows+1);
		char leter=DrawPointStyle.getMinLeter();
		if(row<DrawPointStyle.getLeterSize()){
			leter=(char)(DrawPointStyle.getMinLeter()+row);
		}else{
			leter=DrawPointStyle.getMaxLeter();
		}
		DrawPointStyle style= new DrawPointStyle(leter);
		/*DrawPointStyle.Style changedStyle=mapPresenter.changeStylePointGeoResource(resource,style);
		if(changedStyle!=null && changedStyle!=style.getStyle()){
			changedPoints.put(resource, changedStyle);
		}*/
		List<GeoResource> list= new ArrayList<GeoResource>();
		list.add(resource);
		mapPresenter.drawGeoResouces(list, style);
		routesWidget.setWidget(row, 0, new Image(GWT.getModuleBaseURL()+style.getImageURL()));
		routesWidget.setWidget(row, 1, anchorContainer);
		routesWidget.getCellFormater().setWidth(row, 1, "100%");
		routesWidget.getCellFormater().setHeight(row, 1, "100%");
		Button button = new Button();
		button.setSize("32px", "28px");
		button.getElement().appendChild(new Image(browserResources.eraserIcon()).getElement());
		DOM.setStyleAttribute(button.getElement(),"left" , "0px");
		routesWidget.setWidget(row, 2, button);
		ClickHandler handler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				removePoint(this);
			}
		};
		button.addClickHandler(handler);
		relationHandler.put(handler, new PanelWithGeoResourceGeometry(geoResourceGeometry,row));
	}
	private void resizeRoutesWidget(){
		int maxPixelSize=500;
		if(geoprocessingPresenterDisplay!=null){
			maxPixelSize=geoprocessingPresenterDisplay.getContentHeight();
			if(geoprocessingPresenterDisplay.getContentWidth()!=0){
				scrollPanel.setWidth(geoprocessingPresenterDisplay.getContentWidth()-10+"px");
			}
		}
		maxPixelSize-=addPointMessage.getOffsetHeight()+gridSearchTextAndAddButton.getOffsetHeight()+traceRoute.getOffsetHeight()+optionsDisPanel.getOffsetHeight();
		maxPixelSize-=100;
		if(maxPixelSize<0){
			maxPixelSize=0;
		}
		routesWidget.resizeHeight(minPixelHeightOfRoutesWidget,maxPixelSize);
	}
	private void openMainPopup(int type){
		int width=3;
		int height=2;
		if(dashboardPresenter!=null){
			width=dashboardPresenter.getDisplay().getMapPanel().getOffsetWidth();
			height=dashboardPresenter.getDisplay().getMapPanel().getOffsetHeight();
		}
		width=width/3;
		height=height/2;
		if(width<500){
			width=500;
		}
		if(height<410){
			height=410;
		}
		PopupGeoprocessingView popup=new PopupGeoprocessingView(width,height,dashboardPresenter,browserMessages,browserResources,eventBus,GeoprocessingType.Route);
		if(type==0){
			popup.search(searchTextBox.getText());
		}else{
			popup.search(searchTextBox1.getText());
		}
		if(width>500){
			width=width-20;
		}
		dashboardPresenter.getDisplay().setMainPopup(width,height ,popup,"Geoprocessing");
	}

	private void doSelectedView() {
		geoprocessingPresenterDisplay.doSelectedView(this);
	}
	
	@SuppressWarnings("unchecked")
	private void executeGoogleDirections(){
		DirectionsService service=DirectionsService.newInstance();
		DirectionsRequest request = DirectionsRequest.newInstance();
		if(route.size()<2){
			widgetFactory.getDialogBox().showError(browserMessages.error2OrMorePoints());
			mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
			mapPresenter.getDisplay().stopProcessing();
			return;
		}
		JsArray<DirectionsWaypoint> waypointsJsArray = (JsArray<DirectionsWaypoint>) (DirectionsWaypoint.createArray());
		for(int i=0;i<route.size();i++){
			GeoResourceGeometry geoResourceGeometry = route.get(i);
			Collection<Point> collection = geoResourceGeometry.getGeometry().getPoints();
			Point[] array = collection.toArray(new Point[geoResourceGeometry.getGeometry().getPoints().size()]);
			for(int j=0;j<array.length;j++){
				Point point=array[j];
				LonLat openPoint= OpenLayersAdapter.getLatLng(point);
				openPoint.transform(point.getProjection(), "EPSG:4326");
				LatLng latLng= LatLng.newInstance(openPoint.lat(),openPoint.lon());
				DirectionsWaypoint waypoint=DirectionsWaypoint.newInstance();
				waypoint.setLocation(latLng);
				if(i==0 && j==0){
					request.setOrigin(latLng);
				} else if(i==route.size()-1 && j==array.length-1){
					request.setDestination(latLng);
				}else{
					waypointsJsArray.push(waypoint);
				}
				
			}
		}
		request.setTravelMode(travelMode);
		request.setProvideRouteAlternatives(routeAlternatives.getBoolean());
		if(travelMode==TravelMode.DRIVING){
			request.setAvoidHighways(avoidHighways.getBoolean());
			request.setAvoidTolls(avoidTolls.getBoolean());
		}else{
			request.setAvoidHighways(true);
			request.setAvoidTolls(true);
		}
		request.setOptimizeWaypoints(optimizeWaypoints.getBoolean());
		request.setWaypoints(waypointsJsArray);
		service.route(request, new DirectionsResultHandler() {
			@Override
			public void onCallback(DirectionsResult result,
					DirectionsStatus status) {
					googleCallback(result, status);
			}
		});
	}
	private void googleCallback(DirectionsResult result,
			DirectionsStatus status){
		switch (status) {
		case OK:
			initRoute(result);
			return;
		case ZERO_RESULTS:
			dashboardPresenter.getDisplay().setMainPopup(200,150,getPopupWidget(browserMessages.zeroResults()),"Center");
			break;
		case MAX_WAYPOINTS_EXCEEDED:
			dashboardPresenter.getDisplay().setMainPopup(200,150,getPopupWidget(browserMessages.maxWaypointsExceeded()),"Center");
			break;
		case OVER_QUERY_LIMIT:
			dashboardPresenter.getDisplay().setMainPopup(200,150,getPopupWidget(browserMessages.overQueryLimit()),"Center");
			break;
		case UNKNOWN_ERROR:
			dashboardPresenter.getDisplay().setMainPopup(200,150,getPopupWidget(browserMessages.unknownError()),"Center");
			break;
		case REQUEST_DENIED:
			dashboardPresenter.getDisplay().setMainPopup(200,150,getPopupWidget(browserMessages.requestDenied()),"Center");
			break;
		default:
			widgetFactory.getDialogBox().showError(browserMessages.errorGoogleDirections(status.toString()));
			break;
		}
		mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
		mapPresenter.getDisplay().stopProcessing();
		if(routeDescriptionWidget!=null){
			resultsPresenter.removeWidget(routeDescriptionWidget);
			routeDescriptionWidget=null;
		}
	}
	private void initRoute(DirectionsResult result){
		if(routeDescriptionWidget==null){
			routeDescriptionWidget=new RoutesDescriptionWidget(result.getRoutes(),route,routeSelectedCallback,browserMessages);
		}else{
			resultsPresenter.removeWidget(routeDescriptionWidget);
			routeDescriptionWidget=new RoutesDescriptionWidget(result.getRoutes(), route,routeSelectedCallback, browserMessages);
		}
		resultsPresenter.addWidget(routeDescriptionWidget, browserMessages.routes());
		resultsPresenter.doSelectedWidget(routeDescriptionWidget);
		dashboardPresenter.getDisplay().doSelectedWestWidget(resultsPresenter.getDisplay().asWidget());
		mapPresenter.getDisplay().stopProcessing();		
	}
	private void drawSelectedRoute(DirectionsRoute directionRoute){
		mapPresenter.getDisplay().removePointsStyle(new DrawPointStyle(DrawPointStyle.Style.ROUTES));
		List<Point> points=new ArrayList<Point>();
		for(int j=0;j<directionRoute.getOverview_Path().length();j++){
			Double lat=directionRoute.getOverview_Path().get(j).getLatitude();
			Double lng=directionRoute.getOverview_Path().get(j).getLongitude();
			points.add(new PointBean("", lng, lat,"EPSG:4326"));
		}	
		Geometry geometry = new PolyLineBean(null, points);
		GeoResource geoResource = new GeoResource(null,geometry);
		List<GeoResource> list=new ArrayList<GeoResource>();
		list.add(geoResource);
		mapPresenter.drawGeoResouces(list,new DrawPointStyle(DrawPointStyle.Style.ROUTES));
		if(points.size()>=2){
			mapPresenter.getDisplay().changeZoom(points);
		}
	}
	private Widget getPopupWidget(String error){
		VerticalPanel panel=new VerticalPanel();
		panel.setSize("100%", "100%");
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Label errorLabel=new Label(error);
		errorLabel.setHeight("70px");
		DOM.setStyleAttribute(errorLabel.getElement(),"textAlign", "Center");
		panel.add(errorLabel);
		panel.setCellVerticalAlignment(errorLabel, HasVerticalAlignment.ALIGN_TOP);
		Button button=new Button(browserMessages.close());
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				dashboardPresenter.getDisplay().closeMainPopup();
			}
		});
		panel.add(button);
		panel.setCellVerticalAlignment(button, HasVerticalAlignment.ALIGN_BOTTOM);
		return panel;
	}
	@Override
	public void resize() {
		onResize();
	}
	private class BooleanBoxChangeHandler implements ChangeHandler{
		private ListBox listBox;
		private String valueTrue;
		private MyBoolean toChange;
		public BooleanBoxChangeHandler(ListBox listBox,String valueTrue,MyBoolean toChange){
			this.listBox=listBox;
			this.valueTrue=valueTrue;
			this.toChange=toChange;
		}
		@Override
		public void onChange(ChangeEvent event) {
			if(listBox.getItemText(listBox.getSelectedIndex()).equals(valueTrue)){
				this.toChange.setBoolean(true);
			}else{
				this.toChange.setBoolean(false);
			}
		}

	}
	private class MyBoolean{
		private boolean bool;
		public MyBoolean(boolean bool){
			this.bool=bool;
		}
		public boolean getBoolean() {
			return bool;
		}
		public void setBoolean(boolean bool) {
			this.bool = bool;
		}	
	}
}
