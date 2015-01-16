/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.geostatisticsConformanceTest.fileChecker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.vividsolutions.jts.io.WKTReader;

import es.upm.fi.dia.oeg.map4rdf.server.geostatisticsConformanceTest.fileChecker.callbacks.InfoCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 *
 * @author fsiles
 */
public class GeoSPARQLFileChecker {
    
    private final File fileToCheck;
    private final InfoCallback infoCallback;
    private int step;
    private final int totalSteps;
    private final Property geosparql_hasGeometry=ResourceFactory.createProperty("http://www.opengis.net/ont/geosparql#","hasGeometry");
    private final Property geosparql_asWKT=ResourceFactory.createProperty("http://www.opengis.net/ont/geosparql#","asWKT");
    private static enum WKTTypes{Point, LineString, Polygon,MultiPoint,MultiPolygon,MultiLineString}
    private static String validWKTTypes="{Point, LineString, Polygon,MultiPoint,MultiPolygon,MultiLineString}";
    private int warnings=0;
    private int errors=0;
    public GeoSPARQLFileChecker(File file, InfoCallback infoCallback){
        this.fileToCheck = file;
        this.infoCallback = infoCallback;
        step=0;
        totalSteps=5;
    }
    public GeoSPARQLFileChecker(String file, InfoCallback infoCallback){
        this.fileToCheck = new File(file);
        this.infoCallback = infoCallback;
        this.step=0;
        this.totalSteps=5;
    }
    
