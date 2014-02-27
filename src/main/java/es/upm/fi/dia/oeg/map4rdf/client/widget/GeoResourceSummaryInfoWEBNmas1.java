package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetWebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.event.FilterDateChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.FilterDateChangeEventHandler;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.resource.WebNMasUnoMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.DateFilter;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoGuide;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoImage;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoResourceContainer;
import es.upm.fi.dia.oeg.map4rdf.share.webnmasuno.WebNMasUnoTrip;

//TODO {VeryHard} Implement WEBN+1 model!!!
public class GeoResourceSummaryInfoWEBNmas1 implements GeoResourceSummaryInfo,FilterDateChangeEventHandler{
	
	public interface Stylesheet {
		String WEBNmas1Line0Style();
		String WEBNmas1Line1Style();
	}
	
	private DialogBox mainWidget;
	private BrowserMessages browserMessages;
	private BrowserResources browserResources;
	private Panel mainPanel;
	private DispatchAsync dispatchAsync;
	private WebNMasUnoMessages webNmessages=GWT.create(WebNMasUnoMessages.class);
	private FlowPanel guidesPanel=new FlowPanel();
	private FlowPanel tripsPanel=new FlowPanel();
	private List<WebNMasUnoGuide> lastGuides;
	private List<DateFilter> dateFilters;
	private List<WebNMasUnoTrip> lastTrips;
	public GeoResourceSummaryInfoWEBNmas1(DispatchAsync dispatchAsync,EventBus eventBus,BrowserResources browserResources,BrowserMessages browserMessages){
		this.browserMessages=browserMessages;
		this.browserResources=browserResources;
		this.dispatchAsync=dispatchAsync;
		this.dateFilters=new ArrayList<DateFilter>();
		this.lastGuides=new ArrayList<WebNMasUnoGuide>();
		this.lastTrips=new ArrayList<WebNMasUnoTrip>();
		eventBus.addHandler(FilterDateChangeEvent.getType(), this);
		createUI();
	}
	
