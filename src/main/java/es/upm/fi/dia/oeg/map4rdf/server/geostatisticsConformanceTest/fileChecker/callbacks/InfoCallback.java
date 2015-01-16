/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.upm.fi.dia.oeg.map4rdf.server.geostatisticsConformanceTest.fileChecker.callbacks;

/**
 *
 * @author fsiles
 */
public interface InfoCallback {

    public enum LogLevel{Info,Warning,Error,Fatal}
    
    //Set progress of information in percent
    public void setProgress(double percent);
    
    //Set short label that is doing
    public void setInfo(String shortInfo);
    
    //Set line of log that is doing(Warn, done, problems etc... )
    public void addLog(String logLine, LogLevel level);
    
    //When checker finish call this method with a message
    public void onFinish(String finishMessage);
}
