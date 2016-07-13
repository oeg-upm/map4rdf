package es.upm.fi.dia.oeg.map4rdf.client.presenter;

import com.google.inject.Inject;

import name.alexdeleon.lib.gwtblocks.client.ControlPresenter;
import net.customware.gwt.presenter.client.EventBus;
import net.customware.gwt.presenter.client.widget.WidgetDisplay;

public class SelectConfigurationPresenter  extends ControlPresenter<SelectConfigurationPresenter.Display>{
	
	@Inject
	public SelectConfigurationPresenter(Display display, EventBus eventBus) {
		super(display, eventBus);
	}

	public interface Display extends WidgetDisplay{
		
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
