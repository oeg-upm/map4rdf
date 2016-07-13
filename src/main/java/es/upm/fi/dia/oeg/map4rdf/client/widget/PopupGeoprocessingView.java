package es.upm.fi.dia.oeg.map4rdf.client.widget;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.util.GeoResourceGeometry;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.GeoprocessingType;


public class PopupGeoprocessingView extends Composite{
	public static interface Stylesheet {
		String searchPanel();
	}
	private AbsolutePanel mainPanel;
	private TextBox searchBox;
	private PopupGeoprocessingViewWidget searchResultsView;
	private InlineHTML error;
	private int width;
	private int height;
	private DashboardPresenter dashboardPresenter;
	private BrowserMessages browserMessages;
	private BrowserResources browserResources;
	private EventBus eventBus;
	private GeoprocessingType type;
	private Map<String,GeoResourceGeometry> searchResults;
	public PopupGeoprocessingView(int width,int height, DashboardPresenter dashboardPresenter,
			BrowserMessages browserMessages,BrowserResources browserResources, EventBus eventBus,
			GeoprocessingType type
			){
		this.dashboardPresenter = dashboardPresenter;
		this.browserMessages = browserMessages;
		this.browserResources = browserResources;
		this.eventBus=eventBus;
		this.type=type;
		this.width=width;
		this.height=height;
		if(width>=100){
			this.width=width-100;
		}
		if(height>=65){
			this.height=height-65;
		}
		searchResults= new HashMap<String, GeoResourceGeometry>();
		initWidget(createUi());
	}

	private Widget createUi() {
		this.mainPanel = new AbsolutePanel();
		searchResultsView= new PopupGeoprocessingViewWidget((int)(width*0.94),(int)(height*0.7),eventBus,dashboardPresenter,type, browserResources);
		searchBox = new TextBox();
		searchBox.setFocus(true);
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				
				if(event.getNativeEvent().getCharCode() == KeyCodes.KEY_ENTER ){
					doSearch();
				}
			}
		});
		Button closeButton = new Button();
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				dashboardPresenter.getDisplay().closeMainPopup();
		}});
		closeButton.setSize("32px","28px");
		closeButton.getElement().appendChild(new Image(browserResources.closeButton()).getElement());
		closeButton.setTitle(browserMessages.close());
		this.error=new InlineHTML();
		this.mainPanel.setWidth(String.valueOf(width)+"px");
		this.mainPanel.setHeight(String.valueOf(height)+"px");
		this.searchBox.setWidth(String.valueOf((int)(0.8*width))+"px");
		InlineHTML inline=new InlineHTML("<a>"+browserMessages.searchAResource()+"</a>");
		this.mainPanel.add(closeButton);
		closeButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
		closeButton.getElement().getStyle().setTop(1, Unit.PX);
		closeButton.getElement().getStyle().setProperty("left", "");
		closeButton.getElement().getStyle().clearLeft();
		closeButton.getElement().getStyle().setRight(1, Unit.PX);
		closeButton.getElement().getStyle().setZIndex(2080);
		this.mainPanel.add(new InlineHTML("<br>"));
		this.mainPanel.add(new InlineHTML("<br>"));
		this.mainPanel.add(inline);	
		this.mainPanel.add(searchBox);
		Button buttonSearch=new Button(browserMessages.search(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				doSearch();
			}
		});
		buttonSearch.setTitle(browserMessages.buttonSearchAResourceTooltip());
		this.mainPanel.add(buttonSearch);
		this.mainPanel.add(new InlineHTML("<br>"));
		this.mainPanel.add(new InlineHTML("<br>"));
		this.mainPanel.add(searchResultsView);
		this.mainPanel.add(new InlineHTML("<br>"));
		this.mainPanel.add(error);
		this.mainPanel.add(new InlineHTML("<br>"));
		this.mainPanel.add(new InlineHTML("<br>"));
		return mainPanel;
	}
	public void search(String text){
		if(text!=null && text!=""){
			searchBox.setText(text);
			doSearch();
		}
	}
	private void doSearch(){
		searchResultsView.clear();
		searchResults.clear();
		if(dashboardPresenter.getListGeoResource()== null || dashboardPresenter.getListGeoResource().isEmpty()){
			error.setText(browserMessages.errorNotGeoResource());
			return;
		}
		if(searchBox.getText()==null || searchBox.getText() == "" || searchBox.getText().isEmpty()){
			error.setText(browserMessages.errorNotSearchText());
			return;
		}
		boolean find=false;
		for(GeoResource resource:dashboardPresenter.getListGeoResource()){
			if(compare(resource,searchBox.getText())){
				find=true;
				addSearchResultGeoResource(resource, resource.getFirstGeometry());
			}
		}
		if(find == false){
			error.setText(browserMessages.error0Search());
		} else {
			putSearchResult();
			error.setText("");
		}
	}
	private void putSearchResult(){
		List<String> labels= new ArrayList<String>(searchResults.keySet());
		Collections.sort(labels);
		for(String label:labels){
			searchResultsView.addSearchResultGeoResource(label,searchResults.get(label));
		}
	}
	private void addSearchResultGeoResource(GeoResource resource, Geometry geometry){
		String label=LocaleUtil.getBestLabel(resource);
		searchResults.put(label, new GeoResourceGeometry(resource,geometry));
	}
	private boolean compare(GeoResource resource, String name){
		String label=LocaleUtil.getBestLabel(resource);
		String removedLabel=removeSpecialChars(label);
		return removedLabel.toLowerCase().contains(name.toLowerCase())||label.toLowerCase().contains(name.toLowerCase());
	}
	private String removeSpecialChars(String input) {
		String originalString = browserMessages.specialsChars();
	    String asciiString =    browserMessages.specialsCharsSubstitution();
	    
	    String output = input;
	    for (int i=0; i<originalString.length(); i++) {
	        // Reemplazamos los caracteres especiales.
	        output = output.replace(originalString.charAt(i), asciiString.charAt(i));
	    }
	    return output;
	}

	
}