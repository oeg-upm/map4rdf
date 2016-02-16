package es.upm.fi.dia.oeg.map4rdf.share;

public enum ConfigurationDrawColoursBy{
	FACET,RESOURCE,LABEL;
	
	public static ConfigurationDrawColoursBy getDefault(){
		return FACET;
	}
	public static boolean isValid(String drawColoursBy){
		boolean goodDrawColours = false;
		if(drawColoursBy!=null){
			for(ConfigurationDrawColoursBy toCompare:ConfigurationDrawColoursBy.values()){
				if(toCompare.toString().equals(drawColoursBy)){
					goodDrawColours = true;
				}
			}
		}
		return goodDrawColours;
	}
}
