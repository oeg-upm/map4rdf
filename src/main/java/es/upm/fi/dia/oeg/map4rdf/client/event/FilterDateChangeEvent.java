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
package es.upm.fi.dia.oeg.map4rdf.client.event;

import com.google.gwt.event.shared.GwtEvent;

import es.upm.fi.dia.oeg.map4rdf.client.util.DateFilter;

import java.util.List;

/**
 * @author Alexander De Leon
 */
public class FilterDateChangeEvent extends GwtEvent<FilterDateChangeEventHandler> {

	private static GwtEvent.Type<FilterDateChangeEventHandler> TYPE;

	private final List<DateFilter> dateFilters;

	public FilterDateChangeEvent(List<DateFilter> dateFilters) {
		this.dateFilters = dateFilters;
	}

	public static GwtEvent.Type<FilterDateChangeEventHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<FilterDateChangeEventHandler>();
		}
		return TYPE;
	}

	public List<DateFilter> getDateFilters() {
		return dateFilters;
	}



	@Override
	public GwtEvent.Type<FilterDateChangeEventHandler> getAssociatedType() {
		return getType();
	}



	@Override
	protected void dispatch(FilterDateChangeEventHandler handler) {
		handler.onYearChange(this);
	}

}
