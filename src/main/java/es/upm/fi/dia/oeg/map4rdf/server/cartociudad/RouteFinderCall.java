package es.upm.fi.dia.oeg.map4rdf.server.cartociudad;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point;
import es.upm.fi.dia.oeg.map4rdf.server.cartociudad.utils.RequestMessage;

public class RouteFinderCall {

	private String urlService = "";
	private int timeout = 0;
	private Logger logger= Logger.getLogger(RouteFinderCall.class);
	public RouteFinderCall(String urlService) {
		super();
		this.urlService = urlService;
	}
	
	public RouteFinderCall(String urlService, int timeout) {
		super();
		this.urlService = urlService;
		this.timeout = timeout;
	}

	public List<List<Point>> getPath(List<Point> points) {
		List<List<Point>> paths = new ArrayList<List<Point>>();
		try {
			URL url = new URL(urlService);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (timeout != 0){
				conn.setConnectTimeout(timeout);
				conn.setReadTimeout(timeout);
			}			

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=UTF-8");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn
					.getOutputStream());

			writer.write(RequestMessage.getResquestMessage(points));
			writer.flush();

			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String output = "";

			while ((line = reader.readLine()) != null) {
				output = output + line + "\n";
			}
			writer.close();

			reader.close();

			if (!output.equals("")) {
				paths = parseXML(points, output);
			}

		} catch (MalformedURLException e) {
			logger.error("ERROR DURING URL CREATION::: ", e);
			return null;
		} catch (SocketTimeoutException ste){
			//logger.error("SOCKECT TIMEOUT EXCEPTION::: ",ste);
		} catch (IOException e) {
			logger.error("ERROR DURING REPOSITORY COMMUNICATION::: ", e);
			return null;
		} 
		return paths;
	}

	private List<List<Point>> parseXML(List<Point> points, String xmlText) {

		List<List<Point>> output = new ArrayList<List<Point>>();

		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			InputStream is = new ByteArrayInputStream(xmlText.getBytes());
			Document dom = db.parse(is);

			Element docEle = dom.getDocumentElement();

			NodeList nl = docEle.getElementsByTagName("wps:ProcessOutputs");
			nl = docEle.getElementsByTagName("pac:StaticFeature");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {

					Element el = (Element) nl.item(i);
					NodeList attribute = el
							.getElementsByTagName("gml:Coordinates");
					if (attribute.getLength() > 0) {
						Element elj = (Element) attribute.item(0);
						String fullString = elj.getFirstChild().getNodeValue();
						fullString = fullString.replace(",", "");
						StringTokenizer st = new StringTokenizer(fullString,
								" ");
						List<Point> list = new ArrayList<Point>();
						list.add(points.get(i));
						while (st.hasMoreElements()) {
							String lat = st.nextToken();
							String lon = st.nextToken();
							Point point = new Point(lat, lon);
							list.add(point);
						}
						list.add(points.get(i + 1));
						output.add(list);
					}

				}
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return output;
	}
}
