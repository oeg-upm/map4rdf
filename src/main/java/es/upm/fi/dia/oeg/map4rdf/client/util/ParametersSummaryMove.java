package es.upm.fi.dia.oeg.map4rdf.client.util;

public class ParametersSummaryMove {
	private int firtsTotalTime;
	private int secondTotalTime;
	private int steps;//procure that is divisor of totalTime
	private int diffSteps;
	private int diffSpecialSteps;//procure that the division have a int result.
	private int radiousPX;
	private int intSizeImages;
	private int widgetDistance;
	private int moveType;
	public ParametersSummaryMove(int firtsTotalTime,int secondTotalTime,int steps,int diffSteps, int diffSpecialSteps, int radiousPX,int intSizeImages,int widgetDistance,int moveType){
		this.firtsTotalTime=firtsTotalTime;
		this.secondTotalTime=secondTotalTime;
		this.steps=steps;
		this.diffSteps=diffSteps;
		this.diffSpecialSteps=diffSpecialSteps;
		this.radiousPX=radiousPX;
		this.intSizeImages=intSizeImages;
		this.widgetDistance=widgetDistance;
		this.moveType=moveType;
	}
	public int getIntSizeImages() {
		return intSizeImages;
	}
	public void setIntSizeImages(int intSizeImages) {
		this.intSizeImages = intSizeImages;
	}
	public int getWidgetDistance() {
		return widgetDistance;
	}
	public void setWidgetDistance(int widgetDistance) {
		this.widgetDistance = widgetDistance;
	}
	public int getMoveType() {
		return moveType;
	}
	public void setMoveType(int moveType) {
		this.moveType = moveType;
	}
	public int getFirtsTotalTime() {
		return firtsTotalTime;
	}
	public void setFirtsTotalTime(int firtsTotalTime) {
		this.firtsTotalTime = firtsTotalTime;
	}
	public int getSecondTotalTime() {
		return secondTotalTime;
	}
	public void setSecondTotalTime(int secondTotalTime) {
		this.secondTotalTime = secondTotalTime;
	}
	public int getSteps() {
		return steps;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
	public int getDiffSteps() {
		return diffSteps;
	}
	public void setDiffSteps(int diffSteps) {
		this.diffSteps = diffSteps;
	}
	public int getDiffSpecialSteps() {
		return diffSpecialSteps;
	}
	public void setDiffSpecialSteps(int diffSpecialSteps) {
		this.diffSpecialSteps = diffSpecialSteps;
	}
	public int getRadiousPX() {
		return radiousPX;
	}
	public void setRadiousPX(int radiousPX) {
		this.radiousPX = radiousPX;
	}
}