    public void analize(){
        warnings=0;
        errors=0;
        try{
        Model model = ModelFactory.createDefaultModel();
        infoCallback.addLog("Se procede a comprobar el fichero.", InfoCallback.LogLevel.Info);
        infoCallback.setInfo("Comprobando el fichero.");
        if(!fileToCheck.exists()){
            infoCallback.addLog("El fichero no existe.", InfoCallback.LogLevel.Fatal);
            infoCallback.setInfo("Terminado.");
            infoCallback.setProgress(100.0);
            infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas información.");
            return;
        }
        if(!fileToCheck.canRead()){
            infoCallback.addLog("El fichero no se puede leer (No hay permisos de lectura).", InfoCallback.LogLevel.Fatal);
            infoCallback.setInfo("Terminado.");
            infoCallback.setProgress(100.0);
            infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas información.");
            return;
        }
        if(!fileToCheck.isFile()){
            infoCallback.addLog("El fichero no es un fichero (Puede que sea un directorio un acceso directo).", InfoCallback.LogLevel.Fatal);
            infoCallback.setInfo("Terminado.");
            infoCallback.setProgress(100.0);
            infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas información.");
            return;
        }
        infoCallback.addLog("Se termino de comprobar el fichero.", InfoCallback.LogLevel.Info);
        infoCallback.setProgress(getNewPercent(++step, totalSteps));
        infoCallback.addLog("Se procede generar el modelo Turtle del fichero.", InfoCallback.LogLevel.Info);
        infoCallback.setInfo("Leyendo datos en Turtle");
        InputStream is=null;
        try {
            is = new FileInputStream(fileToCheck);
        } catch (Exception e) {
            infoCallback.addLog("Se produjo una excepcion al obtener el input stream para leer el fichero: ", InfoCallback.LogLevel.Fatal);
            infoCallback.addLog(e.getMessage(), InfoCallback.LogLevel.Fatal);
            for(StackTraceElement i:e.getStackTrace()){
                infoCallback.addLog("\t"+i.toString() ,InfoCallback.LogLevel.Fatal);
            }
            infoCallback.setInfo("Terminado.");
            infoCallback.setProgress(100.0);
            infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas información.");
            return;
        }
        try{
            model.read(is, null, "TURTLE");
            infoCallback.setProgress(getNewPercent(++step, totalSteps));
            infoCallback.addLog("Se termino de generar el modelo.", InfoCallback.LogLevel.Info);
        }catch(Exception e){
            infoCallback.addLog("Se produjo una excepcion al leer el modelo.", InfoCallback.LogLevel.Fatal);
            infoCallback.addLog(e.getMessage(), InfoCallback.LogLevel.Fatal);
            for(StackTraceElement i:e.getStackTrace()){
                infoCallback.addLog("\t"+i.toString() ,InfoCallback.LogLevel.Fatal);
            }
            infoCallback.setInfo("Terminado.");
            infoCallback.setProgress(100.0);
            infoCallback.onFinish("Se finalizo el test con un error grave. \nConsulte el cuadro de log para mas información.");
            return;
        }
        infoCallback.addLog("Se termino de generar el modelo.", InfoCallback.LogLevel.Info);
        infoCallback.setProgress(getNewPercent(++step, totalSteps));
        infoCallback.addLog("Se procede a leer todos los recursos que tengan la propiedad: "+geosparql_hasGeometry, InfoCallback.LogLevel.Info);
        infoCallback.setInfo("Leyendo recursos.");
        ResIterator iterator=model.listResourcesWithProperty(geosparql_hasGeometry);
        List<Resource> resources = new ArrayList<Resource>();
        while (iterator.hasNext()) {
            Resource resource= iterator.next();
            resources.add(resource);
            Statement posibleFacet = resource.getProperty(RDF.type);
            infoCallback.addLog("Encontrado recurso: "+resource.getURI(), InfoCallback.LogLevel.Info);
            if(posibleFacet==null){
                warnings++;
                infoCallback.addLog("No se encontro ningun valor para la propiedad rdf:type de este recurso. ¿El recurso tiene algún otro tipo de faceta definida?", InfoCallback.LogLevel.Warning);
            }
        }
        infoCallback.setProgress(getNewPercent(++step, totalSteps));
        infoCallback.addLog("Se termino de leer los recursos se han encontrado "+ resources.size() + " uris.", InfoCallback.LogLevel.Info);
        infoCallback.addLog("Se procede a leer las geometrias y el wkt de todos los recursos.", InfoCallback.LogLevel.Info);
        infoCallback.setInfo("Leyendo geometrias.");
        int numbersOfResourcesAnalized=0;
        for(Resource resource: resources){
            infoCallback.addLog("Leyendo geometria de:"+resource.getURI(), InfoCallback.LogLevel.Info);
            Resource geometria = resource.getPropertyResourceValue(geosparql_hasGeometry);
            infoCallback.addLog("Uri de la geometria: "+geometria.getURI(), InfoCallback.LogLevel.Info);
            Statement wktStatement = geometria.getProperty(geosparql_asWKT);
            String wkt = wktStatement.getLiteral().getString();
            infoCallback.addLog("WKT de la geometria: "+wkt.substring(0, 200), InfoCallback.LogLevel.Info);
            if(isValidWKT(wkt, infoCallback)){
                infoCallback.addLog("El WKT es correcto.", InfoCallback.LogLevel.Info);
            }else{
                errors++;
                infoCallback.addLog("El WKT es INCORRECTO.", InfoCallback.LogLevel.Error);
            }
            numbersOfResourcesAnalized++;
            infoCallback.setProgress(getNewPercent((step+numbersOfResourcesAnalized*(1.0/resources.size())), totalSteps));
        }
        }catch(Exception e){
            infoCallback.addLog("Se produjo una excepcion inesperada.", InfoCallback.LogLevel.Fatal);
            infoCallback.addLog(e.getMessage(), InfoCallback.LogLevel.Fatal);
            for(StackTraceElement i:e.getStackTrace()){
                infoCallback.addLog("\t"+i.toString() ,InfoCallback.LogLevel.Fatal);
            }
            infoCallback.setInfo("Terminado.");
            infoCallback.setProgress(100.0);
            infoCallback.onFinish("Se finalizo el test con una excepción inesperada. \nConsulte el cuadro de log para mas información.");
            return;
        }
        infoCallback.setProgress(100);
        infoCallback.addLog("Se termino el analisis.", InfoCallback.LogLevel.Info);
        infoCallback.setInfo("Terminado");
        infoCallback.onFinish("Se finalizo el test con "+warnings+" alertas y "+errors+" errores. \nConsulte el cuadro de log para mas información.");
    }
    
