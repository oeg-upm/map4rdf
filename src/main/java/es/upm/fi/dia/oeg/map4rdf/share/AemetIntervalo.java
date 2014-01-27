package es.upm.fi.dia.oeg.map4rdf.share;

import java.io.Serializable;

/**
 * 
 * @author Daniel Garijo
 */
public class AemetIntervalo implements Serializable {
	
	private static final long serialVersionUID = 994118153711293856L;
	
	private int anno;
	private int mes;
	private int dia;
	private int hora;
	private int min;
	//This timeFormatted need to be initialized with dateTimeFormat format.
	private String timeFormatted;
	private static String dateTimeFormat="yyyy-MM-dd HH:mm";
	AemetIntervalo() {
		//For serialization
	}
	public AemetIntervalo(int anno, int mes, int dia, int hora, int minuto) {
		this.anno = anno;
		this.mes = mes;
		this.dia = dia;
		this.hora = hora;
		this.min = minuto;
		StringBuffer time= new StringBuffer(anno+"-");
		//I do this bad practice because GWT dont implement String.Format(String format, Object... args)
		if(mes>=10){
			time.append(mes+"-");
		}else{
			time.append("0"+mes+"-");
		}
		if(dia>=10){
			time.append(dia+" ");
		}else{
			time.append("0"+dia+" ");
		}
		if(hora>=10){
			time.append(hora+":");
		}else{
			time.append("0"+hora+":");
		}
		if(min>=10){
			time.append(min);
		}else{
			time.append("0"+min);
		}
		timeFormatted=time.toString();
	}
	public AemetIntervalo(String timeFormatted){
		String [] annoMesDiaSplit=timeFormatted.split(" ")[0].split("-");
		String [] horaMinSplit=timeFormatted.split(" ")[1].split(":");
		this.anno=Integer.valueOf(annoMesDiaSplit[0]);
		this.mes=Integer.valueOf(annoMesDiaSplit[1]);
		this.dia=Integer.valueOf(annoMesDiaSplit[2]);
		this.hora=Integer.valueOf(horaMinSplit[0]);
		this.min=Integer.valueOf(horaMinSplit[1]);
		this.timeFormatted=timeFormatted;
	}
	public int getAnno() {
		return anno;
	}

	public int getDia() {
		return dia;
	}

	public int getHora() {
		return hora;
	}

	public int getMes() {
		return mes;
	}

	public int getMin() {
		return min;
	}

	public String asXSDDateTime() {
		return anno + "-" + (mes < 10 ? "0" : "") + mes + "-" + (dia < 10 ? "0" : "") + dia + "T"
				+ (hora < 10 ? "0" : "") + hora + ":" + (min < 10 ? "0" : "") + min + ":00Z";
	}
	public String getTimeFormatted(){
		return timeFormatted;
	}
	public static String getDateTimeFormat(){
		return dateTimeFormat;
	}

	@Override
	public String toString() {
		return (hora + ":" + min + " " + dia + "/" + mes + "/" + anno);
	}
}
