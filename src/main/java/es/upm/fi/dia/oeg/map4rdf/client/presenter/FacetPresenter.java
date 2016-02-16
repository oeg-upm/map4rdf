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
package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import java.util.ArrayList;
import java.util.List;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitionsResult;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.FacetConstraintsChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfigurationHandler;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.FacetPresenter.Display.FacetSelectionHandler;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigurationDrawColoursBy;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Alexander De Leon
 */
@Singleton
public class FacetPresenter extends ControlPresenter<FacetPresenter.Display> {

	public interface Display extends WidgetDisplay {

        public void clear();
        public Boolean isEmpty();
        
		interface FacetSelectionHandler {
			void onFacetSelectionChanged(String facetId,String hexColour, String facetValueId, boolean selected);
		}
                
		// TODO this should be decoupled from the model
		void setFacets(List<FacetGroup> facets);
		void setFacetSelectionChangedHandler(FacetSelectionHandler handler);
		void setConfigurationDrawColours(ConfigurationDrawColoursBy drawColoursBy);
	}
	private final ConfIDInterface configID;
	private final DispatchAsync dispatchAsync;
	private final List<FacetConstraint> constraints = new ArrayList<FacetConstraint>();
	private WidgetFactory widgetFactory;
	private BrowserMessages messages; 
	
	public List<FacetConstraint> getConstraints(){
		return this.constraints;
	}
	
	@Inject
	public FacetPresenter(ConfIDInterface configID,Display display, EventBus eventBus, DispatchAsync dispatchAsync, WidgetFactory widgetFactory, BrowserMessages messages) {
		super(display, eventBus);
		this.configID = configID;
		this.dispatchAsync = dispatchAsync;
		this.widgetFactory = widgetFactory;
		this.messages = messages;
    }

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
		getDisplay().setFacetSelectionChangedHandler(new FacetSelectionHandler() {
			@Override
			public void onFacetSelectionChanged(String facetId,String hexColour, String facetValueId, boolean selected) {
				if (selected) {
					constraints.add(new FacetConstraint(facetId,hexColour, facetValueId));
				} else {
					constraints.remove(new FacetConstraint(facetId,hexColour, facetValueId));
				}
				fireFacetConstrainsChanged();
			}
		});
		onRevealDisplay();
	}
	@Override
	protected void onUnbind() {
		

	}

	void loadFacets() {
		dispatchAsync.execute(new GetFacetDefinitions(configID.getConfigID()), new AsyncCallback<GetFacetDefinitionsResult>() {

			@Override
			public void onFailure(Throwable caught) {
				widgetFactory.getDialogBox().showError(messages.errorCommunication()+" Error:"+caught.getMessage());
			}

			@Override
			public void onSuccess(GetFacetDefinitionsResult result) {
				getDisplay().setFacets(result.getFacetDefinitions());
			}
		});
	}

	private void fireFacetConstrainsChanged() {
		eventBus.fireEvent(new FacetConstraintsChangedEvent(constraints));
	}
    
    public void clear() {
    	constraints.clear();
    	getDisplay().clear();
	}

	@Override
	protected void onRevealDisplay() {
		if(configID.existsConfigID()){
			if (getDisplay().isEmpty()) {
				loadConfigurationsParams();
			}
		}else{
			eventBus.addHandler(OnSelectedConfiguration.getType(), new OnSelectedConfigurationHandler() {
				
				@Override
				public void onSelectecConfiguration(String configID) {
					if (getDisplay().isEmpty()) {
						loadConfigurationsParams();
					}
				}
			});
		}
		
	}
	
	private void loadConfigurationsParams(){
		dispatchAsync.execute(new GetConfigurationParameter(configID.getConfigID(), ParameterNames.DRAW_COLOURS_BY), new AsyncCallback<SingletonResult<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				widgetFactory.getDialogBox().showError(messages.errorCommunication()+" Error:"+caught.getMessage());
			}

			@Override
			public void onSuccess(SingletonResult<String> result) {
				if (getDisplay().isEmpty()) {
					if(ConfigurationDrawColoursBy.isValid(result.getValue())){
						display.setConfigurationDrawColours(ConfigurationDrawColoursBy.valueOf(result.getValue()));
					}else{
						display.setConfigurationDrawColours(ConfigurationDrawColoursBy.getDefault());
					}
					loadFacets();
				}
			}
		});
	}
}
