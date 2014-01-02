package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;

import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class GeoprocessingPresenter extends ControlPresenter<GeoprocessingPresenter.Display> {
	@Inject
	public GeoprocessingPresenter(Display display, EventBus eventBus) {
		super(display, eventBus);
	}

	public interface Display extends WidgetDisplay{
		void setDashboardPresenter(DashboardPresenter dashboardPresenter);
		/*void addWidget(Widget widget,String header);
		void removeWidget(Widget widget);*/
		void doSelectedView(Widget widget);
		int getContentHeight();
		int getContentWidth();
	}

	@Override
	protected void onBind() {
		
		
	}

	@Override
	protected void onUnbind() {
		
		
	}

	@Override
	protected void onRevealDisplay() {
		
		
	}

}