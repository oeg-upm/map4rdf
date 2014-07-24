/**
 * Copyright (c) 2011 Ontology Engineering Group, 
 * Departamento de Inteligencia Artificial,
 * Facultad de Informetica, Universidad 
 * Politecnica de Madrid, Spain
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package es.upm.fi.dia.oeg.map4rdf.server.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;
import es.upm.fi.dia.oeg.map4rdf.client.action.SaveRdfFile;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;

/**
 * @author Filip
 */
public class SaveRdfFileHandler implements
		ActionHandler<SaveRdfFile, SingletonResult<String> > {

	private MultipleConfigurations configurations;
	private Logger logger = Logger.getLogger(SaveRdfFileHandler.class);
	@Override
	public Class<SaveRdfFile> getActionType() {
		return SaveRdfFile.class;
	}
	
	@Inject
	public SaveRdfFileHandler(MultipleConfigurations configurations) {
		this.configurations = configurations;
	}

	@Override
	public SingletonResult<String> execute(SaveRdfFile action,
			ExecutionContext context) throws ActionException {
		if(!configurations.existsConfiguration(action.getConfigID())){
			throw new ActionException("Bad Config ID");
		}
		String path = configurations.getConfiguration(action.getConfigID())
				.getConfigurationParamValue(ParameterNames.RDF_STORE_PATH);
		if(!path.endsWith("/")){
			path = path+"/";
		}
		File file = new File(path +action.getConfigID()+"/"+ action.getFileName());
    	if (file.exists()) {
    		logger.error("When save Edited rdf file, the file exists.");
    		return new SingletonResult<String>("The file exists");
    	}else{
    		try {
    			File dirs= new File(path);
    			dirs.mkdirs();
				if(!file.createNewFile()){
				}
			} catch (IOException e) {
				logger.error("Can not save edited rdf file: ",e);
				return new SingletonResult<String>("Cant create the file");
			}
    	}	
    	
    	BufferedWriter output;
    	
		try {
			output = new BufferedWriter(new FileWriter(file));
	    	output.write(action.getFileContent());
	    	output.close();
	    	
		} catch (IOException e) {
			logger.error("Can not save edited rdf file. Buffered ERROR: ",e);
			return new SingletonResult<String>("Buffered ERROR");
		}
		
		return new SingletonResult<String>("");
	}

	@Override
	public void rollback(SaveRdfFile action,
			SingletonResult<String> result,
			ExecutionContext context) throws ActionException {
		
	}
}
