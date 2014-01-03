package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.io.Serializable;
import es.upm.fi.dia.oeg.map4rdf.share.Resource;
/**
 * This is a representative class of Statistic.
 * Contains URI, labels, origin of a statistic.
 */
public class Statistic extends Resource implements Serializable, Comparable<Statistic> {
	private static final long serialVersionUID = -3560700423162724501L;
	private StatisticServer Server;
	private String origin;
	public Statistic(){
		super("");
		this.origin="";
	}
	public Statistic(String URI){
		super(URI);
		this.origin="";
	}
	public Statistic(String URI, String locale, String label){
		super(URI);
		this.addLabel(locale, label);
		this.origin="";
	}
	public Statistic(String URI,String origin){
		super(URI);
		this.origin=origin;
	}
	public Statistic(String URI, String locale, String label,String origin){
		super(URI);
		this.addLabel(locale, label);
		this.origin=origin;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public StatisticServer getServer() {
		return Server;
	}
	public void setServer(StatisticServer server) {
		Server = server;
	}
	@Override
	public int compareTo(Statistic other) {
		String bestLabelThis=LocaleUtil.getBestLabel(this);
		String bestLabelOther=LocaleUtil.getBestLabel(other);
		return bestLabelThis.compareTo(bestLabelOther);
	}
}
