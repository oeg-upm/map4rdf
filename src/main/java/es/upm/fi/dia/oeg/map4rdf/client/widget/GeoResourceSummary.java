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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParameters;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParametersResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.BufferSetPointEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.CloseMapMainPopupEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.DashboardDoSelectedResultWidgetEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.EditResourceEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.ResultWidgetAddEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.ResultWidgetDoSelectedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.ResultWidgetRemoveEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.RoutesAddPointEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.StatisticsSummaryEvent;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.AdditionalInfoExecuter;
import es.upm.fi.dia.oeg.map4rdf.client.util.AdditionalInfoExecuter.InfoCallback;
import es.upm.fi.dia.oeg.map4rdf.client.util.AdditionalInfoSummary;
import es.upm.fi.dia.oeg.map4rdf.client.util.ParametersSummaryMove;
import es.upm.fi.dia.oeg.map4rdf.client.util.RoutesAddGeoResourceType;
import es.upm.fi.dia.oeg.map4rdf.client.util.WidgetsNames;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.MapShape;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Alexander De Leon
 * @author Francisco Siles
 */
public class GeoResourceSummary extends Composite {

	
	private BrowserMessages messages;
	private BrowserResources resources;
	private GeoResource lastGeoResource;
	private Geometry lastGeometry;
	private EventBus eventBus;
	private GeoResourceSummaryInfo summary;
	private boolean summaryVisible;
	private final int intSizeImages=35;
	private final String sizeImages=intSizeImages+"px";
	private final String sizeAllTable=(intSizeImages*3)+"px";//sizeImages*3
	private final int widgetDistance=3;//distance between widgets
	private Panel mainPanel;
	private ArrayList<Widget> allWidgetInOrder;
	private Map<String,Widget> allWidgetsWithName;
	private ParametersSummaryMove parametersSummary;
	private SummaryMove summaryMove;
	private Widget routesWidget;
	private Widget bufferWidget;
	private Widget infoWidget;
	private Widget editWidget;
	private Widget statisticsWidget;
	private Anchor twitterAnchor;
	private Anchor rdfAnchor;
	private Widget wikipediaResultWidget;
	private Widget wikipediaWidget;
	private String twitterURL;
	private String wikipediaParseURL;
	private Map<String,String> additionalsInfo;
	private DispatchAsync dispatchAsync;
	private Panel centerPanel;
	/*If you modify this constant remember to modify GeoResourceSummary.getPointVisualization()
	 * the widgets need to be in the same order that this constant and the same length*/
	public final String SUMMARY_WIDGETS_NAMES = WidgetsNames.ALL_IN_ORDER;
	private int moveType;
	
