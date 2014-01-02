package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
/**
 * This is a representative class of a dimension.
 */
public class StatisticDimension  extends Resource implements Serializable,Comparable<StatisticDimension>{
	private static final long serialVersionUID = 5329788905948133126L;
	private String xType;
	private String aggr;
	private ArrayList<StatisticDimensionY> dimensionsY;
	
	public StatisticDimension(String uri){
		super(uri);
		this.xType="";
		dimensionsY = new ArrayList<StatisticDimensionY>();
	}
	
	/*Auxiliar methods*/
	public void addDimensionY(StatisticDimensionY dimY){
		dimensionsY.add(dimY);
	}

	
	/*Getters and setter*/
	public String getxType() {
		return xType;
	}

	public void setxType(String xType) {
		this.xType = xType;
	}

	public ArrayList<StatisticDimensionY> getDimensionsY() {
		return dimensionsY;
	}

	public void setDimensionsY(ArrayList<StatisticDimensionY> dimensionsY) {
		this.dimensionsY = dimensionsY;
	}

	public String getAggr() {
		return aggr;
	}

	public void setAggr(String aggr) {
		this.aggr = aggr;
	}

	@Override
	public int compareTo(StatisticDimension other) {
		String bestLabelThis=LocaleUtil.getBestLabel(this);
		String bestLabelOther=LocaleUtil.getBestLabel(other);
		return bestLabelThis.compareTo(bestLabelOther);
	}
}