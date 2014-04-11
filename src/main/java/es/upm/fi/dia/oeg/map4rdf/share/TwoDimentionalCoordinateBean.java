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
package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

import org.gwtopenmaps.openlayers.client.LonLat;


/**
 * @author Alexander De Leon
 */
public class TwoDimentionalCoordinateBean implements TwoDimentionalCoordinate, Serializable {

	private static final long serialVersionUID = 3381260503500449198L;
	private double x;
	private double y;
	private String projection;
	TwoDimentionalCoordinateBean() {
		// for serialization
	}

	@Override
	public Type getType() {
		return Type.POINT;
	}
	
	public TwoDimentionalCoordinateBean(double x, double y, String projection) {
		this.x = x;
		this.y = y;
		this.projection=projection;
	}
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public String getProjection() {
		
		return projection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + new Double(x).hashCode();
		result = prime * result + new Double(y).hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TwoDimentionalCoordinateBean other = (TwoDimentionalCoordinateBean) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return true;
	}
	
	//Can only be accessed in client mode
	public void transform(String from, String to) {
		LonLat tmp = new LonLat(x, y);	
		tmp.transform(from ,to);
		x=tmp.lon();
		y=tmp.lat();
		this.projection=to;
	}
	
}
