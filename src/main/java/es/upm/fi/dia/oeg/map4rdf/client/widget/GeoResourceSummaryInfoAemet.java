package es.upm.fi.dia.oeg.map4rdf.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import name.alexdeleon.lib.gwtblocks.client.widget.loading.LoadingWidget;
import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.CurveType;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObs;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObsForProperty;
import es.upm.fi.dia.oeg.map4rdf.client.action.ListResult;
import es.upm.fi.dia.oeg.map4rdf.client.resource.AemetMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;
import es.upm.fi.dia.oeg.map4rdf.client.util.DateUtil;
import es.upm.fi.dia.oeg.map4rdf.client.util.LocaleUtil;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Geometry;
import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetIntervalo;
import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetObs;

/**
 * @author Alexander De Leon
 * Adapted by: @author Francisco Siles
 */

public class GeoResourceSummaryInfoAemet implements GeoResourceSummaryInfo {

	public interface Stylesheet {
		String summaryLabelStyle();
		String summaryPropertyName();
		String summaryPropertyValue();
	}

	private AemetMessages aemetMessages;
	private BrowserMessages browserMessages;
	private BrowserResources browserResources;
	private DispatchAsync dispatchAsync;
	private WidgetFactory widgetFactory;
	private VerticalPanel listPanel;
	private FlexTable additionalInfoTable;
	private Panel mainPanel;
	//private OpenLayersMapView display;
	private List<AemetObs> obs;
	private DialogBox loadingBox;
	private int columnLimit=3;
	private DialogBox mainWidget;
	private Label resoruceLabel;
	
	public GeoResourceSummaryInfoAemet(DispatchAsync dispatchAsync,BrowserResources browserResources,BrowserMessages browserMessages, WidgetFactory widgetFactory) {
		this.aemetMessages = GWT.create(AemetMessages.class);/*messages;*/
		this.dispatchAsync = dispatchAsync;
		this.browserMessages = browserMessages;
		this.browserResources = browserResources;
		this.widgetFactory = widgetFactory;
		createUi();
	}
	@Override
	public void setGeoResource(GeoResource resource, Geometry geometry) {
		//this.display = display;
		resoruceLabel.setText(LocaleUtil.getBestLabel(resource));
		listPanel.clear();
		listPanel.add(new Label(browserMessages.loading()));
		GetAemetObs action = new GetAemetObs(resource.getUri());
		dispatchAsync.execute(action, new AsyncCallback<ListResult<AemetObs>>() {
			@Override
			public void onFailure(Throwable caught) {
				widgetFactory.getDialogBox().showError(browserMessages.errorCommunication());
			}
            @Override
            public void onSuccess(ListResult<AemetObs> result) {
            	setVariables(result);	
            	buildWindow();
           	}
		});
	}
	
