package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

public class MapConfiguration extends Resource implements Serializable{
	
	private static final long serialVersionUID = -5528841093281739208L;
	
	private String layers;
	private String attribution;
	private Boolean resolution;
	private String projection;
	private Boolean maxExtends;
	private String format;
	private Boolean transitionEffect;
	private int numZoomLevels=-1;
	private GoogleV3MapServerType gMapType;
	private Boolean sphericalMercator;
	private String serviceURL;
	private MapServiceType mapServiceType;
	
	public enum MapServiceType{
		WMS,OSM,Google;
	}
	public enum GoogleV3MapServerType{
		G_HYBRID_MAP,G_NORMAL_MAP,G_SATELLITE_MAP,G_TERRAIN_MAP;
	}
	MapConfiguration() {
		//for serialization
	}
	public MapConfiguration(String fileID,MapServiceType type) {
		super(fileID);
		this.mapServiceType=type;
	}
	public boolean haveLayers(){
		return layers!=null;
	}
	public boolean haveAttribution(){
		return attribution!=null;
	}
	public boolean haveResolution(){
		return resolution!=null;
	}
	public boolean haveProjection(){
		return projection!=null;
	}
	public boolean haveMaxExtends(){
		return maxExtends!=null;
	}
	public boolean haveFormat(){
		return format!=null;
	}
	public boolean haveTransitionEffect(){
		return transitionEffect!=null;
	}
	public boolean haveNumZoomLevels(){
		return numZoomLevels>0;
	}
	public boolean haveGMapType(){
		return gMapType!=null;
	}
	public boolean haveSphericalMercator(){
		return sphericalMercator!=null;
	}
	public boolean haveServiceURL(){
		return serviceURL!=null;
	}
	public String getLayers() {
		return layers;
	}
	public void setLayers(String layers) {
		this.layers = layers;
	}
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	public Boolean getResolution() {
		return resolution;
	}
	public void setResolution(Boolean resolution) {
		this.resolution = resolution;
	}
	public String getProjection() {
		return projection;
	}
	public void setProjection(String projection) {
		this.projection = projection;
	}
	public Boolean getMaxExtends() {
		return maxExtends;
	}
	public void setMaxExtends(Boolean maxExtends) {
		this.maxExtends = maxExtends;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public Boolean getTransitionEffect() {
		return transitionEffect;
	}
	public void setTransitionEffect(Boolean transitionEffect) {
		this.transitionEffect = transitionEffect;
	}
	public int getNumZoomLevels() {
		return numZoomLevels;
	}
	public void setNumZoomLevels(int numZoomLevels) {
		this.numZoomLevels = numZoomLevels;
	}
	public GoogleV3MapServerType getgMapType() {
		return gMapType;
	}
	public void setgMapType(GoogleV3MapServerType gMapType) {
		this.gMapType = gMapType;
	}
	public Boolean getSphericalMercator() {
		return sphericalMercator;
	}
	public void setSphericalMercator(Boolean sphericalMercator) {
		this.sphericalMercator = sphericalMercator;
	}
	public String getServiceURL() {
		return serviceURL;
	}
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	public MapServiceType getMapServiceType() {
		return mapServiceType;
	}
	
}
