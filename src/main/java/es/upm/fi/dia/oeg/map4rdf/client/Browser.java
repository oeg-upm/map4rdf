/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informatica, Universidad 
 * Politecnica de Madrid, Spain
 * 
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
package es.upm.fi.dia.oeg.map4rdf.client;

import java.util.HashMap;

import net.customware.gwt.presenter.client.place.PlaceChangedEvent;
import net.customware.gwt.presenter.client.place.PlaceManager;
import net.customware.gwt.presenter.client.place.DefaultPlaceManager;
import net.customware.gwt.presenter.client.place.PlaceRequest;
import net.customware.gwt.presenter.client.place.TokenFormatException;
import net.customware.gwt.presenter.client.place.TokenFormatter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import es.upm.fi.dia.oeg.map4rdf.client.controllers.AppController;
import es.upm.fi.dia.oeg.map4rdf.client.event.LoadResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.inject.Injector;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;

/**
 *
 * 
 * @author Alexander De Leon
 */
public class Browser implements EntryPoint {
	Injector injector = null;
	@Override
	public void onModuleLoad() {
		try {
		injector = GWT.create(Injector.class);
		} catch (Exception e) {
			injector = null;
			e.printStackTrace();
			//System.err.println(e);
		}
		AppController controller = new AppController(injector.getBrowserUi(), injector.getEventBus());
		controller.addPresenter(injector.getDashboard(),Places.DASHBOARD);
		
		controller.bind();
		
		RootLayoutPanel.get().add(controller.getDisplay().asWidget());
		TokenFormatter tokenFormatter = new TokenFormatter() {
			private HashMap<String, PlaceRequest> tokenPlaces=new HashMap<String,PlaceRequest>();
			private HashMap<PlaceRequest, String> placeTokens=new HashMap<PlaceRequest,String>();
			@Override
			public PlaceRequest toPlaceRequest(String token)
					throws TokenFormatException {
				
				PlaceRequest toReturn;
				if(tokenPlaces.containsKey(token)){
					toReturn=tokenPlaces.get(token);
				}else{
					toReturn = new PlaceRequest(token);
					tokenPlaces.put(token, toReturn);
					placeTokens.put(toReturn, token);
				}
				return toReturn;
			}
			
			@Override
			public String toHistoryToken(PlaceRequest placeRequest)
					throws TokenFormatException {
				String token;
				if(placeTokens.containsKey(placeRequest)){
					token=placeTokens.get(placeRequest);
				}else{
					token=placeRequest.getName();
					tokenPlaces.put(token, placeRequest);
					placeTokens.put(placeRequest, token);
				}
				
				return token;
			}
		};
		PlaceManager placeManager = new DefaultPlaceManager(injector.getEventBus(), tokenFormatter) {
		};
		if (History.getToken() == null || History.getToken().length() == 0) {
			// Go to the default place
			injector.getEventBus().fireEvent(new PlaceChangedEvent(Places.DEFAULT));
		}
		// Trigger history tokens.
		
		String parameters[] = Window.Location.getQueryString().substring(1).split("&");
		for (String param : parameters) {
			final String[] parts = param.split("=");
			if (parts[0].equals("uri")) {
				Timer timer = new Timer() {		
					@Override
					public void run() {
						LoadResourceEvent.fire(parts[1], injector.getEventBus());
					}
				};
				timer.schedule(2000);
			}
		}
        //History.addValueChangeHandler(injector.getDashboard());
        placeManager.fireCurrentPlace();

	}
}
