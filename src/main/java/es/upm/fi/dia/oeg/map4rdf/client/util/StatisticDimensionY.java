package es.upm.fi.dia.oeg.map4rdf.client.util;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;

public class StatisticDimensionY extends Resource implements Comparable<StatisticDimensionY>{
	private static final long serialVersionUID = -3098572396843056018L;
	private String dimensionType;
	public StatisticDimensionY(String uri) {
		super(uri);
	}
	public String getDimensionType() {
		return dimensionType;
	}
	public void setDimensionType(String type) {
		this.dimensionType = type;
	}
	@Override
	public int compareTo(StatisticDimensionY other) {
		String bestLabelThis=LocaleUtil.getBestLabel(this);
		String bestLabelOther=LocaleUtil.getBestLabel(other);
		return bestLabelThis.compareTo(bestLabelOther);
	}

}
