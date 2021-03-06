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
package es.upm.fi.dia.oeg.map4rdf.client.widget.event;

import com.google.gwt.event.shared.GwtEvent;

public class FacetValueSelectionChangedEvent extends GwtEvent<FacetValueSelectionChangedHandler> {

	private static GwtEvent.Type<FacetValueSelectionChangedHandler> TYPE;

	public static GwtEvent.Type<FacetValueSelectionChangedHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<FacetValueSelectionChangedHandler>();
		}
		return TYPE;
	}

	private final String selectionOptionId;
	private final boolean selectionValue;
	private final String hexColour;
	public FacetValueSelectionChangedEvent(String hexColour,String selectionOptionId, boolean selectionValue) {
		super();
		this.hexColour=hexColour;
		this.selectionOptionId = selectionOptionId;
		this.selectionValue = selectionValue;
	}

	public String getSelectionOptionId() {
		return selectionOptionId;
	}

	public boolean getSelectionValue() {
		return selectionValue;
	}
	public String getHexColour(){
		return hexColour;
	}

	@Override
	protected void dispatch(FacetValueSelectionChangedHandler handler) {
		handler.onSelectionChanged(this);
	}

	@Override
	public GwtEvent.Type<FacetValueSelectionChangedHandler> getAssociatedType() {
		return getType();
	}

}