/**
 * Copyright (c) 2010 Miguel Angel Garcia Delgado
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
package es.upm.fi.dia.oeg.map4rdf.client.resource;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Miguel Angel Garcia Delgado
 */
public interface BrowserMessages extends Messages {
	
	String facets();
	
	String statistics();

	String results();

	String overlays();

	String loading();

	String select();

	String type();

	String here();

	String latitude();

	String longitude();
	
	String crs();

	String information();
	
	String informationTittle(String resource);
	
	String edit();
	
	String filtres();
	
	String clear();

	String draw();
	
	String canNotLoaddescription();
	
	String geoprocessing();
	
	String routes();
	
	String buttonTraceRoute();
	
	String error2OrMorePoints();
	
	String errorCommunication();
	
	String errorNotRouteTo();
	
	String searchAResource();
	
	String search();
	
	String buttonSearchAResourceTooltip();
	
	String close();
	
	String errorNotGeoResource();
	
	String error0Search();
	
	String errorNotSearchText();
	
	String specialsChars();
	
	String specialsCharsSubstitution();
	
	String messageAddRoutePoint();
	
	String buffer();
	
	String addToRoutes();
	
	String setToBuffer();
	
	String errorConvertDistance();
	
	String errorDistanceNegative();
	
	String errorDistanceUnit();
	
	String bufferIntro();
	
	String currentCenter();
	
	String searchCenter();
	
	String distance();
	
	String unit();
	
	String drawPoints();
	
	String infoResource();
	
	String twitterTitle();
	
	String wikipediaTitle();
	
	String rdfTitle();
	
	String wikipediaNotFound();
	
	String gotoWikipedia();
	
	String wikipedia();
	
	String URLSpecialChars();
	
	String URLSpecialCharsSubstitution();
	
	String warning();
	
	String estimatedTime();
	
	String copyrights();
	
	String routeAlternatives();
	
	String avoidHighways();
	
	String avoidTolls();
	
	String optimizeWaypoints();
	
	String yes();
	
	String no();
	
	String travelMode();
	
	String bicycling();
	
	String driving();
	
	String walking();
	
	String zeroResults();
	
	String maxWaypointsExceeded();
	
	String overQueryLimit();
	
	String unknownError();
	
	String requestDenied();
	
	String nameRoute();
	
	String moreOptions();
	
	String statisticsEmpty();
	
	String errorNotDimensions();
	
	String statisticsChoose();
	
	String statisticsDimensionXChoose();
	
	String statisticsDimensionYChoose();
	
	String statisticsAggrChoose();
	
	String statisticsErrorNotAggr();
	
	String saveRDFError();
	
	String saveRDFDone();
	
	

}
