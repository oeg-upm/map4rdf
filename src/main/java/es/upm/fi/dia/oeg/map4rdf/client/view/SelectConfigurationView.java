package es.upm.fi.dia.oeg.map4rdf.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.navigation.Places;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.SelectConfigurationPresenter;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.place.PlaceChangedEvent;

public class SelectConfigurationView extends Composite implements SelectConfigurationPresenter.Display{
	
	public interface SelectedCallback{
		public void doCallBack(String newConfigID);
	}
	private ConfIDInterface configID;
	private EventBus eventBus;
	private SelectWindow dialog;
	@Inject
	public SelectConfigurationView(ConfIDInterface configID,EventBus eventBus){
		this.configID=configID;
		this.eventBus=eventBus;
		Panel panel= new VerticalPanel();
		initWidget(panel);
		if(!configID.existsConfigID()){
			dialog= new SelectWindow();
			dialog.center();
		}
	}
	public void fireOnSelectedEvent(){
		configID.setConfigID("geolinkeddata");
		eventBus.fireEvent(new OnSelectedConfiguration("geolinkeddata"));
		eventBus.fireEvent(new PlaceChangedEvent(Places.DASHBOARD));
		dialog.hide();
	}
	public class SelectWindow  extends DialogBox{
		public SelectWindow(){
			super();
			
			// Set the dialog box's caption.
			setText("Select configuration");

			// Enable animation.
			setAnimationEnabled(true);

			// Enable glass background.
			setGlassEnabled(true);
			
			//Disable modal. The user cant click in app(GlassEnabled true) and other startup errors can be show.
			//If modal is false. Others errors dont be show.
			setModal(false);
			
			Panel panel= new VerticalPanel();
			
			panel.add(new Label("To Do: Implements view that user can choose configuration"));
			panel.add(new Label("Press the button to go to default configuration (geolinkeddata)"));
			Button button=new Button("PUSH");
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					SelectConfigurationView.this.fireOnSelectedEvent();
				}
			});
			panel.add(button);
			DOM.setStyleAttribute(this.getElement(), "zIndex", "10000");
			setWidget(panel);
		}
	}
}
