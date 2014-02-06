package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetIntervalo;

public class DateUtil {
	public static Date asDate(AemetIntervalo intervalo) {
		DateTimeFormat dtf = DateTimeFormat.getFormat(AemetIntervalo.getDateTimeFormat());
		Date date= new Date();
		dtf.parse(intervalo.getTimeFormatted(),0, date);
		//date is modified by dtf
	    return date;
	}
	public static AemetIntervalo asAemetIntervalo(Date date){
		String timeFormat=AemetIntervalo.getDateTimeFormat();
		String toConvert=DateTimeFormat.getFormat(timeFormat).format(date);
		AemetIntervalo toReturn = new AemetIntervalo(toConvert);
		return toReturn;
	}
}
