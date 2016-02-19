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
package es.upm.fi.dia.oeg.map4rdf.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.presenter.FacetPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.widget.FacetWidget;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedEvent;
import es.upm.fi.dia.oeg.map4rdf.client.widget.event.FacetValueSelectionChangedHandler;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigurationDrawColoursBy;
import es.upm.fi.dia.oeg.map4rdf.share.Facet;
import es.upm.fi.dia.oeg.map4rdf.share.FacetGroup;

/**
 * @author Alexander De Leon
 */
public class FacetView extends Composite implements FacetPresenter.Display {

	private FlowPanel panel;
	private final BrowserResources resources;
	private FacetSelectionHandler handler;
	private ConfigurationDrawColoursBy drawColoursBy=ConfigurationDrawColoursBy.getDefault();
	private ScrollPanel scrollMainPanel;
	private List<FacetGroup> facetsGroup = new ArrayList<FacetGroup>();
	
	private List<FacetWidget> facets;
	@Inject
	public FacetView(BrowserResources resources) {
		this.resources = resources;
		this.facets = new ArrayList<FacetWidget>();
		initWidget(createUi());
		addStyleName(resources.css().facets());
		panel.getElement().getParentElement().getStyle().setProperty("minHeight", "100%");
		panel.getElement().getStyle().setProperty("minHeight", "100%");
	}

	@Override
	public void setFacets(List<FacetGroup> facets) {
		if(!this.isAttached() || !this.isVisible() || this.getParent()==null || this.getParent().getOffsetHeight()==0){
			this.facetsGroup = facets;
			return;
		}
		FacetWidget facet;
		this.facets.clear();
		Map<String,FacetWidget> facetsWidgetToOrder = new HashMap<String, FacetWidget>();
		Map<String,Integer> facetsOrderInt = new HashMap<String, Integer>();
		int totalFacets=0;
		int maxSizeFacetGroup=totalFacets=0;
		for(FacetGroup facetDefinition: facets){
			totalFacets += facetDefinition.getFacets().size();
			if(facetDefinition.getFacets().size()>maxSizeFacetGroup){
				maxSizeFacetGroup = facetDefinition.getFacets().size();
			}
		}
		double perCentOfMax = totalFacets / maxSizeFacetGroup;
		if(perCentOfMax<40.0){
			perCentOfMax = 40.0;
		}
		int totalSizePX = this.getParent().getOffsetHeight();
		for (final FacetGroup facetDefinition : facets) {
			facet = new FacetWidget(resources.css(),drawColoursBy);
			this.facets.add(facet);
			// newSize = (maxPercet * num_of_facets) / max_size_facets_of_facetsGroup;
			// the big facet group is 40% or more;
			double newSize = ((double)(perCentOfMax * facetDefinition.getFacets().size())/(double)maxSizeFacetGroup);
			long newSizePX = Math.round((totalSizePX * newSize)/100.0);
			facet.setHeight(newSizePX+"px",facetDefinition.getFacets().size());
			facet.setLabel(LocaleUtil.getBestLabel(facetDefinition));
			for (Facet facetValue : facetDefinition.getFacets()) {
				String label = LocaleUtil.getBestLabel(facetValue);
				facet.addFacetSelectionOption(facetValue.getUri(), label);
			}
			facet.sort();

			facet.addFacetValueSelectionChangedHandler(new FacetValueSelectionChangedHandler() {
				@Override
				public void onSelectionChanged(FacetValueSelectionChangedEvent event){
					if (handler != null) {
						handler.onFacetSelectionChanged(facetDefinition.getUri(),event.getHexColour(), event.getSelectionOptionId(),
									event.getSelectionValue());							
						
					}
				}
			});
			facetsWidgetToOrder.put(LocaleUtil.getBestLabel(facetDefinition), facet);
			facetsOrderInt.put(LocaleUtil.getBestLabel(facetDefinition), facetDefinition.getOrder());
		}
		Map<Integer,List<String>> orderOfFacetsGroups = new HashMap<Integer, List<String>>();
		for(String uri:facetsOrderInt.keySet()){
			int order = facetsOrderInt.get(uri);
			if(!orderOfFacetsGroups.containsKey(order)){
				orderOfFacetsGroups.put(order, new ArrayList<String>());
			}
			orderOfFacetsGroups.get(order).add(uri);
		}
		List<Integer> orderedListFacetsGroup = new ArrayList<Integer>(orderOfFacetsGroups.keySet());
		Collections.sort(orderedListFacetsGroup);
		for(int i=orderedListFacetsGroup.size()-1;i>=0;i--){
			List<String> orderedList = new ArrayList<String>(orderOfFacetsGroups.get(i));
			Collections.sort(orderedList);
			for(int j=0;j<orderedList.size();j++){
				panel.add(facetsWidgetToOrder.get(orderedList.get(j)));
			}
		}
	}
	@Override
	protected void onLoad(){
		if(facetsGroup != null && !facetsGroup.isEmpty()){
			setFacets(facetsGroup);
		}
		facetsGroup=null;
	}
	@Override
	public void setFacetSelectionChangedHandler(FacetSelectionHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setConfigurationDrawColours(ConfigurationDrawColoursBy drawColoursBy) {
		if(this.facets!=null && !this.facets.isEmpty()){
			for(FacetWidget facet:this.facets){
				facet.setConfigurationDrawColours(drawColoursBy);
			}
		}
		this.drawColoursBy = drawColoursBy;
	}

	/* ------------- Display API -- */
	@Override
	public Widget asWidget() {
		return this;
	}


	/* ---------------- helper methods -- */
	private Widget createUi() {
		panel = new FlowPanel();
		ScrollPanel parent = new ScrollPanel();
		parent.setHeight("100%");
		parent.add(panel);
		scrollMainPanel = parent;
		return parent;
	}

    @Override
    public void clear() {
        //panel.clear();
    }

	@Override
	public Boolean isEmpty() {
		
		return this.facets.isEmpty();
	}
}
