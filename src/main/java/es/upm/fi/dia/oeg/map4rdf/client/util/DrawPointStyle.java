package es.upm.fi.dia.oeg.map4rdf.client.util;

public class DrawPointStyle {
	public enum Style {FACET, NEXT_POINTS, ROUTES,POLYLINE_ROUTE, CENTER_NEXT_POINTS, SELECTED_RESOURCE}
	//POLYLINE_ROUTE is a default style but is for separate route points of polyline(route).
	private Style style;
	private char leter;
	private String facetHexColour="#0000FF";
	private static final String MARKER_RED_ICON = "marker_red.png";
	//private static final String MARKER_YELLOW_ICON = "marker_yellow_facet.png";
	private static final String MARKER_TURQUEISE_ICON = "marker_turqueise_facet.png";
	private static final String MARKER_LIGHTPURPLE_ICON = "marker_light_purple_facet.png";
	
	private static final String MARKER_BLUE_ICON = "marker_blue.png";
	private static final String MARKER_GREEN_ICON = "marker_green<leter>.png";
	private static final String MARKER_ORANGE_ICON = "marker_orange.png";
	private static final String MARKER_PURPLE_ICON = "marker_purple.png";
	private static final String MARKER_cc99ff_ICON = "marker_cc99ff_facet.png";
	private static final String MARKER_8f49d1_ICON = "marker_8f49d1_facet.png";
	private static final String MARKER_4979d1_ICON = "marker_4979d1_facet.png";
	private static final String MARKER_49d1c4_ICON = "marker_49d1c4_facet.png";
	private static final String MARKER_8bd149_ICON = "marker_8bd149_facet.png";
	private static final String MARKER_d149b8_ICON = "marker_d149b8_facet.png";
	private static final String MARKER_d7e354_ICON = "marker_d7e354_facet.png";
	private static final String MARKER_d14972_ICON = "marker_d14972_facet.png";
	//Min length hexColours=0;
	//with Yellow:
	//private static final String[] hexColours={"#f88296","#b0a420","#e6a9dd","#9cf3dc"};
	//private static final String[] hexColours={"#f88296","#e6a9dd","#9cf3dc",};
	private static final String[] hexColours={"#f88296","#e6a9dd","#9cf3dc","#cc99ff","#8f49d1","#4979d1","#49d1c4","#8bd149","#d149b8","#d7e354","#d14972"};
	//hexColours.length == relationHexColours.length
	//with Yellow
	//private static final String[] relationHexColours={MARKER_RED_ICON,MARKER_YELLOW_ICON,MARKER_LIGHTPURPLE_ICON,MARKER_TURQUEISE_ICON};
	private static final String[] relationHexColours={MARKER_RED_ICON,MARKER_LIGHTPURPLE_ICON,MARKER_TURQUEISE_ICON,MARKER_cc99ff_ICON,MARKER_8f49d1_ICON
													,MARKER_4979d1_ICON,MARKER_49d1c4_ICON,MARKER_8bd149_ICON,MARKER_d149b8_ICON,MARKER_d7e354_ICON,MARKER_d14972_ICON};
	public DrawPointStyle(){
		this.style=getDefaultStyle();
		leter=getMinLeter();
	}
	public DrawPointStyle(String hexColour){
		this.style=getDefaultStyle();
		this.facetHexColour=hexColour;
		leter=getMinLeter();
	}
	public DrawPointStyle(Style style){
		this.style=style;
		leter=getMinLeter();
	}
	public DrawPointStyle(Style style,char leter){
		this.style=style;
		if(leter>=getMinLeter() && leter <= getMaxLeter()){
			this.leter=leter;
		}else{
			this.leter=getMinLeter();
		}
		
	}
	public DrawPointStyle(char leter){
		this.style=Style.ROUTES;
		if(leter>=getMinLeter() && leter <= getMaxLeter()){
			this.leter=leter;
		}else{
			this.leter=getMinLeter();
		}
	}
	public Style getStyle(){
		return style;
	}
	public String getImageURL(){
		switch (style) {
		case FACET:
			int position=0;
			for(int i=0;i<hexColours.length;i++){
				if(hexColours[i].equals(facetHexColour)){
					position=i;
					break;
				}
			}
			return relationHexColours[position];
		case NEXT_POINTS:
			return MARKER_BLUE_ICON;
		case ROUTES:
			String toReturn=new String(MARKER_GREEN_ICON);
			if(leter>getMaxLeter()){
				leter=getMaxLeter();
			}
			toReturn=toReturn.replace("<leter>", String.valueOf(leter));
			return toReturn;
		case CENTER_NEXT_POINTS:
			return MARKER_ORANGE_ICON;
		case SELECTED_RESOURCE:
			return MARKER_PURPLE_ICON;
		default:
			return MARKER_RED_ICON;
		}
	}
	public String getFacetHexColour() {
		switch (style) {
		case CENTER_NEXT_POINTS:
			return "#ffb225";
		case ROUTES:
			return "#00d600";
		default:
			return facetHexColour;
		}
	}
	public void setFacetHexColour(String facetHexColour) {
		this.facetHexColour = facetHexColour;
	}
	public int getWidth(){
		if(style==Style.ROUTES){
			return 21;
		}else{
			return 24;
		}
	}
	public int getHeight(){
		if(style==Style.ROUTES){
			return 34;
		}else{
			return 21;
		}
	}
	public int getDesplaceOffsetY(){
		//Depend of image. Desplacement of left-up corner that where image mark.
		if(style==Style.ROUTES){
			return -17;
		}else{
			return -10;
		}
	}
	public int getDesplaceOffsetX(){
		//Depend of image. Desplacement of left-up corner that where image mark.
		if(style==Style.ROUTES){
			return -10;
		}else{
			return -6;
		}
	}
	public static char getMaxLeter(){
		return 'Z';
	}
	public static char getMinLeter(){
		return 'A';
	}
	public static int getLeterSize(){
		return getMaxLeter()-getMinLeter()+1;
	}
	public static Style getDefaultStyle(){
		return Style.FACET;
	}
	public static String[] getHexColours(){
		return hexColours;
	}
	
}
