package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.util.ArrayList;
import java.util.List;



import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetRoutePoints;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetRoutePointsResult;
import es.upm.fi.dia.oeg.map4rdf.server.cartociudad.RouteFinderCall;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.PointBean;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;


public class GetRoutePointsHandler implements ActionHandler<GetRoutePoints, GetRoutePointsResult> {
	
	private int timeoutMiliSeconds;
	private Logger logger = Logger.getLogger(GetRoutePointsHandler.class);
	@Inject
	public GetRoutePointsHandler(@Named(ParameterNames.ROUTES_SERVICE_TIMEOUT_MILISECONDS) String timeoutMiliseconds){
		try{
			this.timeoutMiliSeconds=Integer.valueOf(timeoutMiliseconds);
		}catch (Exception e){
			logger.warn("An error ocurred when parse to int the parameter: "+
								ParameterNames.ROUTES_SERVICE_TIMEOUT_MILISECONDS+
								". Timeout set to default (30000 ms). Please change parameter value and"+
								" reload the service.");
			this.timeoutMiliSeconds=30000;
		}
	}
	@Override
	public GetRoutePointsResult execute(GetRoutePoints action,
			ExecutionContext context) throws ActionException {
		List<es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point> points = new ArrayList<es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point>();
		boolean incorrectEPSG=false;
		for(Point point:action.getPoints()){
			if(point.getProjection().toLowerCase().trim().equals("epsg:4326")){
				incorrectEPSG=true;
				break;
			}
		}
		if(incorrectEPSG){
			throw new ActionException("EPSG incorrect in some point, can't use Route service");
		}
		List<Point> toReturn = new ArrayList<Point>();
		if(this.timeoutMiliSeconds!=0){
			for(Point point:action.getPoints()){
				points.add(new es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point(String.valueOf(point.getX()),String.valueOf(point.getY())));
			}
			RouteFinderCall rfc = new RouteFinderCall("http://www.cartociudad.es/wps/WebProcessingService",timeoutMiliSeconds);
			List<List<es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point>> paths = rfc.getPath(points);
			for(List<es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point> listPoints:paths){
				for(es.upm.fi.dia.oeg.map4rdf.server.cartociudad.types.Point point: listPoints){
					toReturn.add(new PointBean("", Double.parseDouble(point.getLat()),Double.parseDouble(point.getLon()),"EPSG:4326"));
				}
			}
		}
		return new GetRoutePointsResult(toReturn);
		
	}

	@Override
	public Class<GetRoutePoints> getActionType() {
		
		return GetRoutePoints.class;
	}

	@Override
	public void rollback(GetRoutePoints arg0, GetRoutePointsResult arg1,
			ExecutionContext arg2) throws ActionException {
		
		
	}

}