	private Widget createUi() {
		mainPanel = new VerticalPanel();
		listPanel = new VerticalPanel();
		additionalInfoTable = new FlexTable();
		mainPanel.setWidth("auto");
		mainPanel.add(resoruceLabel=new Label());
		mainPanel.add(additionalInfoTable);
		mainPanel.add(listPanel); 
		loadingBox = new LoadingDialogBox(false, false);
	    loadingBox.setAnimationEnabled(false);
	    loadingBox.setGlassEnabled(true);
	    loadingBox.setWidget(getLoadingContent(browserResources.loadingIcon(),aemetMessages.loadingChart(), browserResources.css()));
	    loadingBox.setText(browserMessages.loading());
	    loadingBox.setTitle(browserMessages.loading());
	    DOM.setStyleAttribute(loadingBox.getElement(), "zIndex", "20");
		mainWidget=new DialogBox(false, false);
		mainWidget.setAnimationEnabled(true);
		mainWidget.setGlassEnabled(false);
		DOM.setStyleAttribute(mainWidget.getElement(), "zIndex", "10");
		Button close = new Button(browserMessages.close());
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				mainWidget.hide();
			}
		});
		VerticalPanel mainDialogPanel=new VerticalPanel();
		mainDialogPanel.add(mainPanel);
		mainDialogPanel.add(close);
		mainWidget.setWidget(mainDialogPanel);
		mainDialogPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(mainPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainDialogPanel.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_CENTER);
		mainWidget.setText(browserMessages.informationTittle(""));
		return mainPanel;
	}
	
	private void setVariables(ListResult<AemetObs> result){
		if(result!=null && result.asList()!=null){
			obs = result.asList();
		}else{
			obs=new ArrayList<AemetObs>();
		}
	}
	private void buildWindow() {
		listPanel.clear();
		String text = aemetMessages.noObserveData();
		try {
			if (obs.isEmpty()) {
				listPanel.add(new Label(text));
			} else {
				int row=0;
				int column=0;
				final AemetObs firstObservation = obs.get(0);
				//listPanel.add(new Label(aemetMessages.station() + " " + LocaleUtil.getBestLabel(firstObservation.getEstacion())));
				listPanel.add(new Label(aemetMessages.timeOfObservation()+" "+firstObservation.getIntervalo().toString()));
				FlexTable propertiesPanel=new FlexTable();
				listPanel.add(propertiesPanel);
				DOM.setStyleAttribute(propertiesPanel.getElement(), "border", "1px solid black");
				DOM.setStyleAttribute(propertiesPanel.getElement(), "borderCollapse", "collapse");
				for (final AemetObs observation : obs) {
					HorizontalPanel obsActual = dameUnidadMedicion(LocaleUtil.getBestLabel(observation.getPropiedad()),
							observation.getUriObs(), Double.toString(observation.getValor()));
					//caracteristicas.add(obsActual);
					DOM.setElementAttribute(obsActual.getElement(), "cellpadding", "8px");
					VerticalPanel graf = new VerticalPanel();
					graf.setSize("35%", "100%");
					graf.add(new Label(aemetMessages.charts()));
					//graf.setSpacing(5);
					Anchor dayAnchor = new Anchor(aemetMessages.day());
					dayAnchor.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							createDayChart(observation, firstObservation.getIntervalo());
						}
					});
					graf.add(dayAnchor);
					Anchor weekAnchor = new Anchor(aemetMessages.week());
					weekAnchor.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							createWeekChart(observation, firstObservation.getIntervalo());
						}
					});
					graf.add(weekAnchor);
					obsActual.add(graf);
					obsActual.setCellWidth(graf, "35%");
					propertiesPanel.setWidget(row,column,obsActual);
					DOM.setStyleAttribute(propertiesPanel.getCellFormatter().getElement(row, column), "border", "1px solid black");
					column++;
					if(column==columnLimit){
						propertiesPanel.getRowFormatter().setVerticalAlign(row, HasVerticalAlignment.ALIGN_MIDDLE);
						row++;
						column=0;
					}
					if(mainWidget.isShowing()){
						mainWidget.hide();
						mainWidget.center();
					}
				}
			}
		} catch (Exception e) {
			widgetFactory.getDialogBox().showError("Error: " + e.getMessage()+ " " + e.toString());
			listPanel.clear();
			listPanel.add(new Label(text));
		}
	}
	
	//helpers
	
	private void createWeekChart(final AemetObs ao, AemetIntervalo day) {
		AemetIntervalo end = new AemetIntervalo(day.getAnno(), day.getMes(), day.getDia(), 00, 00);
		Date aWeekEarlier = new Date( DateUtil.asDate(end).getTime() - (6 * 24 * 60 * 60 * 1000) - (1000*60));
		AemetIntervalo start = DateUtil.asAemetIntervalo(aWeekEarlier);
		createChart(ao, start, end,aemetMessages.week());
	}

	private void createDayChart(final AemetObs ao, AemetIntervalo day) {
		createChart(ao, new AemetIntervalo(day.getAnno(), day.getMes(), day.getDia(), 00, 00),
				new AemetIntervalo(day.getAnno(), day.getMes(), day.getDia(), 23, 59),aemetMessages.day());
	}

	private void createChart(final AemetObs ao, AemetIntervalo start, AemetIntervalo end,final String tittleTime) {
		final GetAemetObsForProperty action = new GetAemetObsForProperty();
		action.setStationUri(ao.getEstacion().getUri());
		action.setPropertyUri(ao.getPropiedad().getUri());
		action.setStart(start);
		action.setEnd(end);
		startLoading();
		dispatchAsync.execute(action, new AsyncCallback<ListResult<AemetObs>>() {

			@Override
			public void onFailure(Throwable caught) {
				stopLoading();
				widgetFactory.getDialogBox().showError(caught.getMessage());
			}

			@Override
			public void onSuccess(ListResult<AemetObs> result) {
				//disp.stopProcessing();
				List<AemetObs> observations = result.asList();
				if (observations.isEmpty()) {
					return;
				}
				DataTable table = DataTable.create();
				table.addColumn(ColumnType.DATETIME);
				table.addColumn(ColumnType.NUMBER,LocaleUtil.getBestLabel(ao.getPropiedad()));
				AemetObs [] aemetObsArray= new AemetObs[observations.size()];
				int i=0;
				for(AemetObs obs: observations){
					aemetObsArray[i++]=obs;
				}
				Arrays.sort(aemetObsArray,new Comparator<AemetObs>(){
					@Override
					public int compare(AemetObs arg0, AemetObs arg1) {
						long time1 = DateUtil.asDate(arg0.getIntervalo()).getTime();
						long time2 = DateUtil.asDate(arg1.getIntervalo()).getTime();
						return (int)(time1-time2);
					}
					
				});
				table.addRows(aemetObsArray.length);
				for(i=0;i<aemetObsArray.length;i++){
					table.setCell(i, 0, DateUtil.asDate(aemetObsArray[i].getIntervalo()));
					table.setCell(i, 1, aemetObsArray[i].getValor());
				}
				String tableTittle= aemetMessages.tittleChartOf(LocaleUtil.getBestLabel(ao.getPropiedad()),tittleTime, LocaleUtil.getBestLabel(ao.getEstacion()));
				Widget plot=createPlot(table,tableTittle);
				AemetChartDialog dialog= new AemetChartDialog(
						tableTittle,
						browserMessages.close(), plot);
				dialog.center();
				stopLoading();
			}

		});
	}

	private LineChart createPlot(DataTable dataTable, String tableTittle) {
		LineChartOptions optionsLine=LineChartOptions.create();
		optionsLine.setTitle(tableTittle);
		optionsLine.setWidth((int)(Window.getClientWidth()*0.3));
		optionsLine.setHeight((int)(Window.getClientHeight()*0.3));
		optionsLine.setCurveType(CurveType.NONE);
		LineChart chartLine=new LineChart();
		chartLine.draw(dataTable, optionsLine);
		return chartLine;

	}

	private HorizontalPanel dameUnidadMedicion(String label, String uri, String valor) {
		//TODO GeoResourceSummaryInfoAemet will be adapted to new model (Wait for O.Corcho to change the model).
		HorizontalPanel panelAct = new HorizontalPanel();
		panelAct.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panelAct.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		Anchor anchor;
		Label valorWidget;
		//panelAct.setSpacing(5);
		if (label.contains("VV")) {
			// return " m/s";
			anchor = new Anchor(aemetMessages.avarageVelocityOfWind()+": ", uri, "_blank");
			valorWidget = new Label(valor + " m/s");
		}else if (label.contains("DV")) {
			anchor = new Anchor(aemetMessages.avarageWind()+": ", uri, "_blank");
			valorWidget = new Label(valor + " "+ aemetMessages.degrees());
		}else if (label.contains("RVIENTO")) {
			anchor = new Anchor(aemetMessages.windTour()+": ", uri, "_blank");
			valorWidget = new Label(valor + " Hm");
		}else if (label.contains("DMAX")) {
			anchor = new Anchor(aemetMessages.maxWind()+": ", uri, "_blank");
			valorWidget = new Label(valor + " " + aemetMessages.degrees());
		}else if (label.contains("VMAX")) {
			anchor = new Anchor(aemetMessages.maxWindVelocity()+": ", uri, "_blank");
			valorWidget = new Label(valor + " m/s");
		}else if (label.contains("TA")) {
			anchor = new Anchor(aemetMessages.airTemperature()+": ", uri, "_blank");
			valorWidget = new Label(valor + " grados C.");
		}else if (label.contains("HR")) {
			anchor = new Anchor(aemetMessages.rh()+": ", uri, "_blank");
			valorWidget = new Label(valor + " %");
		}else if (label.contains("PREC")) {
			anchor = new Anchor(aemetMessages.precipitation()+": ", uri, "_blank");
			valorWidget = new Label(valor + " " +aemetMessages.liters()+"/m2");
		}else if (label.contains("TPR")) {
			anchor = new Anchor(aemetMessages.tempTheDewPoint()+": ", uri, "_blank");
			valorWidget = new Label(valor + " " +aemetMessages.degrees()+" C.");
		}else if (label.contains("PRES_nmar")) {
			anchor = new Anchor(aemetMessages.pressureReducedToSeaLevel()+": ", uri, "_blank");
			valorWidget = new Label(valor + " hPa");
		} else if (label.contains("PRES")) {
			anchor = new Anchor("Presion: ", uri, "_blank");
			valorWidget = new Label(valor + " hPa");
		}else if (label.contains("RAGLOB")) {
			anchor = new Anchor(aemetMessages.globalRadiation()+": ", uri, "_blank");
			valorWidget = new Label(valor + " KJ/m2");
		}else if (label.contains("GEO925")) {
			anchor = new Anchor("GEO925", uri, "_blank");
			valorWidget = new Label(valor + "m");
		}else if (label.contains("GEO850")) {
			anchor = new Anchor("GEO850", uri, "_blank");
			valorWidget = new Label(valor + " m");
		}else{
			anchor = new Anchor(label,uri,"_blank");
			valorWidget = new Label(valor);
		}
		panelAct.add(anchor);
		panelAct.add(valorWidget);
		panelAct.setWidth("100%");
		panelAct.setCellWidth(anchor, "40%");
		panelAct.setCellWidth(valorWidget, "20%");
		return panelAct;
	}
	private void startLoading(){
		loadingBox.center();
	}
	private void stopLoading(){
		loadingBox.hide();
	}
	private Widget getLoadingContent(ImageResource loadingIcon, String message, LoadingWidget.Stylesheet style){
		Label label = new Label(message);
		label.setStyleName(style.loadingWidgetLabelStyle());
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setSpacing(8);
		Image icon = new Image(loadingIcon);
		icon.setStyleName(style.loadingWidgetIconStyle());
		DOM.setStyleAttribute(label.getElement(), "float", "right");
		DOM.setStyleAttribute(label.getElement(), "verticalAlign", "middle");
		panel.add(icon);
		panel.add(label);
		return panel;
	}
	
	
	
	@Override
	public void addAdditionalInfo(Map<String, String> additionalsInfo) {
		if(additionalsInfo!=null){
			int rows=0;
			for(String key: additionalsInfo.keySet()){
				additionalInfoTable.setWidget(rows, 0, new Label(key));
				additionalInfoTable.setWidget(rows, 1, new Label(additionalsInfo.get(key)));
				rows++;
			}
		}
	}
	@Override
	public Widget getWidget() {
		return mainPanel;
	}
	@Override
	public void clearAdditionalInfo() {
		additionalInfoTable.clear();
	}
	
	@Override
	public boolean isVisible() {
		return mainWidget.isShowing();
	}

	@Override
	public void show() {
		mainWidget.center();
	}

	@Override
	public void close() {
		mainWidget.hide();
	}

	public class LoadingDialogBox extends DialogBox{
		public LoadingDialogBox(){
			super();
		}
		public LoadingDialogBox(boolean autoHide, boolean modal){
			super(autoHide,modal);
		}
		@Override
		protected void beginDragging(MouseDownEvent e){
			e.preventDefault();
		}
	}
	public class AemetChartDialog extends DialogBox{
		public AemetChartDialog(String tittle,String closeMessage,Widget chart){
			super();
			// Set the dialog box's caption.
			setText(tittle);

			// Enable animation.
			setAnimationEnabled(true);

			// Disable glass background.
			setGlassEnabled(false);
			
			//Disable that the dialog disable other events.
			setModal(false);
			
			DOM.setStyleAttribute(this.getElement(), "zIndex", "10");
			
			// DialogBox is a SimplePanel, so you have to set its widget
			// property to whatever you want its contents to be.
			Button close = new Button(closeMessage);
			close.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					AemetChartDialog.this.hide();
				}
			});
			VerticalPanel mainPanel=new VerticalPanel();
			mainPanel.add(chart);
			mainPanel.add(close);
			setWidget(mainPanel);
			mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			mainPanel.setCellHorizontalAlignment(chart, HasHorizontalAlignment.ALIGN_CENTER);
			mainPanel.setCellHeight(chart, ((int)(Window.getClientHeight()*0.31))+"px");
			mainPanel.setCellWidth(chart, ((int)(Window.getClientWidth()*0.31))+"px");
			mainPanel.setCellHorizontalAlignment(close, HasHorizontalAlignment.ALIGN_CENTER);
			

		}
	}
}

