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
import java.util.Date;
import java.util.List;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.FilterDateChangeEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfigurationHandler;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.FiltersPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.DateFilter;
import es.upm.fi.dia.oeg.map4rdf.client.util.DateFilter.DateFilterType;
import es.upm.fi.dia.oeg.map4rdf.client.widget.DatePickerWithYearSelector;
import es.upm.fi.dia.oeg.map4rdf.client.widget.WidgetFactory;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import es.upm.fi.dia.oeg.map4rdf.share.conf.SharedGeometryModels;

/**
 * @author Filip
 */
public class FiltersView extends Composite implements FiltersPresenter.Display {

	public interface Stylesheet {
		String dateFilterBox();
		String dateFilterPanel();
		String dateFilterTitle();
	}
	private final BrowserMessages messages;
	private final BrowserResources resources;
	private final EventBus eventBus;
	private final DispatchAsync dispatchAsync;
	private ConfIDInterface configID;
	private WidgetFactory widgetFactory;
	
	private FlowPanel panel;
	private FlowPanel dateFilterPanel;
	private PushButton clearButton;
	private ToggleButton drawButton;
	private List<DateFilter> dateFilters;
	
	
	@Inject
	public FiltersView(ConfIDInterface configID,BrowserMessages messages, BrowserResources resources,DispatchAsync dispatchAsync, EventBus eventBus,
			WidgetFactory widgetFactory) {
		this.resources = resources;
		this.messages = messages;
		this.eventBus = eventBus;
		this.widgetFactory = widgetFactory;
		this.dispatchAsync = dispatchAsync;
		this.configID = configID;
		this.dateFilters = new ArrayList<DateFilter>();
		initWidget(createUi());
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
		
	}
	private void initAsync(){
		dispatchAsync.execute(new GetConfigurationParameter(configID.getConfigID(),ParameterNames.GEOMETRY_MODEL), new AsyncCallback<SingletonResult<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(SingletonResult<String> result) {
				if(SharedGeometryModels.WEBNMASUNO.equalsIgnoreCase(result.getValue())){
					addYearDatePicker();
				}	
			}
			
		});
	}

	/* ------------- Display API -- */
	@Override
	public Widget asWidget() {
		return this;
	}

	/* ---------------- helper methods -- */
	private Widget createUi() {
		panel = new FlowPanel();
		Grid grid = new Grid(1, 3);

		drawButton = new ToggleButton(new Image(resources.pencilIcon()));
		drawButton.setSize("20px","20px");
		clearButton = new PushButton(new Image(resources.eraserIcon()));
		clearButton.setSize("20px","20px");
		
		grid.setWidget(0, 1, drawButton);
		grid.setWidget(0, 2, clearButton);
		grid.setWidget(0, 0, new Label(messages.draw()+": "));
		
		panel.add(grid);

		return panel;
	}

    @Override
    public void clear() {
        panel.clear();
    }


	@Override
	public ToggleButton getDrawButton() {
		return this.drawButton;
	}


	@Override
	public PushButton getClearButton() {
		return this.clearButton;
	}

	private void addYearDatePicker(){
		dateFilterPanel=new FlowPanel();
		dateFilterPanel.setStyleName(resources.css().dateFilterPanel());
		panel.add(dateFilterPanel);
	    DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd-MM-yyyy");
	    DateBox dateBox = new DateBox(new DatePickerWithYearSelector(), new Date(), new DateBox.DefaultFormat(dateFormat));
	    final ListBox comboBox=new ListBox();
	    comboBox.setMultipleSelect(false);
	    comboBox.addItem(messages.equalsTo(),DateFilter.DateFilterType.EQUAL.name());
	    comboBox.addItem(messages.beforeTo(),DateFilter.DateFilterType.BEFORE.name());
	    comboBox.addItem(messages.afterTo(),DateFilter.DateFilterType.AFTER.name());
	    comboBox.addItem(messages.beforeOrEqualsTo(),DateFilter.DateFilterType.BEFORE_OR_EQUAL.name());
	    comboBox.addItem(messages.afterOrEqualsTo(),DateFilter.DateFilterType.AFTER_OR_EQUAL.name());
	    Button addFilterButton=new Button(messages.addFilter());
	    Label title = new Label(messages.addADateFilter());
	    title.setStyleName(resources.css().dateFilterTitle());
	    dateFilterPanel.add(title);
	    dateFilterPanel.add(comboBox);
	    dateFilterPanel.add(dateBox);
	    dateFilterPanel.add(addFilterButton);
	    FlowPanel filtersPanel=new FlowPanel();
	    dateFilterPanel.add(filtersPanel);
	    addFilterChange(filtersPanel, addFilterButton, comboBox, dateBox);
	    
	}
	private void addFilterChange(final FlowPanel filters,final Button button, final ListBox comboBox, final DateBox dateBox){
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String filterTypeString=comboBox.getValue(comboBox.getSelectedIndex());
				DateFilterType filterType= DateFilterType.valueOf(filterTypeString);
				if(filterType!=null){
					addFilter(filters, new DateFilter(dateBox.getValue(), filterType));
				}
			}
		});
	}
	private void addFilter(FlowPanel filters,DateFilter dateFilter){
		if(dateFilters.contains(dateFilter)){
			widgetFactory.getDialogBox().showError(messages.existsOtherDateFilterEqual());
		}else{
			HorizontalPanel dateFilterPanel = new HorizontalPanel();
			dateFilterPanel.setSpacing(5);
			dateFilterPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			dateFilterPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			dateFilterPanel.setStyleName(resources.css().dateFilterBox());
			String message=getDateFilterTypeMessage(dateFilter.getFilter());
			if(message!=null){
				dateFilters.add(dateFilter);
				dateFilterPanel.add(new Label(message));
				dateFilterPanel.add(new Label(dateTimeFormater(dateFilter.getDate())));
				Image removeImage = new Image(resources.eraserIcon());
				removeImage.getElement().getStyle().setCursor(Cursor.POINTER);
				dateFilterPanel.add(removeImage);
				addRemoveFilterEvent(removeImage, filters, dateFilterPanel, dateFilter);
				filters.add(dateFilterPanel);
				fireDateFilterChangeEvent();
			}else{
				widgetFactory.getDialogBox().showError(messages.errorFilterType());
			}
		}
	}
	private void addRemoveFilterEvent(Image removeImage,final FlowPanel filters, final HorizontalPanel panelToRemove, final DateFilter dateFilterToRemove){
		removeImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				filters.remove(panelToRemove);
				dateFilters.remove(dateFilterToRemove);
				fireDateFilterChangeEvent();
			}
		});
	}
	private void fireDateFilterChangeEvent(){
		eventBus.fireEvent(new FilterDateChangeEvent(dateFilters));
	}
	private String getDateFilterTypeMessage(DateFilterType filterType){
		switch (filterType) {
		case AFTER:
			return messages.afterTo();
		case AFTER_OR_EQUAL:
			return messages.afterOrEqualsTo();
		case BEFORE:
			return messages.beforeTo();
		case BEFORE_OR_EQUAL:
			return messages.beforeOrEqualsTo(); 
		case EQUAL:
			return messages.equalsTo();
		default:
			return null;
		}
	}
	private String dateTimeFormater(Date date){
		DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd-MM-yyyy");
		String dateFilter=dateFormat.format(date);
		return dateFilter;
	}
}
