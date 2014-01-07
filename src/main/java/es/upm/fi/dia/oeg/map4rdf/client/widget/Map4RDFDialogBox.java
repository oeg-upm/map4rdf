package es.upm.fi.dia.oeg.map4rdf.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Map4RDFDialogBox extends DialogBox {
	private Label mainLabel;
	private Panel imagePanel;
	private ImageResource doneImage;
	private ImageResource errorImage;
	public Map4RDFDialogBox(String tittle,String label, ImageResource doneImage,ImageResource errorImage) {
		this.doneImage = doneImage;
		this.errorImage = errorImage;
		
		// Set the dialog box's caption.
		setText(tittle);

		// Enable animation.
		setAnimationEnabled(true);

		// Enable glass background.
		setGlassEnabled(true);

		// DialogBox is a SimplePanel, so you have to set its widget
		// property to whatever you want its contents to be.
		Button ok = new Button("OK");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Map4RDFDialogBox.this.hide();
			}
		});

		mainLabel = new Label(label);

		VerticalPanel panel = new VerticalPanel();
		panel.setHeight("140");
		panel.setWidth("300");
		setPopupPosition(Window.getClientWidth()/2, Window.getClientHeight()/2);
		panel.setSpacing(10);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		HorizontalPanel messagePanel= new HorizontalPanel();
		imagePanel = new FlowPanel();
		messagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		messagePanel.add(imagePanel);
		messagePanel.add(mainLabel);
		panel.add(messagePanel);
		panel.add(ok);

		setWidget(panel);
		
		DOM.setStyleAttribute(this.getElement(), "zIndex", "10000");
	}
	public void setLabel(String label){
		mainLabel.setText(label);
	}
	public String getLabel(){
		return mainLabel.getText();
	}
	@Override
	public void hide(){
		super.hide();
		/*imagePanel.clear();
		mainLabel.setText("");*/
	}
	public void show(String text){
		imagePanel.clear();
		mainLabel.setText(text);
		super.show();
	}
	public void showDone(String text){
		imagePanel.clear();
		imagePanel.add(new Image(doneImage));
		mainLabel.setText(text);
		super.show();
	}
	public void showError(String text){
		imagePanel.clear();
		imagePanel.add(new Image(errorImage));
		mainLabel.setText(text);
		super.show();
	}
}
