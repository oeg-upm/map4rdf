package es.upm.fi.dia.oeg.map4rdf.server.cartociudad.utils;

import java.util.List;

import es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point;

public class RequestMessage {	

	public static String getResquestMessage(List<Point> points) {
		String output = "";
		output = output + "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>\n";
		output = output + "<Execute service='WPS' version='0.4.0' store='false' status='false'\n";
		output = output + "xmlns='http://www.opengeospatial.net/wps'\n";
		output = output + "xmlns:pak='http://www.opengis.net/examples/packet'\n";
		output = output + "xmlns:ows='http://www.opengeospatial.net/ows' xmlns:xlink='http://www.w3.org/1999/xlink'\n";
		output = output + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'\n";
		output = output + "xsi:schemaLocation='http://www.opengeospatial.net/wps\n";
		output = output + "..\\wpsExecute.xsd' xmlns:om='http://www.opengis.net/om' xmlns:gml='http://www.opengis.net/gml'><ows:Identifier>com.ign.process.geometry.RouteFinder</ows:Identifier>\n";
		output = output + "<DataInputs>\n";
		output = output + "<Input>\n";
		output = output + "<ows:Identifier>details</ows:Identifier>\n";
		output = output + "<ows:Title>Distancia</ows:Title>\n";
		output = output + "<LiteralValue dataType='xs:boolean'>true</LiteralValue>\n";
		output = output + "</Input>\n";
		output = output + "<Input>\n";
		output = output + "<ows:Identifier>wayPointList</ows:Identifier>\n";
		output = output + "<ows:Title>WayPoints</ows:Title>\n";
		output = output + "<ComplexValue>\n";
		output = output + "<wayPointList>\n";
		for (int i=0;i<points.size();i++){
			Point point = points.get(i);			
			output = output + "<wayPoint>\n";
			output = output + "<gml:Point xmlns:gml='www.opengeospatial.net/gml'><gml:coord>" +
						"<gml:X>"+point.getLat()+"</gml:X>" +
						"<gml:Y>"+point.getLon()+"</gml:Y>" +
					"</gml:coord></gml:Point>\n";
			output = output + "</wayPoint>\n";
		}		
		output = output + "</wayPointList>\n";
		output = output + "</ComplexValue>\n";
		output = output + "</Input>\n";
		output = output + "</DataInputs>\n";
		output = output + "<ProcessOutputs>\n";
		output = output + "<Output>\n";
		output = output + "<ows:Identifier>result</ows:Identifier>\n";
		output = output + "<ows:Title>LineString</ows:Title>\n";
		output = output + "<ows:Abstract>GML describiendo una feature de Linestring.</ows:Abstract>\n";
		output = output + "<ComplexOutput defaultFormat='text/XML'\n";
		output = output + "defaultSchema='http://geoserver.itc.nl:8080/wps/schemas/gml/2.1.2/gmlpacket.xsd'>\n";
		output = output + "<SupportedComplexData>\n";
		output = output + "<Schema>http://schemas.opengis.net/gml/2.1.2/feature.xsd</Schema>\n";
		output = output + "</SupportedComplexData>\n";
		output = output + "</ComplexOutput>\n";
		output = output + "</Output>\n";
		output = output + "<Output>\n";
		output = output + "<ows:Identifier>route</ows:Identifier>\n";
		output = output + "<ows:Title>Ruta</ows:Title>\n";
		output = output + "<ows:Abstract>Ruta</ows:Abstract>\n";
		output = output + "<ComplexOutput defaultFormat='text/XML'\n";
		output = output + "defaultSchema='http://www.idee.es/complexValues.xsd'>\n";
		output = output + "<SupportedComplexData>\n";
		output = output + "<Schema>http://www.idee.es/complexValues.xsd</Schema>\n";
		output = output + "</SupportedComplexData>\n";
		output = output + "</ComplexOutput>\n";
		output = output + "</Output>\n";
		output = output + "<Output>\n";
		output = output + "<ows:Identifier>wayPoints</ows:Identifier>\n";
		output = output + "<ows:Title>Puntos</ows:Title>\n";
		output = output + "<ows:Abstract>Lista de puntos</ows:Abstract>\n";
		output = output + "<ComplexOutput defaultFormat='text/XML'\n";
		output = output + "defaultSchema='http://www.idee.es/wayPointsValues.xsd'>\n";
		output = output + "<SupportedComplexData>\n";
		output = output + "<Schema>http://www.idee.es/wayPointsValues.xsd</Schema>\n";
		output = output + "</SupportedComplexData>\n";
		output = output + "</ComplexOutput>\n";
		output = output + "</Output>\n";
		output = output + "</ProcessOutputs>\n";
		output = output + "</Execute>\n";
		return output;
	}
}
