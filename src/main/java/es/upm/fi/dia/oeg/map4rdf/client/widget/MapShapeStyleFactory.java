/**
 * Copyright (c) 2011 Alexander De Leon Battista
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

import es.upm.fi.dia.oeg.map4rdf.client.style.StyleMapShape;
import es.upm.fi.dia.oeg.map4rdf.client.util.DrawPointStyle;
import es.upm.fi.dia.oeg.map4rdf.share.Circle;
import es.upm.fi.dia.oeg.map4rdf.share.MapShape;
import es.upm.fi.dia.oeg.map4rdf.share.PolyLine;
import es.upm.fi.dia.oeg.map4rdf.share.Polygon;
import es.upm.fi.dia.oeg.map4rdf.share.WKTGeometry;

/**
 * @author Alexander De Leon
 */
public class MapShapeStyleFactory {

	private static final int STROKE_Polyline = 3;
	private static final int STROKE_Polygon = 2;
	private static final int STROKE_WKTGeometry = 2;
	
	public static StyleMapShape<PolyLine> createStyle(PolyLine polyLine) {
		StyleMapShapeImpl<PolyLine> style = new StyleMapShapeImpl<PolyLine>(polyLine);
		style.setStrokeWidth(STROKE_Polyline);
		return style;
	}
	public static StyleMapShape<PolyLine> createStyle(PolyLine polyLine, DrawPointStyle drawStyle) {
		StyleMapShapeImpl<PolyLine> style = new StyleMapShapeImpl<PolyLine>(polyLine);
		style.setStrokeWidth(STROKE_Polyline);
		style.setStrokeColor(drawStyle.getFacetHexColour());
		return style;
	}

	public static StyleMapShape<Polygon> createStyle(Polygon polygon) {
		StyleMapShapeImpl<Polygon> style = new StyleMapShapeImpl<Polygon>(polygon);
		style.setStrokeWidth(STROKE_Polygon);
		style.setFillColor(null);
		return style;
	}
	public static StyleMapShape<Polygon> createStyle(Polygon polygon,DrawPointStyle drawStyle) {
		StyleMapShapeImpl<Polygon> style = new StyleMapShapeImpl<Polygon>(polygon);
		style.setStrokeWidth(STROKE_Polygon);
		style.setFillColor(null);
		style.setStrokeColor(drawStyle.getFacetHexColour());
		return style;
	}
	
	public static StyleMapShape<WKTGeometry> createStyle(WKTGeometry wktGeometry,
			DrawPointStyle drawStyle) {
		StyleMapShapeImpl<WKTGeometry> style = new StyleMapShapeImpl<WKTGeometry>(wktGeometry);
		style.setStrokeWidth(STROKE_WKTGeometry);
		style.setFillColor(null);
		style.setStrokeColor(drawStyle.getFacetHexColour());
		return style;
	}

	public static StyleMapShape<Circle> createStyle(Circle circle) {
		StyleMapShapeImpl<Circle> style = new StyleMapShapeImpl<Circle>(circle);
		style.setStrokeColor("green");
		style.setFillColor("green");
		style.setFillOpacity(0.4);
		style.setStrokeOpacity(1.0);
		return style;
	}
	public static StyleMapShape<Circle> createStyle(Circle circle,DrawPointStyle drawStyle) {
		StyleMapShapeImpl<Circle> style = new StyleMapShapeImpl<Circle>(circle);
		style.setStrokeColor(drawStyle.getFacetHexColour());
		style.setFillColor(drawStyle.getFacetHexColour());
		style.setFillOpacity(0.4);
		style.setStrokeOpacity(1.0);
		return style;
	}
	
	private static class StyleMapShapeImpl<E extends MapShape> implements StyleMapShape<E> {

		private final E mapShape;
		private String strokeColor = "#0000FF";
		private int strokeWidth = 1;
		private String fillColor = null;
		private double strokeOpacity = 1;
		private double fillOpacity = 0.5;

		public StyleMapShapeImpl(E mapShape) {
			this.mapShape = mapShape;
		}

		@Override
		public E getMapShape() {
			return mapShape;
		}

		@Override
		public String getStrokeColor() {
			return strokeColor;
		}

		public StyleMapShape<E> setStrokeColor(String strokeColor) {
			this.strokeColor = strokeColor;
			return this;
		}

		@Override
		public int getStrokeWidth() {
			return strokeWidth;
		}

		public StyleMapShape<E> setStrokeWidth(int strokeWidth) {
			this.strokeWidth = strokeWidth;
			return this;
		}

		@Override
		public String getFillColor() {
			return fillColor;
		}

		public StyleMapShape<E> setFillColor(String fillColor) {
			this.fillColor = fillColor;
			return this;
		}

		@Override
		public double getStrokeOpacity() {
			return strokeOpacity;
		}

		public StyleMapShape<E> setStrokeOpacity(double strokeOpacity) {
			this.strokeOpacity = strokeOpacity;
			return this;
		}

		@Override
		public double getFillOpacity() {
			return fillOpacity;
		}

		public StyleMapShape<E> setFillOpacity(double fillOpacity) {
			this.fillOpacity = fillOpacity;
			return this;
		}

	}
}
