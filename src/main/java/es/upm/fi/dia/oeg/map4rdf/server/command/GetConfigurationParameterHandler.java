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

import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.SingletonResult;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;

/**
 * @author Filip
 */
public class GetConfigurationParameterHandler implements
		ActionHandler<GetConfigurationParameter, SingletonResult<String> > {
	
	private MultipleConfigurations configurations;
	
	@Override
	public Class<GetConfigurationParameter> getActionType() {
		return GetConfigurationParameter.class;
	}
	
	@Inject
	public GetConfigurationParameterHandler(MultipleConfigurations configurations) {
		this.configurations=configurations;
	}

	@Override
	public SingletonResult<String> execute(GetConfigurationParameter action,
			ExecutionContext context) throws ActionException {
		if(!configurations.existsConfiguration(action.getConfigID())){
			throw new ActionException("Bad config ID");
		}
		String result = configurations.getConfiguration(action.getConfigID()).getConfigurationParamValue(action.getName());		
		return new SingletonResult<String>(result);
	}

	@Override
	public void rollback(GetConfigurationParameter action,
			SingletonResult<String> result,
			ExecutionContext context) throws ActionException {
		
	}
}
