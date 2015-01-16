/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.geostatisticsConformanceTest.fileChecker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

import es.upm.fi.dia.oeg.map4rdf.server.geostatisticsConformanceTest.fileChecker.callbacks.InfoCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fsiles
 */
public class StatisticsFileChecker {
    
    private final RDFNode datacube_DataSet=ResourceFactory.createProperty("http://purl.org/linked-data/cube#","DataSet");
    private final Property rdfType=ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
    private final Property rdfsLabel=ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#", "label");
    private final Property dcCreator=ResourceFactory.createProperty("http://purl.org/dc/terms/", "creator");
    private final Property datacube_dataSet=ResourceFactory.createProperty("http://purl.org/linked-data/cube#","dataSet");
    private final Resource datacube_Observation=ResourceFactory.createProperty("http://purl.org/linked-data/cube#","Observation");
    private final Property datacube_structure = ResourceFactory.createProperty("http://purl.org/linked-data/cube#","structure");
    private final Property datacube_component = ResourceFactory.createProperty("http://purl.org/linked-data/cube#","component");
    private final Property datacube_measure = ResourceFactory.createProperty("http://purl.org/linked-data/cube#","measure");
    private final Property datacube_dimension = ResourceFactory.createProperty("http://purl.org/linked-data/cube#","dimension");
    private final Property rdfsRange = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#","range");
    private final Property datacube_aggregatedWith = ResourceFactory.createProperty("http://purl.org/linked-data/cube#","aggregatedWith");
    
    //cambiar
    //fin cambiar
    private final File fileToCheck;
    private final InfoCallback infoCallback;
    private int step;
    private final int totalSteps;
    private int warnings;
    private int errors;
    
    public StatisticsFileChecker(File file, InfoCallback infoCallback){
        this.fileToCheck = file;
        this.infoCallback = infoCallback;
        this.step=0;
        this.totalSteps=5;
    }
    public StatisticsFileChecker(String file, InfoCallback infoCallback){
        this.fileToCheck = new File(file);
        this.infoCallback = infoCallback;
        this.step=0;
        this.totalSteps=5;
    }
    
