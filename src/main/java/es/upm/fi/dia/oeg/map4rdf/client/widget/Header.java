/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
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
package es.upm.fi.dia.oeg.map4rdf.client.widget;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigLogotype;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigLogotypeResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfigurationHandler;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

/**
 * @author Alexander De Leon
 */
public class Header extends Composite {
	
	private Image logo;
	private DispatchAsync dispatchAsync;
	private ConfIDInterface configID;
	private WidgetFactory widgetFactory;
	private BrowserMessages browserMessages;
	
	public Header(BrowserResources resources, BrowserMessages messages, ConfIDInterface configID, DispatchAsync dispatchAsync, EventBus eventBus, WidgetFactory widgetFactory) {
		this.dispatchAsync = dispatchAsync;
		this.configID = configID;
		this.widgetFactory = widgetFactory;
		this.browserMessages = messages;
		initWidget(createUi(resources));
		if(configID.existsConfigID()){
			initAsync();
		}else{
			eventBus.addHandler(OnSelectedConfiguration.getType(), new OnSelectedConfigurationHandler() {
				@Override
				public void onSelectecConfiguration(String configID) {
					initAsync();
				}
			});
		}
		addStyleName(resources.css().header());
	}
	private void initAsync(){
		dispatchAsync.execute(new GetConfigLogotype(configID.getConfigID()),new AsyncCallback<GetConfigLogotypeResult>() {

			@Override
			public void onFailure(Throwable caught) {
				widgetFactory.getDialogBox().showError(browserMessages.errorCommunication()+": "+caught.getMessage());
			}

			@Override
			public void onSuccess(GetConfigLogotypeResult result) {
				RootPanel.get().remove(logo);
				logo = new Image(GWT.getHostPageBaseURL() + result.getLogo());
				RootPanel.get().add(logo);
				RootPanel.get().setWidgetPosition(logo, 4, 0);
				logo.getElement().getStyle().setZIndex(3);
			}
		});
	}
	private Widget createUi(BrowserResources resources) {
		LayoutPanel panel = new LayoutPanel();
		logo = new Image(GWT.getHostPageBaseURL() + "logo.png");
		Image betaBadge = new Image(resources.betaBadge());

		// Add the logo to the root panel
		RootPanel.get().add(logo);
		RootPanel.get().setWidgetPosition(logo, 4, 0);
		logo.getElement().getStyle().setZIndex(3);

		panel.add(betaBadge);
		panel.setWidgetRightWidth(betaBadge, 2, Unit.EM, 48, Unit.PX);
		panel.setWidgetTopHeight(betaBadge, 2, Unit.EM, 48, Unit.PX);

		return panel;
	}

}
