package es.upm.fi.dia.oeg.map4rdf.server.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import es.upm.fi.dia.oeg.map4rdf.share.MapConfiguration;
import es.upm.fi.dia.oeg.map4rdf.share.MapConfiguration.GoogleV3MapServerType;
import es.upm.fi.dia.oeg.map4rdf.share.conf.MapsParametersNames;

public class MapsConfigurationServer {
	private ArrayList<MapConfiguration> mapsConfiguration=new ArrayList<MapConfiguration>();
	public MapsConfigurationServer(GetServletContext getServletContext,String sphericalMercator){
		String[] mapsFiles=null;
		String mapsFolder=null;
		String mapsPropertieName=null;
		if(sphericalMercator!=null && sphericalMercator.toLowerCase().equals("true")){
			mapsPropertieName=MapsParametersNames.SPHERICAL_MAPS;
			mapsFolder=Constants.SPHERICAL_MAPS_FOLDER;
		}else{
			mapsPropertieName=MapsParametersNames.FLAT_MAPS;
			mapsFolder=Constants.FLAT_MAPS_FOLDER;
		}
		Properties properties = new Properties();
		try {
			properties.load(getServletContext.getServletContext().getResourceAsStream(Constants.MAPS_CONFIGURATION_FILE));
			String mapsValue=properties.getProperty(mapsPropertieName);
			if(mapsValue!=null && !mapsValue.isEmpty()){
				mapsFiles=mapsValue.split(";");
			}else{
				System.err.println("Propertie "+mapsPropertieName+" is null or empty in "+Constants.MAPS_CONFIGURATION_FILE);
				return;
			}
		} catch (Exception e) {
			System.err.println("Can't obtain maps configuration file: "+Constants.MAPS_CONFIGURATION_FILE);
			e.printStackTrace();
		}
		if(mapsFiles==null || mapsFiles.length==0){
			return;
		}
		for(int i=0;i<mapsFiles.length;i++){
			MapConfiguration map=getIndividualMapConfiguration(getServletContext, mapsFolder+mapsFiles[i]);
			if(map!=null){
				mapsConfiguration.add(map);
			}
		}
	}
	private MapConfiguration getIndividualMapConfiguration(GetServletContext servletContext,String file){
		Properties properties = new Properties();
		try {
			if(servletContext.getServletContext()==null){
				System.err.println("Servlet Context is null");
			}
			properties.load(servletContext.getServletContext().getResourceAsStream(file));
		} catch (Exception e) {
			System.err.println("Can't obtain map file: "+file);
			e.printStackTrace();
			return null;
		}
		String typeString=properties.getProperty(MapsParametersNames.TYPE);
		MapConfiguration.MapServiceType type=null;
		if(typeString!=null && !typeString.isEmpty()){
			for(MapConfiguration.MapServiceType serviceType:MapConfiguration.MapServiceType.values()){
				if(typeString.toLowerCase().equals(serviceType.name().toLowerCase())){
					type=serviceType;
					break;
				}
			}	
		}
		if(type==null){
			System.err.println("File \""+file+"\" not contain a valid propertie: \""+MapsParametersNames.TYPE+"\"");
			return null;
		}
		MapConfiguration map=new MapConfiguration(file, type);
		String value=null;
		value=properties.getProperty(MapsParametersNames.LABELS);
		if(value!=null && !value.isEmpty()){
			Map<String,String> labels=getLabels(value,file);
			for(String locale:labels.keySet()){
				map.addLabel(locale, labels.get(locale));
			}
		}
		value=properties.getProperty(MapsParametersNames.URL);
		if(value!=null && !value.isEmpty()){
			map.setServiceURL(value);
		}else{
			if(type.equals(MapConfiguration.MapServiceType.WMS)){
				System.err.println("The map: "+ file + " need to have propertie "+MapsParametersNames.URL+ " because this map is WMS type.");
				return null;
			}
		}
		value=properties.getProperty(MapsParametersNames.SPHERICAL_MERCATOR);
		if(value!=null && !value.isEmpty()){
			if(!type.equals(MapConfiguration.MapServiceType.WMS)){
				if(value.toLowerCase().equals("true")){
					map.setSphericalMercator(true);
				}
				if(value.toLowerCase().equals("false")){
					map.setSphericalMercator(false);
				}
				if(!map.haveSphericalMercator()){
					System.err.println("Bad value of propertie: \""+MapsParametersNames.SPHERICAL_MERCATOR+"\" in map file: "+file);
				}
			}else{
				System.err.println("WARNING: WMS don't need propertie \""+MapsParametersNames.SPHERICAL_MERCATOR+ "\" in map file: "+file);
			}
		}
		value=properties.getProperty(MapsParametersNames.GTYPE);
		if(value!=null && !value.isEmpty()){
			if(type.equals(MapConfiguration.MapServiceType.Google)){
				for(GoogleV3MapServerType gMapType:GoogleV3MapServerType.values()){
					if(value.toLowerCase().equals(gMapType.name().toLowerCase())){
						map.setgMapType(gMapType);
						break;
					}
				}
			}else{
				System.err.println("WARNING: Only Google type can have propertie \""+MapsParametersNames.GTYPE+ "\" in map file: "+file);
			}
			if(!map.haveGMapType()){
				System.err.println("WARNING(Use default propertie): File \""+file+"\" not contain a valid propertie: \""+MapsParametersNames.GTYPE+"\"");
			}
		}
		value=properties.getProperty(MapsParametersNames.NUM_ZOOM_LEVELS);
		if(value!=null && !value.isEmpty()){
			try{
				int zoom=Integer.parseInt(value);
				if(zoom>0){
					map.setNumZoomLevels(zoom);
				}else{
					System.err.println("Propertie \""+MapsParametersNames.NUM_ZOOM_LEVELS+"\" need to be positive in map file: "+file);
				}
			}catch(Exception e){
				System.err.println("Can't parse propertie \""+MapsParametersNames.NUM_ZOOM_LEVELS +"\" to integer in map file: "+file);
				e.printStackTrace();
			}
			
		}
		value=properties.getProperty(MapsParametersNames.TRANSITION_EFFECT);
		if(value!=null && !value.isEmpty()){
			if(value.toLowerCase().equals("true")){
				map.setTransitionEffect(true);
			}
			if(value.toLowerCase().equals("false")){
				map.setTransitionEffect(false);
			}
			if(!map.haveTransitionEffect()){
				System.err.println("Bad value of propertie: \""+MapsParametersNames.TRANSITION_EFFECT+"\" in map file: "+file);
			}
		}
		value=properties.getProperty(MapsParametersNames.FORMAT);
		if(value!=null && !value.isEmpty()){
			if(type.equals(MapConfiguration.MapServiceType.WMS)){
				map.setFormat(value);
			}else{
				System.err.println("WARNING: Only WMS type can have propertie \""+MapsParametersNames.FORMAT+ "\" in map file: "+file);
			}
		}
		value=properties.getProperty(MapsParametersNames.SET_MAX_EXTENDS);
		if(value!=null && !value.isEmpty()){
			if(value.toLowerCase().equals("true")){
				map.setMaxExtends(true);
			}
			if(value.toLowerCase().equals("false")){
				map.setMaxExtends(false);
			}
			if(!map.haveMaxExtends()){
				System.err.println("Bad value of propertie: \""+MapsParametersNames.SET_MAX_EXTENDS+"\" in map file: "+file);
			}
		}
		value=properties.getProperty(MapsParametersNames.PROJECTION);
		if(value!=null && !value.isEmpty()){
			map.setProjection(value);
		}
		value=properties.getProperty(MapsParametersNames.SET_RESOLUTIONS);
		if(value!=null && !value.isEmpty()){
			if(value.toLowerCase().equals("true")){
				map.setResolution(true);
			}
			if(value.toLowerCase().equals("false")){
				map.setResolution(false);
			}
			if(!map.haveResolution()){
				System.err.println("Bad value of propertie: \""+MapsParametersNames.SET_RESOLUTIONS+"\" in map file: "+file);
			}
		}
		value=properties.getProperty(MapsParametersNames.ATTRIBUTION);
		if(value!=null && !value.isEmpty()){
			map.setAttribution(value);
		}
		value=properties.getProperty(MapsParametersNames.LAYERS);
		if(value!=null && !value.isEmpty()){
			if(type.equals(MapConfiguration.MapServiceType.WMS)){
				map.setLayers(value);
			}else{
				System.err.println("WARNING: Only WMS type could have propertie \""+MapsParametersNames.LAYERS+ "\" in map file: "+file);
			}
		}
		return map;
	}
	private Map<String,String>getLabels(String value,String mapFile){
		Map<String, String> toReturn=new HashMap<String, String>();
		String[] labels=value.split(";");
		for(String eachLabel:labels){
			String[] label=eachLabel.split("@");
			if(label.length==2){
				toReturn.put(label[1], label[0]);
			}else{
				System.err.println("Malformed label: "+eachLabel+" in map file: "+mapFile);
			}
		}
		return toReturn;
	}
	public ArrayList<MapConfiguration> getMapsConfiguration() {
		return mapsConfiguration;
	}
}