    public void analize(){
        this.step=0;
        this.warnings=0;
        this.errors=0;
        try{
            Model model = ModelFactory.createDefaultModel();
            infoCallback.addLog("Se procede a comprobar el fichero.", InfoCallback.LogLevel.Info);
            infoCallback.setInfo("Comprobando el fichero.");
            if(!fileToCheck.exists()){
                infoCallback.addLog("El fichero no existe.", InfoCallback.LogLevel.Fatal);
                infoCallback.setInfo("Terminado.");
                infoCallback.setProgress(100.0);
                infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas informacion.");
                return;
            }
            if(!fileToCheck.canRead()){
                infoCallback.addLog("El fichero no se puede leer (No hay permisos de lectura).", InfoCallback.LogLevel.Fatal);
                infoCallback.setInfo("Terminado.");
                infoCallback.setProgress(100.0);
                infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas informacion.");
                return;
            }
            if(!fileToCheck.isFile()){
                infoCallback.addLog("El fichero no es un fichero (Puede que sea un directorio un acceso directo).", InfoCallback.LogLevel.Fatal);
                infoCallback.setInfo("Terminado.");
                infoCallback.setProgress(100.0);
                infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas informacion.");
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
                infoCallback.onFinish("Se finalizo el test con un error (grave). \nConsulte el cuadro de log para mas informacion.");
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
                infoCallback.onFinish("Se finalizo el test con un error grave. \nConsulte el cuadro de log para mas informacion.");
                return;
            }
            infoCallback.addLog("Se termino de generar el modelo.", InfoCallback.LogLevel.Info);
            infoCallback.setProgress(getNewPercent(++step, totalSteps));
            infoCallback.addLog("Se procede a leer todos las estadisticas que tenga el valor: "+datacube_DataSet+" en la propiedad: "+rdfType, InfoCallback.LogLevel.Info);
            infoCallback.setInfo("Leyendo stadisticas.");
            ResIterator iterator=model.listResourcesWithProperty(rdfType,datacube_DataSet);
            List<Resource> statistics = new ArrayList<Resource>();
            while (iterator.hasNext()) {
                Resource stat= iterator.next();
                statistics.add(stat);
            }
            infoCallback.setProgress(getNewPercent(++step, totalSteps));
            infoCallback.addLog("Se termino de leer las stadisticas se han encontrado "+ statistics.size() + " uris.", InfoCallback.LogLevel.Info);
            infoCallback.addLog("Se procede a leer las propiedades y objetos de todas las estadisticas.", InfoCallback.LogLevel.Info);
            infoCallback.setInfo("Leyendo estadisticas.");
            for(Resource stat:statistics){
                infoCallback.addLog("Leyendo la estadi�stica: "+stat.getURI(), InfoCallback.LogLevel.Info);
                Statement label= stat.getProperty(rdfsLabel);
                if(label==null){
                    warnings++;
                    infoCallback.addLog("\tNo se encontro ningun valor para la propiedad: "+rdfsLabel+" de este recurso.", InfoCallback.LogLevel.Warning);
                }
                Statement creator= stat.getProperty(dcCreator);
                if(creator==null){
                    warnings++;
                    infoCallback.addLog("\tNo se encontro ningun valor para la propiedad: "+dcCreator+" de este recurso.", InfoCallback.LogLevel.Warning);
                }
                ResIterator observations = model.listSubjectsWithProperty(datacube_dataSet, stat);
                boolean correctObservations=false;
                while (observations.hasNext()) {
                   Resource resource= observations.next();
                   Resource prop=resource.getPropertyResourceValue(rdfType);
                   if(prop!=null && datacube_Observation.getURI().equals(prop.getURI())){
                       correctObservations=true;
                   }
                }
                if(!correctObservations){
                    errors++;
                    infoCallback.addLog("\tNo se encontro ningun recurso que tenga la propiedad: "+datacube_dataSet+" y como valor la estadística.", InfoCallback.LogLevel.Error);
                }
                NodeIterator components = model.listObjectsOfProperty(stat, datacube_component);
                boolean haveComponents=false;
                while(components.hasNext()){
                    haveComponents=true;
                    RDFNode comp = components.next();
                    boolean haveMeasureOrDimension=false;              
                    if(comp.isResource()){
                        NodeIterator measures = model.listObjectsOfProperty(comp.asResource(), datacube_measure);
                        while(measures.hasNext()){
                            haveMeasureOrDimension=true;
                            RDFNode measu = measures.next();
                            if(measu.isResource()){
                                if(!model.listObjectsOfProperty(measu.asResource(), datacube_aggregatedWith).hasNext()){
                                    warnings++;
                                    infoCallback.addLog("\tEl recurso \""+measu.asResource().getURI()+"\" no tiene ningún elemento con la propiedad \""+datacube_aggregatedWith+"\"", InfoCallback.LogLevel.Warning);
                                }
                                if(!model.listObjectsOfProperty(measu.asResource(), rdfsRange).hasNext()){
                                    errors++;
                                    infoCallback.addLog("\tEl recurso \""+measu.asResource().getURI()+"\" no tiene ningún elemento con la propiedad \""+rdfsRange+"\"", InfoCallback.LogLevel.Error);
                                }
                                if(!model.listObjectsOfProperty(measu.asResource(), rdfsLabel).hasNext()){
                                    errors++;
                                    infoCallback.addLog("\tEl recurso \""+measu.asResource().getURI()+"\" no tiene ningún elemento con la propiedad \""+rdfsLabel+"\"", InfoCallback.LogLevel.Error);
                                }
                            }else{
                                errors++;
                                infoCallback.addLog("\tEl elemento \""+measu+"\" no es un recurso.", InfoCallback.LogLevel.Error);
                            }
                        }
                        NodeIterator dimensions = model.listObjectsOfProperty(comp.asResource(), datacube_dimension);
                        while(dimensions.hasNext()){
                            haveMeasureOrDimension=true;
                            RDFNode dim = dimensions.next();
                            if(dim.isResource()){
                                if(!model.listObjectsOfProperty(dim.asResource(), rdfsRange).hasNext()){
                                    errors++;
                                    infoCallback.addLog("\tEl recurso \""+dim.asResource().getURI()+"\" no tiene ningún elemento con la propiedad \""+rdfsRange+"\"", InfoCallback.LogLevel.Error);
                                }
                                if(!model.listObjectsOfProperty(dim.asResource(), rdfsLabel).hasNext()){
                                    warnings++;
                                    infoCallback.addLog("\tEl recurso \""+dim.asResource().getURI()+"\" no tiene ningún elemento con la propiedad \""+rdfsLabel+"\"", InfoCallback.LogLevel.Warning);
                                }
                            }else{
                                errors++;
                                infoCallback.addLog("\tEl elemento \""+dim+"\" no es un recurso.", InfoCallback.LogLevel.Error);
                            }
                        }
                        if(!haveMeasureOrDimension){
                            errors++;
                            infoCallback.addLog("\tEl recurso \""+comp.asResource().getURI()+"\" no tiene ningun elemento con la propiedad \""
                                    +datacube_measure+"\" o con la propiedad \""
                                    +datacube_dimension+"\"", InfoCallback.LogLevel.Error);
                        }
                    }else{
                        errors++;
                        infoCallback.addLog("\tEl elemento \""+comp+"\" no es un recurso.", InfoCallback.LogLevel.Error);
                    }
                }
                if(!haveComponents){
                    errors++;
                    infoCallback.addLog("\tEl recurso \""+stat+"\" no tiene ningun elemento con la propiedad \""+datacube_component+"\"", InfoCallback.LogLevel.Error);
                }
            }
        }catch(Exception e){
            infoCallback.addLog("Se produjo una excepcion inesperada.", InfoCallback.LogLevel.Fatal);
            infoCallback.addLog(e.toString()+":"+e.getMessage(), InfoCallback.LogLevel.Fatal);
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
    
    private double getNewPercent(int step,int totalSteps){
        return ((double)step/(double)totalSteps)*100.0;
    }
}
