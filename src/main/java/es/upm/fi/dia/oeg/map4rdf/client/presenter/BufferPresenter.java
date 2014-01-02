package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

import com.google.inject.Inject;


public class BufferPresenter extends ControlPresenter<BufferPresenter.Display> {
	@Inject
	public BufferPresenter(Display display, EventBus eventBus) {
		super(display, eventBus);
	}

	public interface Display extends WidgetDisplay{
		void setDashboardPresenter(DashboardPresenter dashboardPresenter);
		void setGeoprocessingDisplay(GeoprocessingPresenter.Display geoprocessingPresenterDisplay);
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
