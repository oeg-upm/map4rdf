package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetBufferGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetBufferGeoResourcesResult;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.server.dao.Map4rdfDao;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBox;
import es.upm.fi.dia.oeg.map4rdf.share.BoundingBoxBean;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;
import es.upm.fi.dia.oeg.map4rdf.share.Point;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinate;
import es.upm.fi.dia.oeg.map4rdf.share.TwoDimentionalCoordinateBean;

public class GetBufferGeoResourcesHandler implements ActionHandler<GetBufferGeoResources, GetBufferGeoResourcesResult> {
	private Map4rdfDao dao;
	private final double earthRadious=6356.8;
	@Inject
	public GetBufferGeoResourcesHandler(Map4rdfDao dao){
		this.dao=dao;
	}
	@Override
	public GetBufferGeoResourcesResult execute(GetBufferGeoResources action,
			ExecutionContext context) throws ActionException {
		
		List<GeoResource> geoResources= new ArrayList<GeoResource>();
		BoundingBox boundingBox=null;
		try {
			if(action.getGeometry().getPoints().size()<1){
				throw new ActionException("The geometry doesn't contains points");
			}
			Point point=action.getGeometry().getPoints().iterator().next();
			boundingBox=getBoundingBox(point.getY(),point.getX(), action.getRadiousKM());
			geoResources=dao.getNextPoints(boundingBox, 200);
		} catch (DaoException e) {
			throw new ActionException("Error with try to get nextPoints");
		}
		List<GeoResource> resultList = new ArrayList<GeoResource>();
		int cantidad=0;
		for(GeoResource i: geoResources){
			if(cantidad==200){
				break;
			}
			if(i.getUri()!=null && !i.getUri().equals(action.getGeoResourceUri())){
				resultList.add(i);
				cantidad++;
			}	
		}
		GetBufferGeoResourcesResult result= new GetBufferGeoResourcesResult(resultList, boundingBox);
		return result;
	}

	@Override
	public Class<GetBufferGeoResources> getActionType() {
		
		return GetBufferGeoResources.class;
	}


	@Override
	public void rollback(GetBufferGeoResources arg0,
			GetBufferGeoResourcesResult arg1, ExecutionContext arg2)
			throws ActionException {
		
		
	}
	private BoundingBox getBoundingBox(double latitude,double longitude,double radiousKM){
		double top=0.0;
		double right=0.0;
		double bottom=0.0;
		double left=0.0;
		double diffLon=Math.abs((radiousKM*360)/(2*Math.PI*earthRadious*Math.cos(Math.toRadians(latitude))));
		double diffLat=Math.abs((radiousKM*360)/(2*Math.PI*earthRadious));
		right=longitude+diffLon;
		left=longitude-diffLon;
		top=latitude+diffLat;
		bottom=latitude-diffLat;
		if(top>90){
			top=90;
		}
		if(bottom<-90){
			bottom=-90;
		}
		if(left<-180){
			left=-180;
		}
		if(right>180){
			right=180;
		}
		TwoDimentionalCoordinate bottomLeft=new TwoDimentionalCoordinateBean(left, bottom);
		TwoDimentionalCoordinate topRight=new TwoDimentionalCoordinateBean(right,top);
		return new BoundingBoxBean(bottomLeft, topRight);
	}
}