	private void createUI() {
		mainPanel=new FlowPanel();
		mainWidget=new DialogBox(false, false);
		mainWidget.setAnimationEnabled(true);
		mainWidget.setGlassEnabled(false);
		DOM.setStyleAttribute(mainWidget.getElement(), "zIndex", "10");
		Button close = new Button(browserMessages.close());
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mainWidget.hide();
			}
		});
		VerticalPanel mainDialogPanel=new VerticalPanel();
		mainDialogPanel.add(mainPanel);
		mainDialogPanel.add(close);
		mainWidget.setWidget(mainDialogPanel);
		mainDialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(mainPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_CENTER);
		mainWidget.setText(browserMessages.informationTittle(""));	
	}

	@Override
	public void addAdditionalInfo(Map<String, String> additionalsInfo) {
		//TODO: Implement WebNMasUno AdditionalInfo
	}

	@Override
	public void clearAdditionalInfo() {
		//TODO: Implement WebNMasUno ClearAdditionalInfo
	}

	@Override
	public Widget getWidget() {
		return mainPanel;
	}

	@Override
	public void setGeoResource(GeoResource resource, Geometry geometry) {
		mainPanel.clear();
		lastGuides.clear();
		lastTrips.clear();
		mainPanel.add(new Label(browserMessages.loading()));
		dispatchAsync.execute(new GetWebNMasUnoResource(resource.getUri()), new AsyncCallback<SingletonResult<WebNMasUnoResourceContainer>>() {

			@Override
			public void onFailure(Throwable caught) {
				mainPanel.clear();
				mainPanel.add(new Label(browserMessages.errorCommunication()));
			}

			@Override
			public void onSuccess(SingletonResult<WebNMasUnoResourceContainer> result) {
				mainPanel.clear();
				boolean wasShowing=mainWidget.isShowing();
				if(wasShowing){
					mainWidget.hide();
				}
				guidesPanel=new FlowPanel();
				tripsPanel=new FlowPanel();
				mainWidget.setText(browserMessages.informationTittle(""));
				TabPanel tab=new TabPanel();
				lastGuides=result.getValue().getGuides();
				lastTrips=result.getValue().getTrips();
				if(result.getValue().haveGuides() || result.getValue().haveTrips()){
					if(result.getValue().haveTrips()){
						addTrips(applyFiltersTrip(result.getValue().getTrips()), tripsPanel);
						tab.add(tripsPanel,webNmessages.trips());
						tab.selectTab(tab.getWidgetIndex(tripsPanel));
					}
					if(result.getValue().haveGuides()){
						addGuides(applyFiltersGuide(result.getValue().getGuides()), guidesPanel);
						tab.add(guidesPanel, webNmessages.guides());
						tab.selectTab(tab.getWidgetIndex(guidesPanel));
					}
					mainPanel.add(tab);
				}else{
					mainPanel.add(new Label(webNmessages.noGuidesAndTrips()));
				}
				if(wasShowing){
					mainWidget.center();
				}
			}
		});
	}

	@Override
	public boolean isVisible() {
		return mainWidget.isShowing();
	}

	@Override
	public void show() {
		mainWidget.center();
	}

	@Override
	public void close() {
		mainWidget.hide();
	}
	@Override
	public void onYearChange(FilterDateChangeEvent event) {
		dateFilters=event.getDateFilters();
		if(dateFilters==null){
			dateFilters=new ArrayList<DateFilter>();
		}
		addGuides(applyFiltersGuide(lastGuides), guidesPanel);
		addTrips(applyFiltersTrip(lastTrips), tripsPanel);
	}
	private List<WebNMasUnoGuide> applyFiltersGuide(List<WebNMasUnoGuide> toApply){
		if(toApply==null){
			return new ArrayList<WebNMasUnoGuide>();
		}
		if(dateFilters.isEmpty() || toApply.isEmpty()){
			return toApply;
		}
		List<WebNMasUnoGuide> dataFiltered=new ArrayList<WebNMasUnoGuide>();
		for(WebNMasUnoGuide guide:toApply){
			boolean passAllFilters=true;
			for(DateFilter filter:dateFilters){
				Date date=getDate(guide.getDate());
				if(date==null){break;}
				if(!filter.passFilter(date)){
					passAllFilters=false;
					break;
				}
			}
			if(passAllFilters){
				dataFiltered.add(guide);
			}
		}
		return dataFiltered;
	}
	private List<WebNMasUnoTrip> applyFiltersTrip(List<WebNMasUnoTrip> toApply){
		if(toApply==null){
			return new ArrayList<WebNMasUnoTrip>();
		}
		if(dateFilters.isEmpty() || toApply.isEmpty()){
			return toApply;
		}
		List<WebNMasUnoTrip> dataFiltered=new ArrayList<WebNMasUnoTrip>();
		for(WebNMasUnoTrip trip:toApply){
			boolean passAllFilters=true;
			for(DateFilter filter:dateFilters){
				Date date=getDate(trip.getDate());
				if(date==null){break;}
				if(!filter.passFilter(date)){
					passAllFilters=false;
					break;
				}
			}
			if(passAllFilters){
				dataFiltered.add(trip);
			}
		}
		return dataFiltered;
	}
	// TODO search and remove all incrusted styles.
	private void addGuides(List<WebNMasUnoGuide> guides, Panel panel){
		panel.clear();
		if(guides.isEmpty()){
			if(!lastGuides.isEmpty()){
				panel.add(new Label(webNmessages.notGuidesForDateFilter()));
			}else{
				return;
			}
		}
		VerticalPanel verticalPanel=new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		ScrollPanel scroll = new ScrollPanel();
		int width=(int)(Window.getClientWidth()*0.5);
		int height=(int)(Window.getClientHeight()*0.5);
		scroll.setSize(width+"px", height+"px");
		FlexTable table=new FlexTable();
		table.setSize("100%", "auto");
		table.setCellPadding(5);
		table.setCellSpacing(1);
		scroll.setWidget(table);
		HorizontalPanel anchorsPanel=new HorizontalPanel();
		anchorsPanel.setSpacing(6);
		verticalPanel.add(scroll);
		verticalPanel.add(anchorsPanel);
		DOM.setStyleAttribute(DOM.getParent(anchorsPanel.getElement()), "borderTop", "2px solid #004C99");
		DOM.setStyleAttribute(DOM.getParent(anchorsPanel.getElement()), "borderBottom", "2px solid #004C99");
		panel.add(verticalPanel);
		int last=guides.size();
		if(guides.size()>10){last=10;}
		addSubSetGuides(guides.subList(0, last), table, height);
		int totalI=guides.size()/10;
		for(int i=0;i<=totalI;i++){
			Anchor anchor=new Anchor(String.valueOf(i+1));
			anchorsPanel.add(anchor);
			DOM.setStyleAttribute(anchor.getElement(), "color", "");
			if(i==0){
				DOM.setStyleAttribute(anchor.getElement(), "color", "#CC0000");
			}
			int endGuides=(i+1)*10;
			if(endGuides>guides.size()){
				endGuides=guides.size();
			}
			addChangeGuidesPager(anchor, i*10, endGuides,guides,table,height, anchorsPanel,scroll);
		}
		
	}
	private void addTrips(List<WebNMasUnoTrip> trips, Panel panel){
		panel.clear();
		if(trips.isEmpty()){
			if(!dateFilters.isEmpty()){
				panel.add(new Label(webNmessages.notTripsForDateFilter()));
			}else{
				return;
			}
		}
		ScrollPanel scroll=new ScrollPanel();
		int height=(int)(Window.getClientHeight()*0.5);
		scroll.setHeight(height+"px");
		VerticalPanel vertical=new VerticalPanel();
		vertical.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		vertical.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		scroll.setWidget(vertical);
		int tripStyle=0;
		for(WebNMasUnoTrip trip:trips){
			Widget widget=getTripWidget(trip,tripStyle);
			DOM.setStyleAttribute(widget.getElement(), "paddingBottom", "25px");
			vertical.add(widget);
			tripStyle=(tripStyle+1)%2;
		}
		panel.add(scroll);
		//TODO implement trips WebNMas1
	}
	private Widget getTripWidget(WebNMasUnoTrip trip,int style) {
		VerticalPanel vertical = new VerticalPanel();
		vertical.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		vertical.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		HorizontalPanel mainLine= new HorizontalPanel();
		mainLine.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainLine.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainLine.setSpacing(7);
		mainLine.add(new Label(webNmessages.title()));
		mainLine.add(new Anchor(trip.getTitle(), trip.getURL(), "_blank"));
		Image rdfImage= new Image(browserResources.rdfIcon());
		Anchor rdfAnchor=new Anchor("",trip.getURI(),"_blank");
		rdfAnchor.getElement().appendChild(rdfImage.getElement());
		mainLine.add(rdfAnchor);
		mainLine.add(new Label(getVisualDate(trip.getDate())));
		vertical.add(mainLine);
		Widget moreInfoTrip=getMoreInfoTripWidget(trip);
		if(moreInfoTrip!=null){
			vertical.add(moreInfoTrip);
		}
		//TODO Talk with O.Corcho for backgrounds colors !!!!! And obtain the best visualization.
		if(style==0){
			vertical.addStyleName(browserResources.css().WEBNmas1Line0Style());
		}
		if(style==1){
			vertical.setStyleName(browserResources.css().WEBNmas1Line1Style());
		}
		//TODO add drawTrip and history
		return vertical;
	}
	private Widget getMoreInfoTripWidget(WebNMasUnoTrip trip){
		VerticalPanel vertical=new VerticalPanel();
		FlexTable table= new FlexTable();
		boolean addedAMoreInfo=false;
		int actualRow=0;
		if(trip.haveDistanceLess() || trip.haveDistanceMore()){
			int actualColumn=0;
			addedAMoreInfo=true;
			table.getRowFormatter().setVerticalAlign(actualRow, HasVerticalAlignment.ALIGN_MIDDLE);
			if(trip.haveDistanceLess()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(webNmessages.distanceLess()+" "+trip.getDistanceLess()));
				actualColumn++;
			}
			if(trip.haveDistanceMore()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(webNmessages.distanceMore()+" "+trip.getDistanceMore()));
			}
			actualRow++;
		}
		if(trip.haveDurationLess() || trip.haveDurationMore()){
			int actualColumn=0;
			addedAMoreInfo=true;
			table.getRowFormatter().setVerticalAlign(actualRow, HasVerticalAlignment.ALIGN_MIDDLE);
			if(trip.haveDurationLess()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(webNmessages.durationLess()+" "+trip.getDurationLess()));
				actualColumn++;
			}
			if(trip.haveDurationMore()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(webNmessages.durationMore()+" "+trip.getDurationMore()));
			}
			actualRow++;
		}
		if(trip.havePriceLess() || trip.havePriceMore()){
			int actualColumn=0;
			addedAMoreInfo=true;
			table.getRowFormatter().setVerticalAlign(actualRow, HasVerticalAlignment.ALIGN_MIDDLE);
			if(trip.havePriceLess()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(webNmessages.priceLess()+" "+trip.getPriceLess()));
				actualColumn++;
			}
			if(trip.havePriceMore()){
				table.getCellFormatter().setVerticalAlignment(actualRow, actualColumn, HasVerticalAlignment.ALIGN_MIDDLE);
				table.getCellFormatter().setHorizontalAlignment(actualRow, actualColumn, HasHorizontalAlignment.ALIGN_LEFT);
				table.setWidget(actualRow, actualColumn, new Label(webNmessages.priceMore()+" "+trip.getPriceMore()));
			}
			actualRow++;
		}
		if(addedAMoreInfo){
			vertical.add(table);
		}
		if(trip.haveDescription()){
			Label description = new Label(webNmessages.description()+" "+trip.getDescription());
			vertical.add(description);
		}
		if(addedAMoreInfo || trip.haveDescription()){	
			return vertical;
		}else{
			return null;
		}
	}
	private void addSubSetGuides(List<WebNMasUnoGuide> guides, FlexTable table, int height){
		table.clear();
		int row=0;
		for(WebNMasUnoGuide guide:guides){
			table.getRowFormatter().setVerticalAlign(0, HasVerticalAlignment.ALIGN_MIDDLE);
			table.setWidget(row, 0, new Anchor(guide.getTitle(),guide.getURL(),"_blank"));
			table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
			table.setWidget(row, 1, new Label(getVisualDate(guide.getDate())));
			table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
			Widget widgetImages=getWidgetForImages(guide, height);
			if(widgetImages==null){
				Image imageWidget=new Image(browserResources.missingImage().getSafeUri());
				imageWidget.setSize("auto", "180px");
				table.setWidget(row,2,imageWidget);
				table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
			}else{
				table.setWidget(row,2,widgetImages);
				table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
			}
			row++;
		}
	}
	private void addChangeGuidesPager(final Anchor anchor,final int startGuide,final int finalGuide,final List<WebNMasUnoGuide> guides,final FlexTable contentTable,final int height,final HorizontalPanel anchorsPanel, final ScrollPanel scroll){
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addSubSetGuides(guides.subList(startGuide, finalGuide), contentTable, height);
				scroll.scrollToTop();
				for(int i=0;i<anchorsPanel.getWidgetCount();i++){
					DOM.setStyleAttribute(anchorsPanel.getWidget(i).getElement(), "color", "");
				}
				DOM.setStyleAttribute(anchor.getElement(), "color", "#CC0000");	
			}
		});
	}
	private void changeImageWithAnchorClick(final Anchor anchor, final int height,final WebNMasUnoImage image,final Panel contentPanel,final HorizontalPanel anchorsPanel){
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				int imageHeight=(int)(height*0.2);
				contentPanel.clear();
				Anchor contentAnchor=new Anchor();
				contentAnchor.setHref(image.getPname());
				contentAnchor.setTarget("_blank");
				contentAnchor.setSize("auto", imageHeight+"px");
				Image imageWidget=new Image(image.getURL());
				imageWidget.setSize("auto", "180px");
				DOM.appendChild(contentAnchor.getElement(), imageWidget.getElement());
				contentPanel.add(contentAnchor);
				for(int i=0;i<anchorsPanel.getWidgetCount();i++){
					DOM.setStyleAttribute(anchorsPanel.getWidget(i).getElement(), "color", "");
				}
				DOM.setStyleAttribute(anchor.getElement(), "color", "#CC0000");	
			}
		});
	}
	private Widget getWidgetForImages(WebNMasUnoGuide guide, int height){
		VerticalPanel toReturn=new VerticalPanel();
		toReturn.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		toReturn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Panel imagePanel=new FlowPanel();
		HorizontalPanel anchorsPanel=new HorizontalPanel();
		anchorsPanel.setSpacing(6);
		toReturn.add(imagePanel);
		toReturn.add(anchorsPanel);
		boolean isImageAdded=false;
		int imageNumber=1;
		for(WebNMasUnoImage image:guide.getImages()){
			Anchor anchor=new Anchor(String.valueOf(imageNumber));
			imageNumber++;
			changeImageWithAnchorClick(anchor, height, image, imagePanel, anchorsPanel);
			anchorsPanel.add(anchor);
			DOM.setStyleAttribute(anchor.getElement(), "color", "");
			if(!isImageAdded){
				DOM.setStyleAttribute(anchor.getElement(), "color", "#CC0000");
				int imageHeight=(int)(height*0.2);
				Anchor contentImage=new Anchor();
				contentImage.setHref(image.getPname());
				contentImage.setTarget("_blank");
				contentImage.setSize("auto", imageHeight+"px");
				Image imageWidget=new Image(image.getURL());
				imageWidget.setSize("auto", "180px");
				DOM.appendChild(contentImage.getElement(), imageWidget.getElement());
				imagePanel.add(contentImage);
			}
			isImageAdded=true;
		}
		if(isImageAdded){
			return toReturn;
		}else{
			return null;
		}
	}
	private Date getDate(String webNmas1Date){
		try{
			DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd");
			return dtf.parse(webNmas1Date);
		} catch (Exception e){
			return null;
		}
	}
	private String getVisualDate(String webNmas1Date){
		try{
			Date date=getDate(webNmas1Date);
			DateTimeFormat newFormat = DateTimeFormat.getFormat("dd-MM-yyyy");
			return newFormat.format(date);
		}catch (Exception e){
			return webNmas1Date;
		}
	}

}