	public GeoResourceSummary(DispatchAsync dispatchAsync,EventBus eventBus,BrowserMessages messages, BrowserResources appResources) {
		this.messages = messages;
		this.resources=appResources;
		this.eventBus = eventBus;
		allWidgetsWithName=new HashMap<String, Widget>();
		allWidgetInOrder=new ArrayList<Widget>();
		this.dispatchAsync=dispatchAsync;
		List<String> parameters= new ArrayList<String>();
		parameters.add(ParameterNames.WIKIPEDIA_PARSE_URL);
		parameters.add(ParameterNames.SUMMARY_WIDGETS);
		parameters.add(ParameterNames.TWITTER_STATUS_URL);
		dispatchAsync.execute(new GetMultipleConfigurationParameters(parameters), new AsyncCallback<GetMultipleConfigurationParametersResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Summary widgets can't contact with server, please contact with System Admin.");
				initAsync(null);
			}

			@Override
			public void onSuccess(GetMultipleConfigurationParametersResult result) {
				String wiki=result.getResults().get(ParameterNames.WIKIPEDIA_PARSE_URL);
				if(wiki!=null && !wiki.isEmpty()){
					wikipediaParseURL=wiki;
				}else{
					Window.alert(ParameterNames.WIKIPEDIA_PARSE_URL + " config parameter is null or empty.");
				}
				String summaryWidgets=result.getResults().get(ParameterNames.SUMMARY_WIDGETS);
				if(summaryWidgets==null || summaryWidgets.isEmpty()){
					Window.alert(ParameterNames.SUMMARY_WIDGETS+" config parameter is null or empty");
					initAsync(null);
				}else{
					initAsync(summaryWidgets.toLowerCase());
				}
				String twitter=result.getResults().get(ParameterNames.TWITTER_STATUS_URL);
				if(twitter!=null && !twitter.isEmpty()){
					twitterURL=twitter;
				}else{
					Window.alert(ParameterNames.TWITTER_STATUS_URL+" config parameter is null or empty");
				}		
			}
		});
		initWidget(createUi());
	}
	public GeoResourceSummary() {
		allWidgetInOrder=new ArrayList<Widget>();
		summaryMove=new SummaryMove(allWidgetInOrder, parametersSummary, this);
	}

	public void setGeoResource(final GeoResource resource, Geometry geometry) {
		openOrCloseSummary(false);
		lastGeoResource=resource;
		lastGeometry=geometry;
		
		centerPanel.clear();
		centerPanel.add(getCenterImage());
		//panel.remove(editLink);
		//editLink.setText(messages.here());
		//editLink.add
		//panel.add(editLink);
		
		AdditionalInfoExecuter.cancelAllCallbacks();
		AdditionalInfoExecuter.getAdditionalInfo(dispatchAsync, resource, new InfoCallback() {
			@Override
			public void success(AdditionalInfoSummary additionalInfo) {
				summary.clearAdditionalInfo();
				additionalsInfo=additionalInfo.getAdditionalInfo();
				if(additionalInfo.haveImage()){
					centerPanel.clear();
					Image image= new Image(GWT.getModuleBaseURL()
							+ additionalInfo.getImage());
					DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
					DOM.setStyleAttribute(image.getElement(), "position", "absolute");
					image.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							close();
						}
					});
					centerPanel.add(image);
				}
				openOrCloseSummary(false);
			}
		});
		summary.setGeoResource(resource, geometry);
		if(wikipediaResultWidget!=null){
			eventBus.fireEvent(new ResultWidgetRemoveEvent(wikipediaResultWidget));
			wikipediaResultWidget=null;
		}
		if (geometry.getType() == MapShape.Type.POINT) {
			routesWidget.setVisible(true);
			bufferWidget.setVisible(true);
		} else {
			routesWidget.setVisible(false);
			bufferWidget.setVisible(false);
		}
		if (resource.getUri()!=null) {
			infoWidget.setVisible(true);
			twitterAnchor.setVisible(true);
			twitterAnchor.setHref(twitterURL+URL.encode(resource.getUri()));
			rdfAnchor.setVisible(true);
			rdfAnchor.setHref(resource.getUri());
			editWidget.setVisible(true);
			statisticsWidget.setVisible(true);
			wikipediaWidget.setVisible(true);
		} else {
			editWidget.setVisible(false);
			twitterAnchor.setVisible(false);
			rdfAnchor.setVisible(false);
			wikipediaWidget.setVisible(false);
			if(geometry.getType() != MapShape.Type.POINT){
				infoWidget.setVisible(false);
				statisticsWidget.setVisible(false);
			}
		}
		ArrayList<Widget> visibleWidget= new ArrayList<Widget>();
		for(int i=0;i<allWidgetInOrder.size();i++){
			if(allWidgetInOrder.get(i).isVisible()){
				visibleWidget.add(allWidgetInOrder.get(i));
			}
		}
		summaryMove.cancelMove();
		parametersSummary=initializeParametersSummary(visibleWidget.size(),moveType);
		summaryMove=new SummaryMove(visibleWidget, parametersSummary, this);
		moveInitialPosition();
		summaryMove.startMoveWidgets();
	}
	public void closeSummary(){
		openOrCloseSummary(false);
	}

	public void moveLeftTopOfCenter(Widget widget,int left,int top){
		int centerLeft=intSizeImages;
		int centerTop=intSizeImages;
		left=centerLeft+left;
		top=centerTop-top;
		DOM.setStyleAttribute(widget.getElement(), "left", left+"px");
		DOM.setStyleAttribute(widget.getElement(), "top", top+"px");
	}
	private Widget createUi() {
		summary=getSummary();
		
		summaryVisible=false;
		mainPanel= new FlowPanel();
		mainPanel.setSize(sizeAllTable, sizeAllTable);
		Image image=new Image(resources.summaryBackGround());
		image.setSize(sizeAllTable, sizeAllTable);
		DOM.setStyleAttribute(mainPanel.getElement(), "background", image.getElement().getStyle().getBackgroundImage()+" no-repeat scroll 0px 0px transparent");
		mainPanel.add(centerPanel=getCenter());
		DOM.setStyleAttribute(centerPanel.getElement(),"position", "absolute");
		DOM.setStyleAttribute(centerPanel.getElement(),"left", sizeImages);
		DOM.setStyleAttribute(centerPanel.getElement(),"top", sizeImages);
		return mainPanel;
	}
	
	private void initAsync(String summaryWidgets){
		initPointVisualization();
		if(summaryWidgets!=null && !summaryWidgets.isEmpty()){
			String split[]=summaryWidgets.split(";");
			for(String i:summaryWidgets.split(";")){
				if(allWidgetsWithName.containsKey(i)){
					Widget widget=allWidgetsWithName.get(i);
					mainPanel.add(widget);
					allWidgetInOrder.add(widget);
				}
				if(split[split.length-1].toLowerCase().contains("type")){
					try {
						String type[]=split[split.length-1].split(":");
						moveType=Integer.parseInt(type[type.length-1]);
					} catch (Exception e) {
					}
				}
			}
		}else{
			for(Widget i:allWidgetsWithName.values()){
				mainPanel.add(i);
				allWidgetInOrder.add(i);
			}
		}
		parametersSummary=initializeParametersSummary(allWidgetInOrder.size(),moveType);
		summaryMove=new SummaryMove(allWidgetInOrder, parametersSummary, this);
	}
	private Panel initPointVisualization(){
		Widget widget;
		/*This */
		/*To add new widget in the summary please
		 * modify Constants.SUMMARY_WIDGETS_NAMES add the new name of the widget
		 * and add the widget in the same order this appear in Constants.SUMMARY_WIDGETS_NAMES;*/
		String summaryArrayWidgets[]=SUMMARY_WIDGETS_NAMES.split(";");
		int i=0;
		widget=getInfo();
		infoWidget=widget;
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		
		widget=getWikipedia();
		wikipediaWidget=widget;
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		//widgetsLineMove.add(new WidgetLineMove(widget, new LeftTopPosition(intSizeImages*2,0),"px"));
		
		widget=getSetToBuffer();
		bufferWidget=widget;
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		
		widget=getTwitter();
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		//widgetsLineMove.add(new WidgetLineMove(widget, new LeftTopPosition(intSizeImages*2,intSizeImages*2),"px"));
		
		widget=getClose();
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		
		widget=getEdit();
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		//widgetsLineMove.add(new WidgetLineMove(widget, new LeftTopPosition(0,intSizeImages*2),"px"));
		
		widget=getAddToRoutes();
		routesWidget=widget;
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		
		widget=getRDF();
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		//widgetsLineMove.add(new WidgetLineMove(widget, new LeftTopPosition(0,0),"px"));
		
		widget=getStatistics();
		statisticsWidget=widget;
		allWidgetsWithName.put(summaryArrayWidgets[i++], widget);
		DOM.setStyleAttribute(widget.getElement(),"position", "absolute");
		
		return mainPanel;
	}
	private Widget getStatistics(){
		Image image= new Image(resources.statsSummaryIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.statistics());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				openPopupStatisticsView();
			}
		});
		return image;
	}
	private Widget getInfo(){
		Image image= new Image(resources.infoIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.infoResource());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				openOrCloseSummary(!summaryVisible);
			}
		});
		return image;
	}
	private Widget getWikipedia(){
		/*wikipediaAnchor=new Anchor();
		wikipediaAnchor.setSize(sizeImages, sizeImages);
		Image image=new Image(resources.wikipediaIcon());
		image.setSize(sizeImages, sizeImages);
		wikipediaAnchor.setTitle(messages.wikipediaTitle());
		wikipediaAnchor.getElement().appendChild(image.getElement());
		//wikipediaAnchor.setTarget("_blank");
		wikipediaAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				revealWikipedia();
			}
		});
		return wikipediaAnchor;*/
		Image image= new Image(resources.wikipediaIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.wikipediaTitle());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				revealWikipedia();
			}
		});
		return image;
	}
	private Widget getEdit(){
		Image image= new Image(resources.editIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.edit());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				EditResourceEvent editEvent = new EditResourceEvent(lastGeoResource.getUri());
				eventBus.fireEvent(editEvent);
			}
		});
		editWidget=image;
		return editWidget;
	}
	private Widget getRDF(){
		rdfAnchor=new Anchor();
		rdfAnchor.setSize(sizeImages, sizeImages);
		Image image=new Image(resources.rdfIcon());
		image.setSize(sizeImages, sizeImages);
		rdfAnchor.setTitle(messages.rdfTitle());
		rdfAnchor.getElement().appendChild(image.getElement());
		rdfAnchor.setTarget("_blank");
		return rdfAnchor;
	}
	private GeoResourceSummaryInfo getSummary(){
		return new GeoResourceSummaryInfoDefault(messages, resources);
	}
	private Widget getAddToRoutes(){
		Image image=new Image(resources.routesIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.addToRoutes());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				addToRoute();
			}
		});
		return image;
	}
	private Widget getSetToBuffer(){
		Image image=new Image(resources.bufferIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.setToBuffer());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				setToBuffer();
			}
		});
		return image;
	}
	private Panel getCenter(){
		FlowPanel panel= new FlowPanel();
		panel.setSize(sizeImages,sizeImages);
		panel.add(getCenterImage());
		return panel;
	}
	private Widget getCenterImage(){
		Image image=new Image(resources.transparentImage());
		image.setSize(sizeImages, sizeImages);
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				close();
			}
		});
		return image;
	}
	/*private Widget getTransparent(){
		Image image=new Image(resources.transparentImage());
		image.setSize(sizeImages, sizeImages);
		return image;
	}*/
	private Widget getTwitter(){
		twitterAnchor=new Anchor();
		twitterAnchor.setSize(sizeImages, sizeImages);
		Image image=new Image(resources.twitterIcon());
		image.setSize(sizeImages, sizeImages);
		twitterAnchor.setTitle(messages.twitterTitle());
		twitterAnchor.getElement().appendChild(image.getElement());
		twitterAnchor.setTarget("_blank");
		return twitterAnchor;
	}
	private Widget getClose(){
		Image image=new Image(resources.closeIcon());
		image.setSize(sizeImages, sizeImages);
		image.setTitle(messages.close());
		DOM.setStyleAttribute(image.getElement(), "cursor", "pointer");
		image.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				close();
			}
		});
		return image;
	}
	private void revealWikipedia() {
		if(wikipediaResultWidget!=null){
			eventBus.fireEvent(new ResultWidgetRemoveEvent(wikipediaResultWidget));
			wikipediaResultWidget=null;
		}
		if(!lastGeoResource.getWikipediaURL().isEmpty()){
			String wikipediaURL=lastGeoResource.getWikipediaURL().iterator().next();
			VerticalPanel panel=new VerticalPanel();
			/*panel.setWidth("100%");
			panel.setHeight("100%");*/
			Anchor anchor=new Anchor(messages.wikipediaTitle(), wikipediaURL);
			anchor.setTarget("_blank");
			panel.add(anchor);
			//panel.add(new InlineHTML("<br>"));
	
			//ScrollPanel scroll= new ScrollPanel(new InlineHTML("<iframe src=\""+GWT.getHostPageBaseURL()+"infoWikipedia/parse?URL="+wikipediaURL+"\" style=\"width: 380px; height: 600px;\">"));
			ScrollPanel scroll= new ScrollPanel(new InlineHTML("<iframe src=\""+wikipediaParseURL+wikipediaURL+"\" style=\"width: 260px; height:"+String.valueOf(Window.getClientHeight()-400)+"px\">"));
			panel.add(scroll);
			wikipediaResultWidget=panel;
		}else{
			VerticalPanel panel=new VerticalPanel();
			panel.add(new Label(messages.wikipediaNotFound()));
			wikipediaResultWidget=panel;
		}
		eventBus.fireEvent(new ResultWidgetAddEvent(wikipediaResultWidget, messages.wikipedia()));
		eventBus.fireEvent(new ResultWidgetDoSelectedEvent(wikipediaResultWidget));
		eventBus.fireEvent(new DashboardDoSelectedResultWidgetEvent());
	}
	private void addToRoute(){
		eventBus.fireEvent(new RoutesAddPointEvent(lastGeoResource, lastGeometry, RoutesAddGeoResourceType.OtherPopup));
	}
	private void setToBuffer(){
		eventBus.fireEvent(new BufferSetPointEvent(lastGeoResource, lastGeometry));
	}
	private void close(){
		closeProperSummary();
		if(wikipediaResultWidget!=null){
			eventBus.fireEvent(new ResultWidgetRemoveEvent(wikipediaResultWidget));
			wikipediaResultWidget=null;
		}
		eventBus.fireEvent(new CloseMapMainPopupEvent());
	}
	private void openPopupStatisticsView(){
		eventBus.fireEvent(new StatisticsSummaryEvent(true, lastGeoResource));
	}
	private void openOrCloseSummary(boolean open){
		if(open){
			openProperSummary();
		}else{
			closeProperSummary();
		}
	}
	private void openProperSummary(){
		if(!summaryVisible){
			mainPanel.add(summary.getWidget());
			summaryVisible=true;
			int extraRadiousPX=parametersSummary.getRadiousPX()-intSizeImages;
			summary.addAdditionalInfo(additionalsInfo, extraRadiousPX);
		}
	}
	private void closeProperSummary(){
		if(summaryVisible){
			summaryMove.cancelMove();
			mainPanel.remove(summary.getWidget());
			summaryVisible=false;
		}
	}
	private void moveInitialPosition(){
		summaryMove.moveToInitialPosition();
	}
	private ParametersSummaryMove initializeParametersSummary(int widgetsSize,int moveType){
		//For 4 widgets 104
		//For 5 widgets 105
		//For 6 or more  widget 104(6 or more widget are 8 widget and X transparent widget) X{0,1,2}
		if(widgetsSize==0){widgetsSize=1;}
		final int parameter=(int)((int)(104/widgetsSize))*widgetsSize;
		final int firtsTotalTime=parameter*3;
		final int secondTotalTime=parameter;
		final int steps=parameter;
		final int diffSteps=10;
		final int diffSpecialSteps=steps/widgetsSize;//procure that the division have a int result.
		int radiousPX=(int)(((intSizeImages+widgetDistance)*widgetsSize)/(2*Math.PI));
		if(radiousPX<intSizeImages){
			radiousPX=intSizeImages;
		}
		return new ParametersSummaryMove(firtsTotalTime,secondTotalTime, steps, diffSteps, diffSpecialSteps, radiousPX,intSizeImages,widgetDistance,moveType);
		
		/*final int parameter=105;
		final int firtsTotalTime=parameter*3;
		final int secondTotalTime=parameter;
		final int steps=parameter;
		final int diffSteps=10;
		final int diffSpecialSteps=steps/allWidgetInOrder.size();//procure that the division have a int result.
		final int radiousPX=intSizeImages+10;
		return new ParametersSummaryMove(firtsTotalTime,secondTotalTime, steps, diffSteps, diffSpecialSteps, radiousPX);*/
	}

}
