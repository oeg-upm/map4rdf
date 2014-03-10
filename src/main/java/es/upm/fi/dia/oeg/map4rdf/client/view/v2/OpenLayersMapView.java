/**
 * Copyright (c) 2011 Alexander De Leon Battista
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import java.util.ArrayList;
import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.Polygon;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfigurationResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParameters;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParametersResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetReloadEvent;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBoxBean;
import es.upm.fi.dia.oeg.map4rdf.share.MapConfiguration;
import es.upm.fi.dia.oeg.map4rdf.share.OpenLayersAdapter;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinateBean;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;

/**
 * @author Alexander De Leon
 */
public class OpenLayersMapView implements MapView {

	/**
	 * By default the map is centered in Puerta del Sol, Madrid
	 */
	private static LonLat DEFAULT_CENTER = new LonLat(-3.703637, 40.416645);
	private static int DEFAULT_ZOOM_LEVEL = 6;
	//If you change the next variable please change the method: CreateAsyncUI(Map<String,String parameters)
	private static final String[] getParameters={ParameterNames.MAP_DEFAULT_CENTER,ParameterNames.DEFAULT_PROJECTION,ParameterNames.SPHERICAL_MERCATOR,ParameterNames.MAP_ZOOM_LEVEL};
	private final LoadingWidget loadingWidget;
	private final BrowserResources browserResources;
	private Map map;
	private MapWidget mapWidget;
	private final OpenLayersMapLayer defaultLayer;
	private AbsolutePanel panel;
	private LayerSwitcher layerSwitcher;
	private EventBus eventBus;
	private String defaultProjection;
	private DispatchAsync dispatchAsync;
	// drawing
	private FilterAreaLayer filterAreaLayer;
	private WidgetFactory widgetFactory;
	
	public OpenLayersMapView(final WidgetFactory widgetFactory, DispatchAsync dispatchAsync,EventBus eventBus,BrowserResources browserResources) {
		this.browserResources=browserResources;
		this.eventBus=eventBus;
		this.widgetFactory = widgetFactory;
		loadingWidget = widgetFactory.getLoadingWidget();
		createUi();
		defaultLayer = (OpenLayersMapLayer) createLayer("default");
		addNotice();
		filterAreaLayer = new FilterAreaLayer(map);
		List<String> toGet=new ArrayList<String>();
		for(String i:getParameters){
			toGet.add(i);
		}
		GetMultipleConfigurationParameters action = new GetMultipleConfigurationParameters(toGet);
		this.dispatchAsync=dispatchAsync;
		dispatchAsync.execute(action, new AsyncCallback<GetMultipleConfigurationParametersResult>() {

			@Override
			public void onFailure(Throwable caught) {
				widgetFactory.getDialogBox().showError("Mapview can't contact with server. Please contact with System Admin.");
			}

			@Override
			public void onSuccess(GetMultipleConfigurationParametersResult result) {
				createAsyncUi(result.getResults());
			}
		
		});
		
	}

	private void addNotice() {
	}

	@Override
	public Widget asWidget() {
		return panel;
	}

	@Override
	public void startProcessing() {
		loadingWidget.center();
	}

	@Override
	public void stopProcessing() {
		loadingWidget.hide();
	}

	@Override
	public TwoDimentionalCoordinate getCurrentCenter() {
		return OpenLayersAdapter.getTwoDimentionalCoordinate(map.getCenter());
	}

	@Override
	public BoundingBox getVisibleBox() {
		if (filterAreaLayer.getFilterVector() != null) {
			if (filterAreaLayer.getFilterVector().getNumberOfFeatures() > 0) {
				VectorFeature feature;
				feature = filterAreaLayer.getFilterVector().getFeatures()[0];
				Geometry g = feature.getGeometry();
				if (g.getClassName().equals(Geometry.POLYGON_CLASS_NAME)) {
					Polygon p = Polygon.narrowToPolygon(g.getJSObject());
					BoundingBox b = OpenLayersAdapter.getBoudingBox(p);
					b.transform(map.getProjection(), defaultProjection);
					return b;
				}
			}
		}
		BoundingBox box =  OpenLayersAdapter.getBoundingBox(map.getExtent());
		box.transform(map.getProjection(), defaultProjection);
		return box;
		// why??
		// return OpenLayersAdapter.getBoundingBox(map.getExtent());
	}

