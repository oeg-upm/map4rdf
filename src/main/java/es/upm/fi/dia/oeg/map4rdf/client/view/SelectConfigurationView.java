package es.upm.fi.dia.oeg.map4rdf.client.view;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAllConfigurationsDescription;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAllConfigurationsDescriptionResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.SelectConfigurationPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ConstantsFolders;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.ConfigurationDescription;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceChangedEvent;

public class SelectConfigurationView extends Composite implements SelectConfigurationPresenter.Display{
	
	public interface Stylesheet {
		String configurationDescriptionLine0Style();
		String configurationDescriptionLine1Style();
	}

	public interface SelectedCallback{
		public void doCallBack(String newConfigID);
	}
	private ConfIDInterface configID;
	private EventBus eventBus;
	private SelectWindow dialog;
	private BrowserResources resources;
	private BrowserMessages messages;
	@Inject
	public SelectConfigurationView(ConfIDInterface configID,EventBus eventBus, DispatchAsync dispatchAsync,BrowserResources resources,final BrowserMessages messages, final WidgetFactory widgetFactory){
		this.configID=configID;
		this.eventBus=eventBus;
		this.resources=resources;
		this.messages=messages;
		Panel panel= new VerticalPanel();
		initWidget(panel);
		if(!configID.existsConfigID()){
			dispatchAsync.execute(new GetAllConfigurationsDescription(), new AsyncCallback<GetAllConfigurationsDescriptionResult>() {
				
				@Override
				public void onSuccess(GetAllConfigurationsDescriptionResult result) {
					initAsync(result.getConfigurationsDescription());	
				}

				@Override
				public void onFailure(Throwable caught) {
					widgetFactory.getDialogBox().showError(messages.errorCommunication()+":"+caught.getMessage());
				}
			});
		}
		
	}
	private void initAsync(
			List<ConfigurationDescription> configurationsDescription) {
		dialog = new SelectWindow();
		int style=0;
		for(ConfigurationDescription configDescription: configurationsDescription){
			if(style==0){
				dialog.addWidget(getConfigWidget(configDescription,resources.css().configurationDescriptionLine0Style()));
				style=1;
			}else{
				dialog.addWidget(getConfigWidget(configDescription,resources.css().configurationDescriptionLine1Style()));
				style=0;
			}
		}
		dialog.center();
	}
	private Widget getConfigWidget(final ConfigurationDescription configDescription, String style){
		HorizontalPanel panel= new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		VerticalPanel infoPanel = new VerticalPanel();
		if(configDescription.hasImage()){
			Image image=new Image(GWT.getHostPageBaseURL()+ConstantsFolders.LOGOS_FOLDER+configDescription.getImage());
			panel.add(image);
		}
		infoPanel.add(new Label(LocaleUtil.getBestLabel(configDescription)));
		infoPanel.add(new InlineHTML("<p></p>"));
		if(configDescription.hasDescription()){
			infoPanel.add(new Label(LocaleUtil.getBestLabel(new DescriptionResource(configDescription.getDescriptions()))));
		}
		panel.add(infoPanel);
		panel.sinkEvents(Event.ONCLICK);
		panel.addStyleName(style);
		panel.addHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				fireOnSelectedEvent(configDescription.getId());
			}

		}, ClickEvent.getType());
		return panel;
	}
	public void fireOnSelectedEvent(String id){
		dialog.hide();
		configID.setConfigID(id);
		eventBus.fireEvent(new OnSelectedConfiguration(id));
		eventBus.fireEvent(new PlaceChangedEvent(Places.DASHBOARD));
	}
	private class DescriptionResource extends Resource{
		private static final long serialVersionUID = -4532902285276486639L;

		public DescriptionResource(Map<String,String> descriptions) {
			super("");
			for(String locale: descriptions.keySet()){
				super.addLabel(locale, descriptions.get(locale));
			}
		}
		
	}
	
	public class SelectWindow  extends DialogBox{
		private Panel mainPanel;
		
		public SelectWindow(){
			super();
			
			// Set the dialog box's caption.
			setText(messages.configurationSelectionTittle());

			// Enable animation.
			setAnimationEnabled(true);

			// Enable glass background.
			setGlassEnabled(true);
			
			//Disable modal. The user cant click in app(GlassEnabled true) and other startup errors can be show.
			//If modal is false. Others errors dont be show.
			setModal(false);
			
			mainPanel= new VerticalPanel();
			
			DOM.setStyleAttribute(this.getElement(), "zIndex", "10000");
			setWidget(new ScrollPanel(mainPanel));
		}
		
		public void addWidget(Widget widget){
			mainPanel.add(widget);
		}
		
		public void removeWidget(Widget widget){
			mainPanel.remove(widget);
		}
	}
}
