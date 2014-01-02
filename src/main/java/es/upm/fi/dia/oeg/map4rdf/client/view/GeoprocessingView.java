package es.upm.fi.dia.oeg.map4rdf.client.view;

import net.customware.gwt.dispatch.client.DispatchAsync;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.BufferPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.DashboardPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.GeoprocessingPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.presenter.RoutesPresenter;
import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserMessages;
import es.upm.fi.dia.oeg.map4rdf.client.util.WidgetsNames;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

public class GeoprocessingView  extends ResizeComposite implements GeoprocessingPresenter.Display{
	private RoutesPresenter routesPresenter;
	private DashboardPresenter dashboardPresenter;
	private BrowserMessages browserMessages;
	private BufferPresenter bufferPresenter;
	private TabLayoutPanel panel;
	public static interface Stylesheet {
		String geoprocessingLabel();
		String searchBox();
		String distanceBox();
		String routesResourceBox();
	}
	@Inject
	public GeoprocessingView(RoutesPresenter routesPresenter, BufferPresenter bufferPresenter, BrowserMessages browserMessages, DispatchAsync dispatchAsync){
		this.routesPresenter=routesPresenter;
		this.bufferPresenter=bufferPresenter;
		this.browserMessages=browserMessages;
		routesPresenter.getDisplay().setGeoprocessingDisplay(this);
		bufferPresenter.getDisplay().setGeoprocessingDisplay(this);
		dispatchAsync.execute(new GetConfigurationParameter(ParameterNames.SUMMARY_WIDGETS), new AsyncCallback<SingletonResult<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SingletonResult<String> value) {	
				if(value.getValue()!=null && !value.getValue().isEmpty()){
					initWidget(createUi(value.getValue()));
				}
			}
		});
		
	}
	private Widget createUi(String widgets) {
		//Grid grid = new Grid(4,1);
		//panel.setSize("98%", "100%");
		//panel= new VerticalPanel();
		panel = new TabLayoutPanel(22, Unit.PX);
		//Label label = new Label(browserMessages.routes());
		//label.setStyleName(browserResoruces.css().geoprocessingLabel());
		//panel.add(label);
		if(widgets!=null){
			if(widgets.contains(WidgetsNames.ROUTES)){
				panel.add(routesPresenter.getDisplay().asWidget(),browserMessages.routes());
				panel.selectTab(routesPresenter.getDisplay().asWidget());
			}
			if(widgets.contains(WidgetsNames.BUFFER)){
				panel.add(bufferPresenter.getDisplay().asWidget(),browserMessages.buffer());
				panel.selectTab(bufferPresenter.getDisplay().asWidget());
			}
			//panel.showWidget(routesPresenter.getDisplay().asWidget());
			
		}
		//grid.setWidget(0, 0, label);
		//DOM.setStyleAttribute(label.getElement(), "textAlign", "center");
		//grid.setWidget(1,0,routesPresenter.getDisplay().asWidget());
		//return grid;
		return panel;
	}
	@Override
	public Widget asWidget() {
		
		return this;
	}

	@Override
	public void setDashboardPresenter(DashboardPresenter dashboardPresenter) {
		
		this.dashboardPresenter=dashboardPresenter;
		routesPresenter.getDisplay().setDashboardPresenter(dashboardPresenter);
		bufferPresenter.getDisplay().setDashboardPresenter(dashboardPresenter);
	}
	@Override
	public void doSelectedView(Widget widget) {
		
		//if(widget isChildrenOf panel)
		if(panel.getWidgetIndex(widget)!=-1){
			panel.selectTab(widget);
			if(dashboardPresenter!=null){
				dashboardPresenter.getDisplay().doSelectedWestWidget(this);
			}
		}
	}
	@Override
	public int getContentHeight() {
		
		return panel.getOffsetHeight();
	}
	@Override
	public int getContentWidth() {
		
		return panel.getOffsetWidth();
	}
	@Override
	public void onResize(){
		panel.onResize();
		routesPresenter.getDisplay().resize();
	}
	/*@Override
	public void removeWidget(Widget widget) {
		
		panel.remove(widget);
	}
	@Override
	public void addWidget(Widget widget, String header) {
		
		panel.add(widget, header);
	}*/

}
