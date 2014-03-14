package es.upm.fi.dia.oeg.map4rdf.client.view.v2;

import java.util.ArrayList;
import java.util.List;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3MapType;
import org.gwtopenmaps.openlayers.client.layer.GoogleV3Options;
import org.gwtopenmaps.openlayers.client.layer.Layer;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.TransitionEffect;
import org.gwtopenmaps.openlayers.client.layer.WMS;
import org.gwtopenmaps.openlayers.client.layer.WMSOptions;
import org.gwtopenmaps.openlayers.client.layer.WMSParams;

import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.MapConfiguration;

public class LayersManager {
	private static WMS getWMS(MapConfiguration map, Bounds bounds, double[] resolutions){
		WMSParams wmsParams = new WMSParams();
		WMSOptions wmsOptions = new WMSOptions();
		if(map.haveLayers()){
			wmsParams.setLayers(map.getLayers());
		}
		if(map.haveAttribution()){
			wmsOptions.setAttribution(map.getAttribution());
		}
		if(map.haveResolution() && map.getResolution()){
			wmsOptions.setResolutions(resolutions);
		}
		if(map.haveProjection()){
			wmsOptions.setProjection(map.getProjection());
		}
		if(map.haveMaxExtends() && map.getMaxExtends()){
			wmsOptions.setMaxExtent(bounds);
		}
		if(map.haveFormat()){
			wmsParams.setFormat(map.getFormat());
		}
		if(map.haveTransitionEffect() && map.getTransitionEffect()){
			wmsOptions.setTransitionEffect(TransitionEffect.RESIZE);
		}
		if(map.haveNumZoomLevels()){
			wmsOptions.setNumZoomLevels(map.getNumZoomLevels());
		}
		WMS wmsLayer = new WMS(LocaleUtil.getBestLabel(map), map.getServiceURL(), wmsParams, wmsOptions);
		return wmsLayer;
	}
	private static OSM getOSM(MapConfiguration map, Bounds bounds, double[] resolutions){
		OSMOptions options = new OSMOptions();
		if(map.haveAttribution()){
			options.setAttribution(map.getAttribution());
		}
		if(map.haveResolution() && map.getResolution()){
			options.setResolutions(resolutions);
		}
		if(map.haveProjection()){
			options.setProjection(map.getProjection());
		}
		if(map.haveMaxExtends() && map.getMaxExtends()){
			options.setMaxExtent(bounds);
		}
		if(map.haveTransitionEffect() && map.getTransitionEffect()){
			options.setTransitionEffect(TransitionEffect.RESIZE);
		}
		if(map.haveNumZoomLevels()){
			options.setNumZoomLevels(map.getNumZoomLevels());
		}
		if(map.haveSphericalMercator()){
			options.setSphericalMercator(map.getSphericalMercator());
		}
		OSM osm;
		if(map.haveServiceURL()){
			 osm = new OSM(LocaleUtil.getBestLabel(map), map.getServiceURL() , options);
		}else{
			osm = OSM.Mapnik(LocaleUtil.getBestLabel(map),options);
		}
		return osm;
	}
	private static GoogleV3 getGoogleV3(MapConfiguration map, Bounds bounds, double[] resolutions){
		GoogleV3Options options = new GoogleV3Options();
		if(map.haveGMapType()){
			GoogleV3MapType googleType=GoogleV3MapType.valueOf(map.getgMapType().name());
			if(googleType!=null){
				options.setType(googleType);
			}
		}
		if(map.haveAttribution()){
			options.setAttribution(map.getAttribution());
		}
		if(map.haveResolution() && map.getResolution()){
			options.setResolutions(resolutions);
		}
		if(map.haveProjection()){
			options.setProjection(map.getProjection());
		}
		if(map.haveMaxExtends() && map.getMaxExtends()){
			options.setMaxExtent(bounds);
		}
		if(map.haveTransitionEffect() && map.getTransitionEffect()){
			options.setTransitionEffect(TransitionEffect.RESIZE);
		}
		if(map.haveNumZoomLevels()){
			options.setNumZoomLevels(map.getNumZoomLevels());
		}
		if(map.haveSphericalMercator()){
			options.setSphericalMercator(map.getSphericalMercator());
		}
		GoogleV3 google = new GoogleV3(LocaleUtil.getBestLabel(map), options);
		return google;
	}
	public static Layer[] getLayers(List<MapConfiguration> maps,Bounds bounds, double[] resolutions){
		List<Layer> layers=new ArrayList<Layer>();
		for(MapConfiguration map:maps){
			switch (map.getMapServiceType()) {
			case OSM:
				layers.add(getOSM(map, bounds, resolutions));
				break;
			case WMS:
				layers.add(getWMS(map, bounds, resolutions));
				break;
			case Google:
				layers.add(getGoogleV3(map, bounds, resolutions));
				break;
			default:
				break;
			}
		}
		Layer[] layersArray=new Layer[layers.size()];
		int i=0;
		for(Layer layer:layers){
			layersArray[i++]=layer;
		}
		return layersArray;
	}
}
