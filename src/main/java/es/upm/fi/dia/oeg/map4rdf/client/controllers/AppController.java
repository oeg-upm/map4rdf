/**
 * Copyright (c) 2010 Alexander De Leon Battista
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
package es.upm.fi.dia.oeg.map4rdf.client.controllers;

import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceChangedEvent;
import net.customware.gwt.presenter.client.place.PlaceChangedHandler;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.place.PlaceRequestEvent;
import net.customware.gwt.presenter.client.place.PlaceRequestHandler;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;
import net.customware.gwt.presenter.client.widget.WidgetPresenter;

import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.event.UrlParametersChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.place.Place;

/**
 * @author Filip
 */
public class AppController extends WidgetPresenter<AppController.Display> implements PlaceRequestHandler,PlaceChangedHandler {

	public interface Display extends WidgetDisplay {
		void setContent(Widget widget);
	}

	//private final Set<WidgetPresenter<?>> presenters;
	private Map<String,WidgetPresenter<?>> presenters;
	private EventBus eventBus;
	public AppController(Display display, EventBus eventBus) {
		super(display, eventBus);
		presenters = new HashMap<String,WidgetPresenter<?>>();
		this.eventBus=eventBus;
		eventBus.addHandler(PlaceRequestEvent.getType(), this);
		eventBus.addHandler(PlaceChangedEvent.getType(), this);
	}
    

	public void addPresenter(WidgetPresenter<?> presenter,Place place) {
		presenters.put(place.getName(), presenter);
	}

	public void removePresenter(WidgetPresenter<?> presenter) {
		presenters.remove(presenter);
	}
	@Override
	public void onPlaceRequest(PlaceRequestEvent event) {
    	Place place = getPlaceFromQueryString(event);
    	HashMap<String, String> myMap = getParamtersMap(event);
		if (place == null) {
			return;
		}
        for (String namePlace: presenters.keySet()) {
			if (place.getName()!=null && !place.getName().isEmpty() && place.getName().equals(namePlace)) {
				if (myMap != null) {
					UrlParametersChangeEvent parametersChangeEvent = new UrlParametersChangeEvent(myMap);
					eventBus.fireEvent(parametersChangeEvent);
				}
                getDisplay().setContent(presenters.get(namePlace).getDisplay().asWidget());
				break;
			}
		}
	}

	/* ----- Presenter API -- */
	public Place getPlace() {
		// This is the default place
		return Places.DEFAULT;
	}

	@Override
	protected void onBind() {
		// bind children
		for (WidgetPresenter<?> presenter : presenters.values()) {
			presenter.bind();
		}
	}

	protected void onPlaceRequest(PlaceRequest request) {
		// empty
	}

	@Override
	protected void onUnbind() {
		// unbind children
		for (WidgetPresenter<?> presenter : presenters.values()) {
			presenter.unbind();
		}
	}
	private Place getPlaceFromQueryString(PlaceRequestEvent event){
		PlaceRequest placeRequest = event.getRequest();
		String originRequestAddress = placeRequest.toString();
		String address = "";
		
		if(originRequestAddress.contains("?")) {
			address = originRequestAddress.split("\\?")[0];
			return new Place(address);
		}
		
		return new Place(placeRequest.getName());
	}
	private HashMap<String, String> getParamtersMap(PlaceRequestEvent event){
		PlaceRequest placeRequest = event.getRequest();
		String originRequestAddress = placeRequest.toString();
		HashMap<String,String> paramsMap= new HashMap<String, String>();
		try {
			if(originRequestAddress.contains("?")) {
				String paramsString = originRequestAddress.split("\\?")[1];
				String[] map = paramsString.split(";");
				for(String pair : map) {
					paramsMap.put(pair.split("=")[0], pair.split("=")[1]);
				}
				return paramsMap;
			}
		} catch (IndexOutOfBoundsException e) {
			
		}
		return null;
	}


	@Override
	protected void onRevealDisplay() {
		
	}


	@Override
	public void onPlaceChanged(PlaceChangedEvent event) {
    	Place place = new Place(event.getPlace().getName());
        for (String namePlace: presenters.keySet()) {
			if (place.getName()!=null && !place.getName().isEmpty() && place.getName().equals(namePlace)) {
                getDisplay().setContent(presenters.get(namePlace).getDisplay().asWidget());
				break;
			}
		}
	}

}
