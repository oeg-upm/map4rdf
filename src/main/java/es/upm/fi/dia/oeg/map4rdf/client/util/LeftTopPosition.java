package es.upm.fi.dia.oeg.map4rdf.client.util;

public class LeftTopPosition {
	private int left;
	private int top;
	public LeftTopPosition(){
		this.left=0;
		this.top=0;
	}
	public LeftTopPosition(int left, int top){
		this.left=left;
		this.top=top;
	}
	public int getLeft() {
		return left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
}
