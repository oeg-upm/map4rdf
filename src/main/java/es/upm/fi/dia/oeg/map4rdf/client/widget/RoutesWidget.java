package es.upm.fi.dia.oeg.map4rdf.client.widget;


import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import es.upm.fi.dia.oeg.map4rdf.client.resource.BrowserResources;

public class RoutesWidget extends ResizeComposite{
	private LayoutPanel panel;
	private Grid gridPanel;
	private BrowserResources browserResources;
	private ScrollPanel scrollPanel;
	private String width;
	private String height;
	
	public RoutesWidget(String width, String height,BrowserResources browserResources){
		this.width=width;
		this.height=height;
		this.browserResources=browserResources;
		initWidget(createUi());
	}
	private Widget createUi() {
		panel = new LayoutPanel();
		panel.setSize(width, height);
		//panel.setStyleName(browserResources.css().searchPanel());
		gridPanel = new Grid(0, 3);
		gridPanel.setSize("100%", "100%");
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setSize(width, height);
		scrollPanel.setWidget(gridPanel);
		panel.add(scrollPanel);
		return panel;
	}
	public void setWidget(int row,int column, Widget widget){
		if(column==1){
			widget.addStyleName(browserResources.css().routesResourceBox());
		}
		gridPanel.setWidget(row, column, widget);
	}
	public  CellFormatter getCellFormater(){
		return gridPanel.getCellFormatter();
	}
	public void resizeRows(int rows){
		gridPanel.resizeRows(rows);
	}
	public void removeRow(int row){
		gridPanel.removeRow(row);
	}
	public void resizeHeight(int minPixelHeight,int maxPixelHeight){
		if(gridPanel.getOffsetHeight()==0 || maxPixelHeight==0){
			return;
		}
		if(gridPanel.getOffsetHeight()<maxPixelHeight){
			int pixelHeight=minPixelHeight;
			if(gridPanel.getRowCount()!=0){
				pixelHeight=gridPanel.getOffsetHeight()+15;
			}
			panel.setHeight(pixelHeight+"px");
			scrollPanel.setHeight(pixelHeight+"px");
		}else{
			if(gridPanel.getRowCount()==0){
				panel.setHeight(minPixelHeight+"px");
				scrollPanel.setHeight(minPixelHeight+"px");
			}else if(gridPanel.getRowCount()==1 || gridPanel.getRowCount()==2){
				panel.setHeight(gridPanel.getOffsetHeight()+15+"px");
				scrollPanel.setHeight(gridPanel.getOffsetHeight()+15+"px");
			} else {
				panel.setHeight(maxPixelHeight+"px");
				scrollPanel.setHeight(maxPixelHeight+"px");
			}
		}
	}
}
