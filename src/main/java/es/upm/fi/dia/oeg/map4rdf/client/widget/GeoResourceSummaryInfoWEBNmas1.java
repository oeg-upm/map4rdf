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
	
	public GeoResourceSummaryInfoWEBNmas1(DispatchAsync dispatchAsync,EventBus eventBus,BrowserResources browserResources,BrowserMessages browserMessages){
		this.browserMessages=browserMessages;
		this.browserResources=browserResources;
		this.dispatchAsync=dispatchAsync;
		this.dateFilters=new ArrayList<DateFilter>();
		this.lastGuides=new ArrayList<WebNMasUnoGuide>();
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
				if(result.getValue().haveGuides() && result.getValue().haveTrips()){
					lastGuides=result.getValue().getGuides();
					mainWidget.setText(browserMessages.informationTittle(""));
					TabPanel tab=new TabPanel();
					tab.add(guidesPanel, webNmessages.guides());
					addGuides(result.getValue().getGuides(), guidesPanel);
					tab.add(tripsPanel,webNmessages.trips());
					addTrips(result.getValue().getTrips(), tripsPanel);
					mainPanel.add(tab);
				}else{
					if(result.getValue().haveGuides()){
						lastGuides=result.getValue().getGuides();
						mainWidget.setText(webNmessages.guides());
						mainPanel.add(new Label(webNmessages.guides()));
						mainPanel.add(guidesPanel);
						addGuides(applyFilters(result.getValue().getGuides()), guidesPanel);
					}else if(result.getValue().haveTrips()){
						mainWidget.setText(webNmessages.trips());
						mainPanel.add(new Label(webNmessages.trips()));
						mainPanel.add(tripsPanel);
						addTrips(result.getValue().getTrips(), tripsPanel);
					}else{
						mainPanel.add(new Label(webNmessages.noGuidesAndTrips()));
					}
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
		addGuides(applyFilters(lastGuides), guidesPanel);
	}
	private List<WebNMasUnoGuide> applyFilters(List<WebNMasUnoGuide> toApply){
		if(toApply==null){
			return new ArrayList<WebNMasUnoGuide>();
		}
		System.out.println("Entran=>"+toApply.size());
		if(dateFilters.isEmpty() || toApply.isEmpty()){
			System.out.println("Sale igual.");
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
		System.out.println("Salen=>"+dataFiltered.size());
		return dataFiltered;
	}
	// TODO search and remove all incrusted styles.
	private void addGuides(List<WebNMasUnoGuide> guides, Panel panel){
		panel.clear();
		if(guides.isEmpty()){
			panel.add(new Label(webNmessages.notGuidesForDateFilter()));
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
		//TODO implement trips WebNMas1
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
