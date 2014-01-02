package es.upm.fi.dia.oeg.map4rdf.client.util;

import java.io.Serializable;
import com.google.gwt.core.client.JavaScriptObject;

public class JavaScriptArray<T extends Serializable> {
	JavaScriptObject array;
	public JavaScriptArray(){
		array = createArray();
	}
	public JavaScriptObject getArrayObject(){
		return array;
	}
	public void put(int position,T element){
		pushArray(array, position, element);
	}
	public T get(int position){
		return getArray(array, position);
	}
	private native JavaScriptObject createArray () /*-{
		return new Array();
	}-*/;

	private native void pushArray (JavaScriptObject array,int position, T element) /*-{
		array[position]=element;
	}-*/;
	private native T getArray (JavaScriptObject array,int position)/*-{
		return array[position];
	}-*/;
	
}
