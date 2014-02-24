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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.Size;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.MapMoveListener;
import org.gwtopenmaps.openlayers.client.event.MapZoomListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureUnselectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Geometry;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.LinearRing;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.popup.Popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.style.StyleMapShape;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.share.Circle;
import es.upm.fi.dia.oeg.map4rdf.share.OpenLayersAdapter;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;

/**
 * @author Alexander De Leon
 */
public class OpenLayersMapLayer implements MapLayer,
		VectorFeatureSelectedListener, VectorFeatureUnselectedListener,
		MapMoveListener {

	private static final int CIRCLE_NUMBER_OF_POINTS = 20;
	private final Vector vectorLayer;
	private FlowPanel popupPanel;

	private final Set<VectorFeature> features = new HashSet<VectorFeature>();
	private final OpenLayersMapView owner;
	private final Map map;
	private final java.util.Map<String, List<ClickHandler>> handlers = new HashMap<String, List<ClickHandler>>();
	private final BrowserResources browserResources;
	private List<VectorFeature> polylines;
	private java.util.Map<DrawPointStyle.Style, List<VectorFeature>> points;
	//private java.util.Map<VectorFeature, org.gwtopenmaps.openlayers.client.geometry.Point> allPoints;

	public OpenLayersMapLayer(OpenLayersMapView owner, Map map, String name,
			BrowserResources browserResources) {
		this.owner = owner;
		this.map = map;
		this.browserResources = browserResources;
		polylines = new ArrayList<VectorFeature>();
		VectorOptions vectorOptions = new VectorOptions();
		// VectorOptions vectorBckgOptions = new VectorOptions();

		vectorLayer = new Vector(name + "_vectors", vectorOptions);
		vectorLayer.setDisplayInLayerSwitcher(false);
		map.addLayer(vectorLayer);
		map.addMapMoveListener(this);
		points = new HashMap<DrawPointStyle.Style, List<VectorFeature>>();
		for (DrawPointStyle.Style i : DrawPointStyle.Style.values()) {
			points.put(i, new ArrayList<VectorFeature>());
		}
		//allPoints = new HashMap<VectorFeature, org.gwtopenmaps.openlayers.client.geometry.Point>();
	}

	@Override
	public HasClickHandlers draw(Point point, DrawPointStyle pointStyle) {
		LonLat ll = new LonLat(point.getX(), point.getY());
		ll.transform(point.getProjection(), map.getProjection());
		org.gwtopenmaps.openlayers.client.geometry.Point olPoint = new org.gwtopenmaps.openlayers.client.geometry.Point(
				ll.lon(), ll.lat());
		return addFeature(olPoint, getStyle(olPoint, pointStyle), pointStyle);
	}
	@Override
	public HasClickHandlers drawPolygon(StyleMapShape<Polygon> polygon, DrawPointStyle pointStyle) {
		List<Point> points = polygon.getMapShape().getPoints();
		List<Point> tranformedPoints = new ArrayList<Point>();
		for (Point p : points) {
			LonLat ll = new LonLat(p.getX(), p.getY());
			ll.transform(p.getProjection(), map.getProjection());
			tranformedPoints.add(new PointBean(p.getUri(), ll.lon(), ll.lat()));
		}
		LinearRing ring = new LinearRing(getPoints(tranformedPoints));
		return addFeature(
				new org.gwtopenmaps.openlayers.client.geometry.Polygon(
						new LinearRing[] { ring }), getStyle(polygon), pointStyle);
	}

	@Override
	public HasClickHandlers drawPolyline(StyleMapShape<PolyLine> polyline, DrawPointStyle pointStyle) {
		List<Point> points = polyline.getMapShape().getPoints();
		List<Point> tranformedPoints = new ArrayList<Point>();
		for (Point p : points) {
			LonLat ll = new LonLat(p.getX(), p.getY());
			ll.transform(p.getProjection(), map.getProjection());
			tranformedPoints.add(new PointBean(p.getUri(), ll.lon(), ll.lat()));
		}
		LineString lineString = new LineString(getPoints(tranformedPoints));
		return addFeature(lineString, getStyle(polyline), pointStyle);
	}

	@Override
	public HasClickHandlers drawCircle(StyleMapShape<Circle> circle) {
		org.gwtopenmaps.openlayers.client.geometry.Point[] circlePoints = new org.gwtopenmaps.openlayers.client.geometry.Point[CIRCLE_NUMBER_OF_POINTS];

		double EARTH_RADIUS = 6371000;
		double d = circle.getMapShape().getRadius() / EARTH_RADIUS;
		double lat1 = Math.toRadians(circle.getMapShape().getCenter().getY());
		double lng1 = Math.toRadians(circle.getMapShape().getCenter().getX());

		double a = 0;
		double step = 360.0 / CIRCLE_NUMBER_OF_POINTS;
		for (int i = 0; i < CIRCLE_NUMBER_OF_POINTS; i++) {
			double tc = Math.toRadians(a);
			double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d)
					+ Math.cos(lat1) * Math.sin(d) * Math.cos(tc));
			double lng2 = lng1
					+ Math.atan2(Math.sin(tc) * Math.sin(d) * Math.cos(lat1),
							Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));
			circlePoints[i] = new org.gwtopenmaps.openlayers.client.geometry.Point(
					Math.toDegrees(lng2), Math.toDegrees(lat2));
			LonLat ll = new LonLat(circlePoints[i].getX(),
					circlePoints[i].getY());
			ll.transform(circle.getMapShape().getCenter().getProjection(), map.getProjection());
			circlePoints[i] = new org.gwtopenmaps.openlayers.client.geometry.Point(
					ll.lon(), ll.lat());
			a += step;
		}
		LinearRing ring = new LinearRing(circlePoints);
		return addFeature(
				(new org.gwtopenmaps.openlayers.client.geometry.Polygon(
						new LinearRing[] { ring })), getStyle(circle), null);
	}

	@Override
	public HasClickHandlers drawCircle(StyleMapShape<Circle> circle, String text) {
		org.gwtopenmaps.openlayers.client.geometry.Point[] circlePoints = new org.gwtopenmaps.openlayers.client.geometry.Point[CIRCLE_NUMBER_OF_POINTS];

		double EARTH_RADIUS = 6371000;
		double d = circle.getMapShape().getRadius() / EARTH_RADIUS;
		double lat1 = Math.toRadians(circle.getMapShape().getCenter().getY());
		double lng1 = Math.toRadians(circle.getMapShape().getCenter().getX());

		double a = 0;
		double step = 360.0 / CIRCLE_NUMBER_OF_POINTS;
		for (int i = 0; i < CIRCLE_NUMBER_OF_POINTS; i++) {
			double tc = Math.toRadians(a);
			double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d)
					+ Math.cos(lat1) * Math.sin(d) * Math.cos(tc));
			double lng2 = lng1
					+ Math.atan2(Math.sin(tc) * Math.sin(d) * Math.cos(lat1),
							Math.cos(d) - Math.sin(lat1) * Math.sin(lat2));
			circlePoints[i] = new org.gwtopenmaps.openlayers.client.geometry.Point(
					Math.toDegrees(lng2), Math.toDegrees(lat2));
			LonLat ll = new LonLat(circlePoints[i].getX(),
					circlePoints[i].getY());
			ll.transform(circle.getMapShape().getCenter().getProjection(), map.getProjection());
			circlePoints[i] = new org.gwtopenmaps.openlayers.client.geometry.Point(
					ll.lon(), ll.lat());
			a += step;
		}
		LinearRing ring = new LinearRing(circlePoints);
		Style style = getStyle(circle);
		style.setLabel(text);
		return addFeature(
				(new org.gwtopenmaps.openlayers.client.geometry.Polygon(
						new LinearRing[] { ring })), style, null);
	}

	@Override
	public PopupWindow createPopupWindow() {

		return new PopupWindow() {
			private final FlowPanel panel = new FlowPanel();
			private Popup popup;
			/*private int width=316;
			private int height=378;*/
			private int width=106;
			private int height=106;
			MapZoomListener zoomListener;

			@Override
			public boolean remove(Widget w) {
				return panel.remove(w);
			}

			@Override
			public Iterator<Widget> iterator() {
				return panel.iterator();
			}

			@Override
			public void clear() {
				panel.clear();
			}

			@Override
			public void add(Widget w) {
				panel.add(w);
			}

			@Override
			public void open(Point location) {
				MapZoomListener zoomListener = new MapZoomListener() {
					@Override
					public void onMapZoom(MapZoomEvent eventObject) {
						
						if(getPopupLeft()!=null && getPopupLeft()!="" && !getPopupLeft().isEmpty() && getPopupTop()!=null && getPopupTop()!="" && !getPopupTop().isEmpty()){
							int popupLeft=Integer.parseInt(getPopupLeft().replace("px", ""));
							int popupTop=Integer.parseInt(getPopupTop().replace("px", ""));
						DOM.setStyleAttribute(popupPanel.getElement(), "left",
								String.valueOf(popupLeft-(width/2))+"px");// + "px");
						DOM.setStyleAttribute(popupPanel.getElement(), "top",
								String.valueOf(popupTop-(height/2))+"px");// + "px");
						}
					}
				};
				map.addMapZoomListener(zoomListener);
				LonLat popupPosition = OpenLayersAdapter.getLatLng(location);
				popupPosition.transform(location.getProjection(), map.getProjection());
				popup = new Popup("exclusive-mapresources-popup",
						popupPosition, new Size(width, height),
						DOM.getInnerHTML(panel.getElement()), false);
				//popup.setBorder("1px solid #424242");

				map.addPopupExclusive(popup);
				popupPanel = new FlowPanel();
				popupPanel.setSize(width+"px", height+"px");
				popupPanel.add(panel);
				//popupPanel.setStyleName(browserResources.css().popup());

				owner.getContainer().add(popupPanel);
				if(getPopupLeft()!=null && getPopupLeft()!="" && getPopupTop()!=null && getPopupTop()!=""){
					int popupLeft=Integer.parseInt(getPopupLeft().replace("px", ""));
					int popupTop=Integer.parseInt(getPopupTop().replace("px", ""));
				
					DOM.setStyleAttribute(popupPanel.getElement(), "position",
						"absolute");
					DOM.setStyleAttribute(popupPanel.getElement(), "left",
						String.valueOf(popupLeft-(width/2))+"px");// + "px");
					DOM.setStyleAttribute(popupPanel.getElement(), "top",
						String.valueOf(popupTop-(height/2))+"px");// + "px");
				}
				DOM.setStyleAttribute(popupPanel.getElement(), "zIndex", "2024");
				DOM.setElementAttribute(popupPanel.getElement(), "id",
						"map4rdf-popup-new");

				replace();
			}

			@Override
			public void close() {
				if (popup != null) {
					map.removePopup(popup);
					owner.getContainer().remove(popupPanel);
					map.removeListener(zoomListener);
				}
			}
		};
	}

	// set
	native String getPopupLeft() /*-{
		if($wnd.document.getElementById("exclusive-mapresources-popup")!=null){
			var e = $wnd.document.getElementById("exclusive-mapresources-popup").style.left;
			return e;
		} else {
			return "";
		}
	}-*/;

	native String getPopupTop() /*-{
		if($wnd.document.getElementById("exclusive-mapresources-popup")!=null){
			var e = $wnd.document.getElementById("exclusive-mapresources-popup").style.top;
			return e;
		} else {
			return "";
		}
	}-*/;

	native void replace() /*-{
		if($wnd.document.getElementById("exclusive-mapresources-popup")!=null && $wnd.document.getElementById("map4rdf-popup-new")!=null){
			var element = $wnd.document
				.getElementById("exclusive-mapresources-popup");
			var parent = element.parentNode;
			var newElement = $wnd.document.getElementById("map4rdf-popup-new");
			parent.appendChild(newElement);
			element.style.height = "0px";
			element.style.width = "0px";
		}
		return;
	}-*/;

	@Override
	public void clear() {
		for (VectorFeature feature : features) {
			vectorLayer.removeFeature(feature);
		}
		features.clear();
		removePolylines();
		//allPoints.clear();
		for (List<VectorFeature> i : points.values()) {
			i.clear();
		}
	}

	@Override
	public MapView getMapView() {
		return owner;
	}

	public class FeatureHasClickHandlerWrapper implements HasClickHandlers {

		private final String featureId;

		private FeatureHasClickHandlerWrapper(String featureId) {
			this.featureId = featureId;
		}

		@Override
		public void fireEvent(GwtEvent<?> event) {
			// ignore
		}

		@Override
		public HandlerRegistration addClickHandler(final ClickHandler handler) {
			List<ClickHandler> clickHandlers = handlers.get(featureId);
			if (clickHandlers == null) {
				clickHandlers = new ArrayList<ClickHandler>();
				handlers.put(featureId, clickHandlers);
			}
			final List<ClickHandler> fClickHandlers = clickHandlers;
			clickHandlers.add(handler);
			return new HandlerRegistration() {

				@Override
				public void removeHandler() {
					fClickHandlers.remove(handler);
				}
			};
		}
	}

	void bind() {
		vectorLayer.addVectorFeatureSelectedListener(this);
		vectorLayer.addVectorFeatureUnselectedListener(this);
		SelectFeature selectFeature = new SelectFeature(vectorLayer);
		selectFeature.setClickOut(false);
		selectFeature.setToggle(true);
		selectFeature.setMultiple(false);
		map.addControl(selectFeature);
		selectFeature.activate();
	}

	/* ------------------------- helper methods -- */
	private org.gwtopenmaps.openlayers.client.geometry.Point[] getPoints(
			List<Point> points) {
		org.gwtopenmaps.openlayers.client.geometry.Point[] pointsArray = new org.gwtopenmaps.openlayers.client.geometry.Point[points
				.size()];
		int index = 0;
		for (Point p : points) {
			pointsArray[index++] = new org.gwtopenmaps.openlayers.client.geometry.Point(
					p.getX(), p.getY());
		}
		return pointsArray;
	}

	private HasClickHandlers addFeature(Geometry geometry, Style style,
			DrawPointStyle pointStyle) {
		VectorFeature feature = new VectorFeature(geometry);
		if (style != null) {
			feature.setStyle(style);
			feature.getStyle().setGraphicZIndex(0);
		}
		String featureId = DOM.createUniqueId();
		feature.getAttributes().setAttribute("map4rdf_id", featureId);
		if (geometry instanceof LineString) {
			if(pointStyle!=null){
				points.get(pointStyle.getStyle()).add(feature);
			}else{
				polylines.add(feature);
			}
		}
		if(geometry instanceof org.gwtopenmaps.openlayers.client.geometry.Polygon){
			if(pointStyle!=null){
				points.get(pointStyle.getStyle()).add(feature);
			}else{
				points.get(DrawPointStyle.getDefaultStyle()).add(feature);
			}
		}
		if (geometry instanceof org.gwtopenmaps.openlayers.client.geometry.Point) {
			if (pointStyle != null) {
				points.get(pointStyle.getStyle()).add(feature);
			} else {
				points.get(DrawPointStyle.getDefaultStyle()).add(feature);
			}
			//allPoints.put(feature,
						//	(org.gwtopenmaps.openlayers.client.geometry.Point) geometry);
		}
		vectorLayer.addFeature(feature);
		features.add(feature);
		//vectorLayer.drawFeature(feature, style);
		return new FeatureHasClickHandlerWrapper(featureId);
	}

	private void removeFeature(VectorFeature feature) {
		vectorLayer.removeFeature(feature);
		features.remove(feature);
	}

	private Style getStyle(StyleMapShape<?> styleMapShape) {
		Style style = new Style();
		style.setFillColor(styleMapShape.getFillColor());
		style.setFillOpacity(styleMapShape.getFillOpacity());
		style.setStrokeColor(styleMapShape.getStrokeColor());
		style.setStrokeOpacity(styleMapShape.getStrokeOpacity());
		style.setStrokeWidth(styleMapShape.getStrokeWidth());
		style.setCursor("pointer");
		return style;

	}

	private Style getStyle(
			org.gwtopenmaps.openlayers.client.geometry.Point olPoint,
			DrawPointStyle pointStyle) {
		Style style = new Style();
		/*
		 * switch (pointStyle) { case BLUE:
		 * style.setExternalGraphic(GWT.getModuleBaseURL() + MARKER_BLUE_ICON);
		 * break; default: style.setExternalGraphic(GWT.getModuleBaseURL() +
		 * MARKER_RED_ICON); break; }
		 */
		style.setExternalGraphic(GWT.getModuleBaseURL()
				+ pointStyle.getImageURL());
		style.setGraphicOffset(pointStyle.getDesplaceOffsetX(), pointStyle.getDesplaceOffsetY());
		// style.setGraphicSize(24, 21);
		style.setGraphicSize(pointStyle.getWidth(), pointStyle.getHeight());
		style.setFillOpacity(1);
		style.setCursor("pointer");
		return style;
	}

	private Style getTextStyle(String text) {
		Style style = new Style();
		style.setLabel(text);
		style.setCursor("pointer");
		style.setPointRadius(20);
		return style;
	}

	@Override
	public void onFeatureSelected(FeatureSelectedEvent eventObject) {
		List<ClickHandler> clickHandlers = handlers.get(eventObject
				.getVectorFeature().getAttributes()
				.getAttributeAsString("map4rdf_id"));
		if (clickHandlers != null) {
			for (ClickHandler handler : clickHandlers) {
				handler.onClick(null);
			}
		}
	}

	@Override
	public void onFeatureUnselected(FeatureUnselectedEvent eventObject) {
		//getMapView().closeWindow();
		
		//Disabled because the new GUI do auto close without click on the image of last resource.
		
		/*List<ClickHandler> clickHandlers = handlers.get(eventObject
				.getVectorFeature().getAttributes()
				.getAttributeAsString("map4rdf_id"));
		if (clickHandlers != null) {
			for (ClickHandler handler : clickHandlers) {
				handler.onClick(null);
			}
		}*/
		
	}

	@Override
	public void onMapMove(MapMoveEvent eventObject) {
		return;
	}

	@Override
	public void removePointsStyle(DrawPointStyle pointStyle) {
		
		if (pointStyle != null) {
			for (VectorFeature i : points.get(pointStyle.getStyle())) {
				removeFeature(i);
				//allPoints.remove(i);
			}
			points.get(pointStyle.getStyle()).clear();
		} else {
			for (VectorFeature i : points.get(DrawPointStyle.getDefaultStyle())) {
				removeFeature(i);
				//allPoints.remove(i);
			}
			points.get(DrawPointStyle.getDefaultStyle()).clear();
		}
	}

	@Override
	public void removePolylines() {
		
		for (VectorFeature i : polylines) {
			removeFeature(i);
		}
		polylines.clear();
	}

	

}