    private double getNewPercent(double step,double totalSteps){
        return (step/totalSteps)*100.0;
    }
    
    private boolean isValidWKT(String wkt,InfoCallback infoCallback){
        try{
            String realWKTText="";
            int firtsIndex=-1;
            for(WKTTypes i: WKTTypes.values()){
            	int index=wkt.toLowerCase().indexOf(i.toString().toLowerCase());
            	if(index>=0 && (index<firtsIndex || firtsIndex==-1)){
                    firtsIndex=index;
                }
            }
            if(firtsIndex==-1){
                infoCallback.addLog("No se encontro un tipo de WKT valido. Tipos validos: "+validWKTTypes, InfoCallback.LogLevel.Error);
                return false;
            }
            realWKTText=wkt.substring(firtsIndex, wkt.length());
            int count=1;
            int lastIndex=-1;
            int searchIndex=realWKTText.indexOf("(");
            if(searchIndex==-1 || searchIndex==realWKTText.length()-1){
                infoCallback.addLog("No se encuentra el caracter de comienzo: '(' .", InfoCallback.LogLevel.Error);
                return false;
            }
            for(int i=searchIndex+1;i<realWKTText.length();i++){
		if(realWKTText.charAt(i)=='('){
			count++;
		}
		if(realWKTText.charAt(i)==')'){
			count--;
		}
		if(count==0){
			lastIndex=i+1;
			break;
		}
            }
            if(lastIndex==-1){
                infoCallback.addLog("Estan desbalanceados los caracteres : '(' y ')' .", InfoCallback.LogLevel.Error);
		return false;
            }
            try{
                /*CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
                GeometryFactory geometryFactory = new GeometryFactoryImpl(crs, new PositionFactoryImpl(crs));
                PositionFactory positionFactory = new PositionFactoryImpl();
                PrimitiveFactory primitiveFactory = new PrimitiveFactoryImpl();
                AggregateFactory aggregateFactory = new AggregateFactoryImpl();
                WKTParser parser = new WKTParser( geometryFactory, primitiveFactory, positionFactory, aggregateFactory);
                parser.parse(realWKTText);*/
                com.vividsolutions.jts.geom.GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		WKTReader reader = new WKTReader(geometryFactory);
		com.vividsolutions.jts.geom.Geometry geometry = reader.read(realWKTText);
                if(geometry==null){
                    infoCallback.addLog("El texto WKT no forma un poligono valido.", InfoCallback.LogLevel.Error);
                    infoCallback.addLog("Compruebe que el WKT empieze y termine por el mismo punto.", InfoCallback.LogLevel.Error);
                    infoCallback.addLog("Compruebe tambien que no existe intersecciones entre las lineas que genera cada par de puntos.", InfoCallback.LogLevel.Error);
                    return false;
                }
                return true;
            }catch(Exception e){
                infoCallback.addLog("Se produjo una excepcion al parsear el WKT.", InfoCallback.LogLevel.Fatal);
                infoCallback.addLog(e.getMessage(), InfoCallback.LogLevel.Fatal);
                for(StackTraceElement i:e.getStackTrace()){
                    infoCallback.addLog("\t"+i.toString() ,InfoCallback.LogLevel.Fatal);
                }
                return false;
            }
        }catch(Exception e){
            infoCallback.addLog("Se produjo una excepcion al analizar el wkt.", InfoCallback.LogLevel.Fatal);
            infoCallback.addLog(e.getMessage(), InfoCallback.LogLevel.Fatal);
            for(StackTraceElement i:e.getStackTrace()){
                infoCallback.addLog("\t"+i.toString() ,InfoCallback.LogLevel.Fatal);
            }
            return false;
        }
    }
}
