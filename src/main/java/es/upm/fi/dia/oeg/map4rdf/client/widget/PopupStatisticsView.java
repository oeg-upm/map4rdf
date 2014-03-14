package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.BarChart;
import com.googlecode.gwt.charts.client.corechart.BarChartOptions;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.options.CurveType;

import es.upm.fi.dia.oeg.map4rdf.client.event.StatisticsSummaryEvent;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.StatisticDataValue;
import es.upm.fi.dia.oeg.map4rdf.client.util.StatisticDimension;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.client.util.Statistic;
import es.upm.fi.dia.oeg.map4rdf.client.util.StatisticDimensionY;
import es.upm.fi.dia.oeg.map4rdf.client.util.StatisticServer;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

public class PopupStatisticsView extends Composite{
	private GeoResource resource;
	private String statisticsServiceURL;
	private BrowserMessages browserMessages;
	private BrowserResources browserResources;
	private EventBus eventBus;
	private WidgetFactory widgetFactory;
	private Grid statisticsGrid;
	private ListBox statisticsBox;
	private ListBox dimensionsXBox;
	private ListBox dimensionsYBox;
	private ListBox aggrBox;
	private Label aggrLabel;
	private Label errorLabel;
	private ArrayList<Statistic> statistics;
	private ArrayList<StatisticDimension> dimensions;
	private Statistic selectedStatistic;
	private StatisticDimension selectedDimensionX;
	private StatisticDimensionY selectedDimensionY;
	private int width;
	private int height;
	private Panel mainPanel;
	private Grid mainGrid;
	private Grid chartGrid;
	public enum ChartType {
		PIE,LINE,BAR
	}
	private ChartType [] charts={ChartType.PIE,ChartType.LINE,ChartType.BAR};
	private int selectedChart=0;
	private List<StatisticDataValue> lastStatisticsValues;
	//TODO add aemet LoadingBox for statistics here and replace the old loadingWidget.
	private LoadingWidget loadingWidget;
		
	public PopupStatisticsView(GeoResource resource,String statisticsServiceURL,int width,int height,EventBus eventBus,BrowserMessages browserMessages,BrowserResources browserResources,WidgetFactory widgetFactory){
		this.resource=resource;
		this.statisticsServiceURL=statisticsServiceURL;
		this.eventBus= eventBus;
		this.browserMessages = browserMessages;
		this.browserResources = browserResources;
		this.widgetFactory = widgetFactory;
		this.width=width;
		this.height=height;
		initWidget(createUi());
		DOM.setStyleAttribute(loadingWidget.getElement(), "display", "");
		Timer timer=new Timer(){

			@Override
			public void run() {
				initAsync();
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
			}
			
		};
		timer.schedule(50);
	}

