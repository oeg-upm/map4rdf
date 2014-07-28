package es.upm.fi.dia.oeg.map4rdf.client.view;

import net.customware.gwt.dispatch.client.DispatchAsync;
import net.customware.gwt.presenter.client.EventBus;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.client.conf.ConfIDInterface;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.event.OnSelectedConfigurationHandler;
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
	public GeoprocessingView(final ConfIDInterface configID,RoutesPresenter routesPresenter, BufferPresenter bufferPresenter, BrowserMessages browserMessages, final DispatchAsync dispatchAsync, EventBus eventBus){
		this.routesPresenter=routesPresenter;
		this.bufferPresenter=bufferPresenter;
		this.browserMessages=browserMessages;
		routesPresenter.getDisplay().setGeoprocessingDisplay(this);
		bufferPresenter.getDisplay().setGeoprocessingDisplay(this);
		if(configID.existsConfigID()){
			initAsync(configID.getConfigID(), dispatchAsync);
		}else{
			eventBus.addHandler(OnSelectedConfiguration.getType(), new OnSelectedConfigurationHandler() {
				
				@Override
				public void onSelectecConfiguration(String configID) {
					initAsync(configID, dispatchAsync);
				}
			});
		}
		panel = new TabLayoutPanel(22, Unit.PX);
		initWidget(panel);
	}
	private void initAsync(String configID, DispatchAsync dispatchAsync){
		dispatchAsync.execute(new GetConfigurationParameter(configID,ParameterNames.SUMMARY_WIDGETS), new AsyncCallback<SingletonResult<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SingletonResult<String> value) {	
				if(value.getValue()!=null && !value.getValue().isEmpty()){
					createAsyncUi(value.getValue());
				}
			}
		});
	}
	private void createAsyncUi(String widgets) {
		if(widgets!=null){
			if(widgets.contains(WidgetsNames.ROUTES)){
				panel.add(routesPresenter.getDisplay().asWidget(),browserMessages.routes());
				panel.selectTab(routesPresenter.getDisplay().asWidget());
			}
			if(widgets.contains(WidgetsNames.BUFFER)){
				panel.add(bufferPresenter.getDisplay().asWidget(),browserMessages.buffer());
				panel.selectTab(bufferPresenter.getDisplay().asWidget());
			}
		}
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

}
