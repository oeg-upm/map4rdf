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



import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ToggleButton;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterClearEvent;
import es.upm.fi.dia.oeg.map4rdf.client.event.AreaFilterModeChangeEvent;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;

/**
 * @author Filip
 */
@Singleton
public class FiltersPresenter extends  ControlPresenter<FiltersPresenter.Display>  {

	public interface Display extends WidgetDisplay {
        public void clear();
        public ToggleButton getDrawButton();
        public PushButton getClearButton();
	}
	
	@Inject
	public FiltersPresenter(Display display, EventBus eventBus) {
		super(display, eventBus);
		onBind();
	}

	/* -------------- Presenter callbacks -- */
	@Override
	protected void onBind() {
		
		getDisplay().getClearButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				clear();
				eventBus.fireEvent(new AreaFilterModeChangeEvent(false));	
				eventBus.fireEvent(new AreaFilterClearEvent());	
			}
		});
		getDisplay().getDrawButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Boolean drawValue = getDisplay().getDrawButton().isDown();
				eventBus.fireEvent(new AreaFilterModeChangeEvent(drawValue));	
			}
		});
	}

	public void clear() {
		getDisplay().getDrawButton().setDown(false);
	}
	
	@Override
	protected void onUnbind() {
		

	}

	@Override
	protected void onRevealDisplay() {
		
		
	}

}
