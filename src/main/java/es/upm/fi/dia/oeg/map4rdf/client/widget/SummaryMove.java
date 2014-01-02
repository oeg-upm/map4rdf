package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.util.LeftTopPosition;
import es.upm.fi.dia.oeg.map4rdf.client.util.ParametersSummaryMove;
import es.upm.fi.dia.oeg.map4rdf.client.util.WidgetLineMove;

/**
 * @author Francisco Siles
 */
public class SummaryMove {
	private Timer firtsTimer;
	//private Timer secondTimer;
	private int currentStep;
	private ArrayList<Integer> widgetSteps;
	private ParametersSummaryMove parametersSummary;
	private ArrayList<Widget> allWidgetInOrder;
	private GeoResourceSummary geoResourceSummary;
	private ArrayList<WidgetLineMove> widgetsLineMove;
	public SummaryMove(ArrayList<Widget> allWidgetInOrder, ParametersSummaryMove parametersSummary, GeoResourceSummary geoResourceSummary){
		this.allWidgetInOrder = allWidgetInOrder;
		this.parametersSummary = parametersSummary;
		this.geoResourceSummary = geoResourceSummary;
		firtsTimer=getFirtsTimer();
		//secondTimer=getSecondTimer();
		currentStep=0;
		widgetSteps=new ArrayList<Integer>(allWidgetInOrder.size());
		for(int i=0;i<allWidgetInOrder.size();i++){
			widgetSteps.add(0);
		}
		initializeWidgetsLineMove();
	}
	public void moveToInitialPosition(){
		this.currentStep=0;	
		switch (parametersSummary.getMoveType()) {
		case 1:
			for(int i=allWidgetInOrder.size()-1;i>=0;i--){
				geoResourceSummary.moveLeftTopOfCenter(allWidgetInOrder.get(i), calculateLeftMove(0), calculateTopMove(0));
			}
			break;
		case 2:
			for(int i=allWidgetInOrder.size()-1;i>=0;i--){
				geoResourceSummary.moveLeftTopOfCenter(allWidgetInOrder.get(i), 0, 0);
			}
			break;
		default:
			for(int i=0;i<widgetSteps.size();i++){
				widgetSteps.set(i, i*parametersSummary.getDiffSpecialSteps());
			}
			for(int i=allWidgetInOrder.size()-1;i>=0;i--){
				geoResourceSummary.moveLeftTopOfCenter(allWidgetInOrder.get(i), calculateLeftMove(i), calculateTopMove(i));
			}
			break;
		}
			/*DOM.setStyleAttribute(allWidgetInOrder.get(i).getElement(), "left", sizeImages);
			DOM.setStyleAttribute(allWidgetInOrder.get(i).getElement(), "top", top+"px");*/
	}
	public void startMoveWidgets(){
		widgetSteps.clear();
		for(int i=0;i<allWidgetInOrder.size();i++){
			widgetSteps.add(0);
		}
		currentStep=0;
		firtsTimer.cancel();
		//secondTimer.cancel();
		switch (parametersSummary.getMoveType()) {
		case 1:
			firtsTimer.scheduleRepeating(parametersSummary.getFirtsTotalTime()/parametersSummary.getSteps());
			break;
		case 2:
			for(WidgetLineMove i:widgetsLineMove){
				i.initWidgetLineMove(parametersSummary.getSteps());
			}
			firtsTimer.scheduleRepeating(parametersSummary.getSecondTotalTime()/parametersSummary.getSteps());
			break;
		default:
			break;
		}
		
	}
	public void cancelMove(){
		firtsTimer.cancel();
		//secondTimer.cancel();
	}
	private void initializeWidgetsLineMove() {
		widgetsLineMove= new ArrayList<WidgetLineMove>();
		for(int i=0;i<widgetSteps.size();i++){
			widgetSteps.set(i, i*parametersSummary.getDiffSpecialSteps());
		}
		for(int i=0;i<allWidgetInOrder.size();i++){
			int left=parametersSummary.getIntSizeImages()+calculateLeftMove(i);
			int top=parametersSummary.getIntSizeImages()-calculateTopMove(i);
			widgetsLineMove.add(new WidgetLineMove(allWidgetInOrder.get(i), new LeftTopPosition(left, top), "px"));
		}
	}
	private Timer getFirtsTimer(){
		Timer toReturn=new Timer() {
			@Override
			public void run() {
				switch (parametersSummary.getMoveType()) {
				case 1:
					doCircleMoveOfWidgets();
					break;
				case 2:
					doLineMoveOfWidgets();
					break;
				default:
					break;
				}
			}
		};
		return toReturn;
	}
	/*private Timer getSecondTimer(){
		Timer toReturn=new Timer() {
			@Override
			public void run() {
				try {
					doLastMoves();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		return toReturn;
	}*/
	private int calculateTopMove(int widgetIndex){
		double angleDegrees=0.0;
		angleDegrees=((double)(360.0/parametersSummary.getSteps()))*widgetSteps.get(widgetIndex);
		int top=(int)(Math.cos(Math.toRadians(angleDegrees))*parametersSummary.getRadiousPX());
		return top;
	}
	private int calculateLeftMove(int widgetIndex){
		double angleDegrees=0.0;
		angleDegrees=((double)(360.0/parametersSummary.getSteps()))*widgetSteps.get(widgetIndex);
		int left=(int)(Math.sin(Math.toRadians(angleDegrees))*parametersSummary.getRadiousPX());
		return left;
	}
	private void doCircleMoveOfWidgets(){
		int init=((++currentStep)/parametersSummary.getDiffSteps());
		if(init>allWidgetInOrder.size()-1){
			init=allWidgetInOrder.size()-1;
		}
		for(int i=allWidgetInOrder.size()-1;i>=allWidgetInOrder.size()-1-init;i--){
			if(widgetSteps.get(i)<=(i*parametersSummary.getDiffSpecialSteps())){
				geoResourceSummary.moveLeftTopOfCenter(allWidgetInOrder.get(i), calculateLeftMove(i), calculateTopMove(i));
				widgetSteps.set(i, widgetSteps.get(i)+1);
			}
		}
	
		if(currentStep-1>=parametersSummary.getSteps()){
			currentStep=0;
			/*for(WidgetLineMove i:widgetsLineMove){
				i.initWidgetLineMove(parametersSummary.getSteps()/4);
			}
			if(!widgetsLineMove.isEmpty()){
				secondTimer.scheduleRepeating(parametersSummary.getSecondTotalTime()/parametersSummary.getSteps());
			}*/
			firtsTimer.cancel();
		}
	}
	private void doLineMoveOfWidgets(){
		int init=++currentStep;
		for(WidgetLineMove i:widgetsLineMove){
			try {
				i.doStep(init);
			} catch (Exception e) {
			}
		}
		if(currentStep>parametersSummary.getSteps()){
			currentStep=0;
			firtsTimer.cancel();
		}
	}
	/*private void doLastMoves() throws Exception{
		if(currentStep<=parametersSummary.getSteps()/4){
			for(WidgetLineMove i:widgetsLineMove){
				i.doStep(currentStep);
			}
			currentStep++;
		}else{
			secondTimer.cancel();
		}
	}*/
}
