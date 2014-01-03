package es.upm.fi.dia.oeg.map4rdf.client.util;

import es.upm.fi.dia.oeg.map4rdf.share.Resource;

public class StatisticDataValue extends Resource implements Comparable<StatisticDataValue>{
	private static final long serialVersionUID = -716131887313397823L;
	private String y;
	public StatisticDataValue(String x,String y){
		super(x);
		this.y=y;
	}
	public String getX() {
		return super.getUri();
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	@Override
	public int compareTo(StatisticDataValue other) {
		
		String bestLabelThis=LocaleUtil.getBestLabel(this);
		String bestLabelOther=LocaleUtil.getBestLabel(other);
		return bestLabelThis.compareTo(bestLabelOther);
	}
	@Override
	public String toString(){
		return "(X="+super.getUri()+" || Y="+y+")";
	}
}