	@Override
	public void setVisibleBox(BoundingBox boundingBox) {
		BoundingBox box=new BoundingBoxBean(boundingBox.getBottomLeft(), boundingBox.getTopRight());
		box.transform(defaultProjection, map.getProjection());
		map.zoomToExtent(OpenLayersAdapter.getLatLngBounds(box));
	}

	@Override
	public MapLayer getDefaultLayer() {
		return defaultLayer;
	}

	@Override
	public MapLayer createLayer(String name) {
		// TODO save layer
		return new OpenLayersMapLayer(this, map, name,browserResources);
	}

	@Override
	public AbsolutePanel getContainer() {
		return panel;
	}

	/* ----------------------------- helper methods -- */
	
	private void createUi() {
		panel = new AbsolutePanel() {

			@Override
			protected void onLoad() {
				defaultLayer.bind();
			};
		};
		mapWidget = new MapWidget("100%", "100%", new MapOptions());
		map = mapWidget.getMap();
		layerSwitcher = new LayerSwitcher();
		map.addControl(layerSwitcher);
		panel.add(mapWidget);
		Image image=new Image(browserResources.refreshImage());
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new FacetReloadEvent());
			}
		});
		DOM.setStyleAttribute(image.getElement(), "position", "absolute");
		DOM.setStyleAttribute(image.getElement(), "top", "62px");
		DOM.setStyleAttribute(image.getElement(), "left", "8px");
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		DOM.setStyleAttribute(image.getElement(), "zIndex", "2080");
		panel.add(image);
		DOM.setStyleAttribute(panel.getElement(), "zIndex", "0");
	}
	
	private void createAsyncUi(java.util.Map<String,String> parameters){
		boolean errors=false;
		for(String i:getParameters){
			if(!parameters.containsKey(i)){
				widgetFactory.getDialogBox().showError("check "+i+" parameter in config file");
				errors=true;
			}
		}
		if(!errors){
			defaultProjection=parameters.get(ParameterNames.DEFAULT_PROJECTION);
			TwoDimentionalCoordinateBean.setDefaultProjection(defaultProjection);
			final String spherical_mercator=parameters.get(ParameterNames.SPHERICAL_MERCATOR);
			String centerString=parameters.get(ParameterNames.MAP_DEFAULT_CENTER);
			String[] centerStringSplit=centerString.split(",");
			if(centerStringSplit.length!=2){
				widgetFactory.getDialogBox().showError("Malformed "+ParameterNames.MAP_DEFAULT_CENTER+" parameter in config file. Please contact with system admin.");
			}
			try{
				DEFAULT_CENTER = new LonLat(Double.parseDouble(centerStringSplit[0]),Double.parseDouble(centerStringSplit[1]));
			}catch(Exception e){
				widgetFactory.getDialogBox().showError("Can't parse to double "+ParameterNames.MAP_DEFAULT_CENTER+" parameter in config file. Please contact with system admin.");
			}
			try{
				DEFAULT_ZOOM_LEVEL = Integer.parseInt(parameters.get(ParameterNames.MAP_ZOOM_LEVEL));
			}catch(Exception e){
				widgetFactory.getDialogBox().showError("Can't parse to int "+ParameterNames.MAP_ZOOM_LEVEL+" parameter in config file. Please contact with system admin.");
			}
			dispatchAsync.execute(new GetMapsConfiguration(), new AsyncCallback<GetMapsConfigurationResult>() {
				@Override
				public void onFailure(Throwable caught) {
					widgetFactory.getDialogBox().showError("Mapview can't contact with server to obtain Maps. Please contact with System Admin.");
				}
				@Override
				public void onSuccess(GetMapsConfigurationResult result) {
					if(spherical_mercator!=null && spherical_mercator.toLowerCase().equals("true")) {
						addSphericalMaps(result.getMapsConfiguration());
					} else {
						addFlatMaps(result.getMapsConfiguration());
					}
				}
			});
			
		}
	}
	@Override
	public void changeZoom(List<Point> points){
		if(points.size()>=2){
			map.zoomToExtent(getBoundsOfPoints(points));
		}
		//map.zoomToExtent(bound);
		//new Bounds(lowerLeftX, lowerLeftY, upperRightX, upperRightY);
	}
	private Bounds getBoundsOfPoints(List<Point> points){
		Bounds bounds;
		Double lowerLeftX = points.get(0).getX();
		Double lowerLeftY = points.get(0).getY();
		Double upperRightX = points.get(0).getX();
		Double upperRightY = points.get(0).getY();
		for(Point point:points){
			if(point.getX()<lowerLeftX){
				lowerLeftX=point.getX();
			}
			if(point.getY()<lowerLeftY){
				lowerLeftY=point.getY();
			}
			if(point.getX()>upperRightX){
				upperRightX=point.getX();
			}
			if(point.getY()>upperRightY){
				upperRightY=point.getY();
			}
		}
		LonLat lowerLeft = new LonLat(lowerLeftX, lowerLeftY);
		LonLat upperRight = new LonLat(upperRightX, upperRightY);
		lowerLeft.transform(defaultProjection, map.getProjection());
		upperRight.transform(defaultProjection, map.getProjection());
		bounds=new Bounds(lowerLeft.lon(), lowerLeft.lat(), upperRight.lon(), upperRight.lat());
		return bounds;
	}
	private void addSphericalMaps(List<MapConfiguration> maps){
		//needed constants
		Bounds bounds = new Bounds(-20037508.34, -20037508.34, 20037508.34,
				20037508.34);
		double[] resolutions = new double[] { 0.703125, 0.3515625, 0.17578125,
				0.087890625, 0.0439453125, 0.02197265625, 0.010986328125,
				0.0054931640625, 0.00274658203125, 0.001373291015625,
				0.0006866455078125, 0.00034332275390625, 0.000171661376953125,
				8.58306884765625e-005, 4.291534423828125e-005,
				2.1457672119140625e-005, 1.0728836059570313e-005,
				5.3644180297851563e-006, 2.6822090148925781e-006,
				1.3411045074462891e-006 };
		MapOptions options = new MapOptions();
		options.setMaxExtent(bounds);
		options.setProjection("EPSG:900913");
		options.setUnits("m");
		options.setMaxResolution((float) 156543.0339);
	
		//buliding maps
		map.setOptions(options);
		//building layers
		/*GoogleV3 googleLayer = LayersManager.getGoogleLayer(bounds);
		OSM openStreetMapsLayer = LayersManager.getOpenStreetMapsLayer(bounds);*/
		map.addLayers(LayersManager.getLayers(maps, bounds, resolutions));
		LonLat lonlat=new LonLat(DEFAULT_CENTER.lon(), DEFAULT_CENTER.lat());
		lonlat.transform(defaultProjection, map.getProjection());
		map.setCenter(lonlat, DEFAULT_ZOOM_LEVEL);
	}
	
	private void addFlatMaps(List<MapConfiguration> maps){
		//needed constants
		Bounds bounds = new Bounds(-20037508.34, -20037508.34, 20037508.34,
				20037508.34);
		double[] resolutions = new double[] { 0.703125, 0.3515625, 0.17578125,
				0.087890625, 0.0439453125, 0.02197265625, 0.010986328125,
				0.0054931640625, 0.00274658203125, 0.001373291015625,
				0.0006866455078125, 0.00034332275390625, 0.000171661376953125,
				8.58306884765625e-005, 4.291534423828125e-005,
				2.1457672119140625e-005, 1.0728836059570313e-005,
				5.3644180297851563e-006, 2.6822090148925781e-006,
				1.3411045074462891e-006 };
		
		MapOptions options = new MapOptions();
		options.setProjection("EPSG:4326");
		options.setResolutions(resolutions);
		options.setUnits("degrees");
		options.setMaxExtent(new Bounds(-180, -90, 180, 90));
		options.setMinExtent(new Bounds(-1, -1, 1, 1));
		//options.setNumZoomLevels(5);
		//buliding maps
		map.setOptions(options);
		//building layers
		/*WMS cartociudadLayer = LayersManager.getCartociudadLayer(resolutions);
		WMS otalexLayer = LayersManager.newIDEE(resolutions);
		//WMS olLayer = LayersManager.getOpenLayersFlatLayer();
		//WMS olBasicLayer = LayersManager.getOpenLayersFlatBasicLayer();*/
		map.addLayers(LayersManager.getLayers(maps, bounds, resolutions));
		LonLat lonlat=new LonLat(DEFAULT_CENTER.lon(), DEFAULT_CENTER.lat());
		lonlat.transform(defaultProjection, map.getProjection());
		map.setCenter(lonlat, DEFAULT_ZOOM_LEVEL);
	}
	
	@Override
	public void closeWindow() {
	}
	
	//filter area
	// presenter
	public Vector getFilterVector() {
		return filterAreaLayer.getFilterVector();
	}

	public void setAreaFilterDrawing(Boolean value) {
		filterAreaLayer.setAreaFilterDrawing(value);
	}

	public void clearAreaFilterDrawing() {
		filterAreaLayer.clearAreaFilterDrawing();
	}
	
}	
