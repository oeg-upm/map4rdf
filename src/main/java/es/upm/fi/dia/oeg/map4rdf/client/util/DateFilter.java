package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.util.Date;

public class DateFilter{
	public enum DateFilterType {
		AFTER, BEFORE, EQUAL, AFTER_OR_EQUAL, BEFORE_OR_EQUAL
	}

	private DateFilterType filter;
	private Date date;
	
	public DateFilter(Date date, DateFilterType filter) {
		this.date=date;
		this.filter=filter;
	}
	
	public DateFilterType getFilter() {
		return filter;
	}

	public Date getDate() {
		return date;
	}

	public boolean passFilter(Date date) {
		return compare(date);
	}
	@Override
	public boolean equals(Object object){
		if(!(object instanceof DateFilter)){
			return false;
		}
		DateFilter dateFilter=(DateFilter) object;
		if(date==null || filter==null || dateFilter.date==null || filter==null){
			//All parameters cant be null.
			return false;
		}
		return this.date.equals(dateFilter.date)&&this.filter.equals(dateFilter.filter);
	}
	private boolean compare(Date toCompare) {
		switch (filter) {
		case AFTER:
			return this.date.compareTo(toCompare)<0;
		case BEFORE:
			return this.date.compareTo(toCompare)>0;
		case EQUAL:
			return this.date.compareTo(toCompare)==0;
		case AFTER_OR_EQUAL:
			return this.date.compareTo(toCompare)<=0;
		case BEFORE_OR_EQUAL:
			return this.date.compareTo(toCompare)>=0;
		default:
			break;
		}
		return false;
	}
}