	private Widget createUi() {
		
		FlowPanel panel= new FlowPanel();
		this.mainPanel=panel;
		Button closeButton = new Button();
		closeButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				eventBus.fireEvent(new StatisticsSummaryEvent(false,null));
		}});
		closeButton.setSize("32px","28px");
		closeButton.getElement().appendChild(new Image(browserResources.closeButton()).getElement());
		closeButton.setTitle(browserMessages.close());
		panel.add(closeButton);
		DOM.setStyleAttribute(closeButton.getElement(), "position", "absolute");
		DOM.setStyleAttribute(closeButton.getElement(), "top", "1px");
		DOM.setStyleAttribute(closeButton.getElement(), "left", "");
		DOM.removeElementAttribute(closeButton.getElement(),"left");
		DOM.setStyleAttribute(closeButton.getElement(), "right", "1px");
		DOM.setStyleAttribute(closeButton.getElement(), "zIndex", "2080");
		mainGrid = new Grid(4,1);
		Grid chooseGrid = new Grid(2,4);
		statisticsGrid = new Grid(1,1);
		statisticsBox= new ListBox(false);
		dimensionsXBox = new ListBox(false);
		dimensionsYBox = new ListBox(false);
		aggrBox = new ListBox(false);
		aggrBox.setVisible(false);
		aggrBox.addItem("SUM");
		aggrBox.addItem("COUNT");
		aggrBox.addItem("MIN");
		aggrBox.addItem("MAX");
		aggrBox.addItem("AVG");
		aggrBox.addItem("GROUP_CONCAT");
		aggrBox.addItem("SAMPLE");
		aggrLabel= new Label(browserMessages.statisticsAggrChoose());
		aggrLabel.setVisible(false);
		errorLabel=new Label();
		chooseGrid.setWidget(0, 0, new Label(browserMessages.statisticsChoose()));
		chooseGrid.setWidget(0, 1, new Label(browserMessages.statisticsDimensionXChoose()));
		chooseGrid.setWidget(0, 2, new Label(browserMessages.statisticsDimensionYChoose()));
		chooseGrid.setWidget(0, 3, aggrLabel);
		chooseGrid.setWidget(1, 0, statisticsBox);
		chooseGrid.setWidget(1, 1, dimensionsXBox);
		chooseGrid.setWidget(1, 2, dimensionsYBox);
		chooseGrid.setWidget(1, 3, aggrBox);
		mainGrid.setWidget(0, 0, chooseGrid);
		mainGrid.setWidget(1, 0, errorLabel);
		mainGrid.setWidget(3, 0, statisticsGrid);
		statisticsBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(final ChangeEvent event) {
				statisticsGrid.clear();
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "");
				Timer timer=new Timer(){
					@Override
					public void run() {
						handlerStatisticChange(event);
						DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
					}
				};
				timer.schedule(50);
				
			}
		});
		dimensionsXBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				statisticsGrid.clear();
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "");
				Timer timer=new Timer(){

					@Override
					public void run() {
						handlerDimensionXChange(event);
						DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
					}
					
				};
				timer.schedule(50);
			}
		});
		dimensionsYBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(final ChangeEvent event) {
				statisticsGrid.clear();
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "");
				Timer timer=new Timer(){

					@Override
					public void run() {
						handlerDimensionYChange(event);
						DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
					}
					
				};
				timer.schedule(50);
			}
		});
		aggrBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(final ChangeEvent event) {
				statisticsGrid.clear();
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "");
				Timer timer=new Timer(){

					@Override
					public void run() {
						handlerAggrChange(event);
						DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
					}
					
				};
				timer.schedule(50);
			}
		});
		loadingWidget = new LoadingWidget(browserResources.loadingIcon(),browserMessages.loading(), browserResources.css());
		chartGrid=new Grid(1,3);
		initCharts();
		mainGrid.setWidget(2, 0, chartGrid);
		return panel;
	}
	private void initCharts(){
		DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "#A4A4A4");
		Image image=new Image(browserResources.chartPieIcon());
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "");
				selectedChart=0;
				DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "#A4A4A4");
				if(selectedStatistic!=null && selectedDimensionX!=null && selectedDimensionY!=null){
					handlerChartChange();
				}
			}
		});
		chartGrid.setWidget(0, 0, image);
		image=new Image(browserResources.chartLineIcon());
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "");
				selectedChart=1;
				DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "#A4A4A4");
				if(selectedStatistic!=null && selectedDimensionX!=null && selectedDimensionY!=null){
					handlerChartChange();
				}
			}
		});
		chartGrid.setWidget(0, 1, image);
		image=new Image(browserResources.chartBarIcon());
		image.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "");
				selectedChart=2;
				DOM.setStyleAttribute(chartGrid.getCellFormatter().getElement(0, selectedChart),"background", "#A4A4A4");
				if(selectedStatistic!=null && selectedDimensionX!=null && selectedDimensionY!=null){
					handlerChartChange();
				}
			}
		});
		chartGrid.setWidget(0, 2, image);
	}
	private void initAsync(){
		statistics=getStatistics(statisticsServiceURL, resource.getUri());
		statisticsBox.addItem("");
		if(statistics!=null){
			for(Statistic i:statistics){
				statisticsBox.addItem(LocaleUtil.getBestLabel(i).replace("\"", "")+" ("+LocaleUtil.getBestLabel(i.getServer()).replace("\"", "")+")");
			}
			if(statistics.isEmpty()){
				mainPanel.add(new Label(browserMessages.statisticsEmpty()));
			}else{
				mainPanel.add(mainGrid);
				mainPanel.add(loadingWidget);
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
				DOM.setStyleAttribute(loadingWidget.getElement(), "position", "absolute");
				DOM.setStyleAttribute(loadingWidget.getElement(), "top", "50%");
				DOM.setStyleAttribute(loadingWidget.getElement(), "left", "50%");
				DOM.setStyleAttribute(loadingWidget.getElement(), "margin", "-16px 0 0 -80px");
				DOM.setStyleAttribute(loadingWidget.getElement(), "zIndex", "2080");
			}
		} else {
			mainPanel.add(new Label(browserMessages.errorCommunication()));
		}
	}
	private void hideWidgets(){
		errorLabel.setVisible(false);
		aggrBox.setVisible(false);
		aggrLabel.setVisible(false);
	}
	private void doVisibleWidgets(){
		errorLabel.setVisible(true);
		aggrBox.setVisible(true);
		aggrLabel.setVisible(true);
	}
	private void handlerStatisticChange(ChangeEvent event){
		hideWidgets();
		lastStatisticsValues=null;
		if(statisticsBox.getItemCount()>statistics.size()){
			statisticsBox.removeItem(0);
		}
		selectedStatistic=statistics.get(statisticsBox.getSelectedIndex());
		selectedDimensionX=null;
		selectedDimensionY=null;
		changeDimensionXBox();
	}

	private void handlerDimensionXChange(ChangeEvent event){
		hideWidgets();
		lastStatisticsValues=null;
		if(dimensionsXBox.getItemCount()>dimensions.size()){
			dimensionsXBox.removeItem(0);
		}
		selectedDimensionY=null;
		selectedDimensionX=dimensions.get(dimensionsXBox.getSelectedIndex());
		changeDimensionYBox();
	}
	private void handlerDimensionYChange(ChangeEvent event){
		hideWidgets();
		lastStatisticsValues=null;
		if(dimensionsYBox.getItemCount()>selectedDimensionX.getDimensionsY().size()){
			dimensionsYBox.removeItem(0);
		}
		selectedDimensionY=selectedDimensionX.getDimensionsY().get(dimensionsYBox.getSelectedIndex());
		if(selectedDimensionX.getAggr()!=null && !selectedDimensionX.getAggr().isEmpty()){
			drawStatistic();
		} else {
			errorLabel.setText(browserMessages.statisticsErrorNotAggr());
			doVisibleWidgets();
			drawStatistic();
		}
	}
	private void handlerAggrChange(ChangeEvent event){
		lastStatisticsValues=null;
		errorLabel.setVisible(false);
		drawStatistic();
	}
	private void handlerChartChange(){
		statisticsGrid.clear();
		DOM.setStyleAttribute(loadingWidget.getElement(), "display", "");
		Timer timer=new Timer(){

			@Override
			public void run() {
				drawStatistic();
				DOM.setStyleAttribute(loadingWidget.getElement(), "display", "none");
			}
			
		};
		timer.schedule(50);
	}
	private void changeDimensionXBox(){
		dimensionsXBox.clear();
		dimensionsYBox.clear();
		dimensionsXBox.addItem("");
		dimensions = getDimensions(statisticsServiceURL,selectedStatistic.getServer().getUri(),selectedStatistic.getUri());
		if(dimensions!=null){
			if(!dimensions.isEmpty()){
				for(int i=0;i<dimensions.size();i++){
					dimensionsXBox.addItem(LocaleUtil.getBestLabel(dimensions.get(i)).replace("\"", ""));
				}
			} else {
				widgetFactory.getDialogBox().showError(browserMessages.errorNotDimensions());
			}
		}else{
			widgetFactory.getDialogBox().showError(browserMessages.errorCommunication());
		}
	}
	
	private void changeDimensionYBox() {
		dimensionsYBox.clear();
		ArrayList<StatisticDimensionY> dimensionsY = selectedDimensionX.getDimensionsY();
		dimensionsYBox.addItem("");
		for(int i=0;i<dimensionsY.size();i++){
			dimensionsYBox.addItem(LocaleUtil.getBestLabel(dimensionsY.get(i)).replace("\"", ""));
		}
	}
	private void drawStatistic(){
		if(lastStatisticsValues==null){
			lastStatisticsValues  = getValues(resource.getUri(), selectedStatistic.getServer().getUri(), selectedStatistic.getUri(), selectedDimensionX.getUri(), selectedDimensionY.getUri());
		}
		
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel panel = new FlowPanel();
		scroll.add(panel);
		scroll.setSize(((int)(0.7*width))+"px", ((int)(0.7*height))+"px");
		String tableTitle=LocaleUtil.getBestLabel(selectedStatistic);
		int chartWidth=(int)(0.68*width);
		int chartHeight=(int)(0.68*height);
		String xTitle=LocaleUtil.getBestLabel(selectedDimensionX);
		String yTitle=LocaleUtil.getBestLabel(selectedDimensionY);
		statisticsGrid.setWidget(0, 0, scroll);
		drawChart(charts[selectedChart],tableTitle, panel, chartWidth, chartHeight, xTitle, yTitle, lastStatisticsValues);
	}
		
	private Set<String> getServers(String url){
		String servers= getQueryJSON(url,"getServers","");
		Set<String> set = new HashSet<String>();
		if(servers!=null){
			JSONValue value= JSONParser.parseStrict(servers);
			if(value.isArray()!=null){
				for(int i=0;i<value.isArray().size();i++){
					set.add(value.isArray().get(i).toString().replace("\"", ""));
				}
			}
		}
		return set;
	}
	private ArrayList<Statistic> getStatistics(String url,String geoResourceUri){
		String statistics= getQueryJSON(url,"getStatistics","Server=all&URI="+encodeParameter(geoResourceUri));
		ArrayList<Statistic> stats= new ArrayList<Statistic>();
		if(statistics!=null){
			JSONValue value= JSONParser.parseStrict(statistics);
			if(value.isObject()!=null){
				JSONObject dataServer = value.isObject();
				for(String serverURI: dataServer.keySet()){
					JSONObject objectResponseServer=dataServer.get(serverURI).isObject();
					StatisticServer statisticServer= new StatisticServer(serverURI);
					if(objectResponseServer!=null){
						if(objectResponseServer.get("labels")!=null && objectResponseServer.get("labels").isObject()!=null){
							for(String key: objectResponseServer.get("labels").isObject().keySet()){
								if(objectResponseServer.get("labels").isObject().get(key)!=null){
									statisticServer.addLabel(key.replace("\"", ""), objectResponseServer.get("labels").isObject().get(key).toString().replace("\"", ""));
								}
							}
						}
						if(objectResponseServer.get("values")!=null && objectResponseServer.get("values").isArray()!=null){
							JSONArray dataAllStatistics = objectResponseServer.get("values").isArray();
							for(int i=0;i<dataAllStatistics.size();i++){
								JSONObject dataStatistic = dataAllStatistics.get(i).isObject();
								if(dataStatistic !=null && dataStatistic.get("uri")!=null){
									Statistic statistic = new Statistic(dataStatistic.get("uri").toString().replace("\"", ""));
									statistic.setServer(statisticServer);
									if(dataStatistic.get("origin")!=null){
										statistic.setOrigin(dataStatistic.get("origin").toString().replace("\"", ""));
									}
									JSONObject labels;
									if(dataStatistic.get("labels")!=null && dataStatistic.get("labels").isObject()!=null){
										labels = dataStatistic.get("labels").isObject();
										for(String key: labels.keySet()){
											if(labels.get(key)!=null){
												statistic.addLabel(key.replace("\"", ""), labels.get(key).toString().replace("\"", ""));
											}
										}
									}
									stats.add(statistic);
								}
							}
						}
					}
				}
			}
			Collections.sort(stats);
		}else{
			stats=null;
		}
		return stats;
	}
	private ArrayList<StatisticDimension> getDimensions(String url, String server, String statistic) {
		String data = "Server=" +encodeParameter(server) +"&";
		data = data + "Statistic=" + encodeParameter(statistic);
		Map<String,StatisticDimension> dimMap = new HashMap<String, StatisticDimension>();
		String dimensions= getQueryJSON(url,"getDimensions",data);
		ArrayList<StatisticDimension> dims=new ArrayList<StatisticDimension>();
		if(dimensions!=null){
			JSONValue value= JSONParser.parseStrict(dimensions);
			if(value.isArray()!=null){
				for(int i=0;i<value.isArray().size();i++){
					JSONValue valueDim=value.isArray().get(i);
					if(valueDim.isObject()!=null){
						JSONObject dim= valueDim.isObject();
						if(dim.get("xURI")!=null && dim.get("xURI").isString()!=null){
							String uri= dim.get("xURI").isString().toString().replace("\"", "");
							StatisticDimension dimension= new StatisticDimension(uri);
							if(dim.get("xType")!=null && dim.get("xType").isString()!=null){
								dimension.setxType(dim.get("xType").isString().toString().replace("\"", ""));
							}
							if(dim.get("xLabels")!=null && dim.get("xLabels").isObject()!=null){	
								JSONObject labels = dim.get("xLabels").isObject();
								for(String key: labels.keySet()){
									if(labels.get(key)!=null){
										dimension.addLabel(key.replace("\"", ""), labels.get(key).toString().replace("\"", ""));
									}
								}
							}
							if(dim.get("aggr")!=null && dim.get("aggr").isString()!=null){
								dimension.setAggr(dim.get("aggr").isString().toString().replace("\"", ""));
							}
							if(dim.get("yURI")!=null && dim.get("yURI").isString()!=null){
								String yURI= dim.get("yURI").isString().toString().replace("\"", "");
								StatisticDimensionY dimensionY= new StatisticDimensionY(yURI);
								if(dim.get("yType")!=null && dim.get("yType").isString()!=null){
									dimensionY.setDimensionType(dim.get("yType").isString().toString().replace("\"", ""));
								}
								if(dim.get("yLabels")!=null && dim.get("yLabels").isObject()!=null){	
									JSONObject labels = dim.get("yLabels").isObject();
									for(String key: labels.keySet()){
										if(labels.get(key)!=null){
											dimensionY.addLabel(key.replace("\"", ""), labels.get(key).toString().replace("\"", ""));
										}
									}
								}
								if(dimMap.containsKey(dimension.getUri())){
									dimMap.get(dimension.getUri()).addDimensionY(dimensionY);
								}else{
									dimension.addDimensionY(dimensionY);
									dimMap.put(dimension.getUri(), dimension);
								}
							}
						}
					}
				}
			}
		}else{
			return null;
		}
		dims.addAll(dimMap.values());
		for(StatisticDimension dimension:dims){
			Collections.sort(dimension.getDimensionsY());
		}
		Collections.sort(dims);
		return dims;
	}
	private List<StatisticDataValue> getValues(String url, String server, String statistic,String dimensionX,String dimensionY){
		List<StatisticDataValue> toReturn=new ArrayList<StatisticDataValue>();
		HashMap<String,StatisticDataValue> mapa= new HashMap<String, StatisticDataValue>();
		String data="";
		data += "Server=" + encodeParameter(selectedStatistic.getServer().getUri()) +"&";
		data += "URI=" + encodeParameter(resource.getUri())+"&";
		data += "Statistic=" + encodeParameter(selectedStatistic.getUri())+"&";
		data += "Dimension=" + encodeParameter(selectedDimensionX.getUri())+"&";
		data += "DimensionY=" + encodeParameter(selectedDimensionY.getUri())+"&";
		if(selectedDimensionX.getAggr()!=null && !selectedDimensionX.getAggr().isEmpty()){
			data += "aggr=" + encodeParameter(selectedDimensionX.getAggr());
		}else{
			data += "aggr=" + aggrBox.getItemText(aggrBox.getSelectedIndex());
		}
		String datasValues= getQueryJSON(statisticsServiceURL, "getStatisticsValues", data);
		if(datasValues!=null && !datasValues.isEmpty()){
			JSONValue value= JSONParser.parseStrict(datasValues);
			if( value!=null && value.isObject()!=null 
					&& value.isObject().get("results")!=null 
					&& value.isObject().get("results").isObject()!=null 
					&& value.isObject().get("results").isObject().get("bindings")!=null
					&& value.isObject().get("results").isObject().get("bindings").isArray()!=null){
				JSONArray array= value.isObject().get("results").isObject().get("bindings").isArray();
				for(int i=0;i<array.size();i++){
					if(array.get(i)!=null && array.get(i).isObject()!=null){
						JSONObject punto=array.get(i).isObject();
						String x="";
						String y="";
						String localeLabelX="";
						String labelLabelX="";
						if(punto.get("valueX")!=null && punto.get("valueX").isObject()!=null
								&& punto.get("valueX").isObject().get("value")!=null
								&& punto.get("valueX").isObject().get("value").isString()!=null){
								x= punto.get("valueX").isObject().get("value").isString().toString().replace("\"", "");								
						}
						if(punto.get("aggValueY")!=null && punto.get("aggValueY").isObject()!=null
								&& punto.get("aggValueY").isObject().get("value")!=null
								&& punto.get("aggValueY").isObject().get("value").isString()!=null){
								y= punto.get("aggValueY").isObject().get("value").isString().toString().replace("\"", "");								
						}
						if(punto.get("labelX")!=null && punto.get("labelX").isObject()!=null
								&& punto.get("labelX").isObject().get("xml:lang")!=null
								&& punto.get("labelX").isObject().get("xml:lang").isString()!=null){
								localeLabelX= punto.get("labelX").isObject().get("xml:lang").isString().toString().replace("\"", "");								
						}
						if(punto.get("labelX")!=null && punto.get("labelX").isObject()!=null
								&& punto.get("labelX").isObject().get("value")!=null
								&& punto.get("labelX").isObject().get("value").isString()!=null){
								labelLabelX= punto.get("labelX").isObject().get("value").isString().toString().replace("\"", "");								
						}
						if(!x.equals("") && !y.equals("")){
							StatisticDataValue statisticDataValue;
							if(mapa.containsKey(x)){
								statisticDataValue=mapa.get(x);
							}else{
								statisticDataValue=new StatisticDataValue(x, y);
								mapa.put(x, statisticDataValue);
							}
							if(!labelLabelX.isEmpty()){
								statisticDataValue.addLabel(localeLabelX, labelLabelX);
							}
						}
					}
				}
			}
		}
		toReturn.addAll(mapa.values());
		Collections.sort(toReturn);
		return toReturn;
	}
	private String encodeParameter(String decodedParameter){
		String encoded=URL.encode(decodedParameter);
		String splitParameter[]= encoded.split("#");
		encoded="";
		for(int j=0;j<splitParameter.length;j++){
			if(j<splitParameter.length-1){
				encoded=encoded+splitParameter[j]+"%23";
			}else{
				encoded=encoded+splitParameter[j];
			}
		}
		return encoded;
	}
	native static String getQueryJSON(String url,String method,String urlData)/*-{
		var toReturnData=null;
		var toReturnJson = $wnd.jQuery.ajax(
			{
				type: 'GET',
				url: url+method,
				async:false,
				data: urlData,
				//contentType: "application/json;charset=utf-8",
				datatype: "json",
				success : function toReturn(data){
					toReturnData = data;
				},
				error:function control(msg,url,line){
					alert('A error ocurred: message='+msg.statusText+' ,url='+url+' ,line='+line+'.');
				}
			}
		);
		if(toReturnJson.status != 200 || toReturnJson.readyState != 4){
			return null;
		} 
		return toReturnJson.responseText;
	}-*/;
	private void drawChart(ChartType chartType,String tableTittle, Panel parent,int width,int height,String xTittle,String yTittle,List<StatisticDataValue> values){
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, xTittle);
		dataTable.addColumn(ColumnType.NUMBER, yTittle);
		dataTable.addRows(values.size());
		Collections.sort(values);
		for(int i=0;i<values.size();i++){
			dataTable.setCell(i, 0, LocaleUtil.getBestLabel(values.get(i)).replace("\"", ""));
			dataTable.setCell(i, 1, Double.parseDouble(values.get(i).getY().replace("\"", "")));
		}
		 switch (chartType) {
			case PIE:
				PieChartOptions optionsPie=PieChartOptions.create();
				optionsPie.setTitle(tableTittle);
				optionsPie.setWidth(width);
				optionsPie.setHeight(height);
				optionsPie.setIs3D(true);
				PieChart chartPie=new PieChart();
				parent.add(chartPie);
				chartPie.draw(dataTable, optionsPie);
 				break;
			case LINE:
				LineChartOptions optionsLine=LineChartOptions.create();
				optionsLine.setTitle(tableTittle);
				optionsLine.setWidth(width);
				optionsLine.setHeight(height);
				optionsLine.setCurveType(CurveType.NONE);
				LineChart chartLine=new LineChart();
				parent.add(chartLine);
				chartLine.draw(dataTable, optionsLine);
 				break;
			case BAR:
				BarChartOptions optionsBar=BarChartOptions.create();
				optionsBar.setTitle(tableTittle);
				optionsBar.setWidth(width);
				optionsBar.setHeight(height);
				BarChart chartBar=new BarChart();
				parent.add(chartBar);
				chartBar.draw(dataTable, optionsBar);
 				break;
			default:
				PieChartOptions optionsDef=PieChartOptions.create();
				optionsDef.setTitle(tableTittle);
				optionsDef.setWidth(width);
				optionsDef.setHeight(height);
				optionsDef.setIs3D(true);
				PieChart chartDef=new PieChart();
				parent.add(chartDef);
				chartDef.draw(dataTable, optionsDef);
 				break;
		}
	}
}
