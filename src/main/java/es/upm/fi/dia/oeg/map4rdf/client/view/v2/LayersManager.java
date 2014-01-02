package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import org.gwtopenmaps.openlayers.client.Bounds;

import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

public class LayersManager {

	private static final String IDEE_URL = "http://www.idee.es/wms-c/IDEE-Base/IDEE-Base";
	private static final String OTALEX_URL = "http://www.ign.es/wms-inspire/ign-base";
	private static final String OL_URL = "http://vmap0.tiles.osgeo.org/wms/vmap0";
	private static final String CARTOCIUDAD_URL = "http://www.cartociudad.es/wms-c/CARTOCIUDAD/CARTOCIUDAD";
	public static WMS getIdeeLayer(double[] resolutions){
		
		WMSParams wmsParams = new WMSParams();
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsParams.setLayers("Todas");
		//wmsLayerParams.setMaxExtent(new Bounds(-50, -50, 50, 50));
		wmsLayerParams.setAttribution("Maps provided by <a href =\"http://www.idee.es\">IDEE</a>");
		wmsLayerParams.setResolutions(resolutions);
		WMS wmsLayer = new WMS("IDEE", IDEE_URL, wmsParams, wmsLayerParams);
		return wmsLayer;
	}
	public static WMS getCartociudadLayer(double[] resolutions){
		WMSParams wmsParams = new WMSParams();
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsParams.setLayers("Todas");
		wmsLayerParams.setNumZoomLevels(20);
		wmsLayerParams.setProjection("EPSG:4258");
		//wmsLayerParams.setMaxExtent(new Bounds(-50, -50, 50, 50));
		wmsLayerParams.setAttribution("Maps provided by <a href =\"http://www.cartociudad.es\">CartoCiudad </a>");
		wmsLayerParams.setResolutions(resolutions);
		WMS wmsLayer = new WMS("CartoCiudad", CARTOCIUDAD_URL, wmsParams, wmsLayerParams);
		return wmsLayer;
	}
	public static WMS newIDEE(double[] resolutions){
		WMSParams wmsParams = new WMSParams();
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsParams.setLayers("IGNBaseTodo");
		//wmsLayerParams.setMaxExtent(new Bounds(-50, -50, 50, 50));
		wmsLayerParams.setAttribution("Maps provided by <a href =\"http://www.idee.es\">IDEE</a>");
		wmsLayerParams.setResolutions(resolutions);
		WMS wmsLayer = new WMS("IDEE", OTALEX_URL, wmsParams, wmsLayerParams);
		return wmsLayer;
	}
	public static OSM getOpenStreetMapsLayer(Bounds bounds) {
		OSMOptions options = new OSMOptions();
		options.setMaxExtent(bounds);
		options.setNumZoomLevels(20);
		//OSM openStreetMap = OSM.Mapnik("Open Street Maps",options);
		OSM other = new OSM("Open Street Maps", "http://a.tile.openstreetmap.org/${z}/${x}/${y}.png", options);
		//return openStreetMap;
		return other;
	}
	public static WMS getOpenLayersSphericalLayer(){
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("basic");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setProjection("EPSG:900913");
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		WMS wmsLayer = new WMS(
				"Open Layers Maps",
				OL_URL,wmsParams,wmsLayerParams);
		return wmsLayer;
	}
	
	public static WMS getOpenLayersFlatLayer() {
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("Vmap0");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		WMS wmsLayer = new WMS(
				"Open Layers Maps",
				OL_URL,wmsParams,wmsLayerParams);
		return wmsLayer;
	}
	
	public static WMS getOpenLayersFlatBasicLayer() {
		WMSParams wmsParams = new WMSParams();
		wmsParams.setFormat("image/png");
		wmsParams.setLayers("basic");
		WMSOptions wmsLayerParams = new WMSOptions();
		wmsLayerParams.setTransitionEffect(TransitionEffect.RESIZE);
		WMS wmsLayer = new WMS(
				"Open Layers Maps (Basic)",
				OL_URL,wmsParams,wmsLayerParams);
		return wmsLayer;
	}
	
	public static GoogleV3 getGoogleLayer(Bounds bounds){
		GoogleV3Options googleOptions = new GoogleV3Options();
		googleOptions.setType(GoogleV3MapType.G_NORMAL_MAP);
		googleOptions.setSphericalMercator(true);
		googleOptions.setMaxExtent(bounds);
		googleOptions.setNumZoomLevels(20);
		//googleOptions.setMaxExtent(bounds);
		GoogleV3 google = new GoogleV3("Google Maps", googleOptions);
		return google;
	}
}
