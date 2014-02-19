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
package es.upm.fi.dia.oeg.map4rdf.server.inject;

import net.customware.gwt.dispatch.server.guice.ActionHandlerModule;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAddInfoConfig;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObs;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetAemetObsForProperty;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetBufferGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetConfigurationParameter;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetFacetDefinitions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourceOverlays;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResources;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetGeoResourcesAsKmlUrl;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMapsConfiguration;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetMultipleConfigurationParameters;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetRoutePoints;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticDatasets;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetStatisticYears;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectDescriptions;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetSubjectLabel;
import es.upm.fi.dia.oeg.map4rdf.client.action.GetWebNMasUnoResource;
import es.upm.fi.dia.oeg.map4rdf.client.action.SaveRdfFile;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetAemetObsForPropertyHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetAemetObsHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetFacetDefinitionsHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetGeoResourceHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetGeoResourceOverlaysHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetBufferGeoResourcesHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetAddInfoConfigHandler;
//import es.upm.fi.dia.oeg.map4rdf.server.command.GetGeoResourcesAsKmlUrlHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetConfigurationParameterHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetGeoResourcesAsKmlUrlHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetGeoResourcesHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetMapsConfigurationHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetMultipleConfigurationParametersHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetRoutePointsHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetStatisticDatasetsHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetStatisticYearsHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetSubjectDescriptionsHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetSubjectLabelHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.GetWebNMasUnoResourceHandler;
import es.upm.fi.dia.oeg.map4rdf.server.command.SaveRdfFIleHandler;
/**
 * @author Alexander De Leon
 */
public class BrowserActionHandlerModule extends ActionHandlerModule {

	@Override
	protected void configureHandlers() {
		super.bindHandler(GetGeoResources.class,GetGeoResourcesHandler.class);
		super.bindHandler(GetFacetDefinitions.class,GetFacetDefinitionsHandler.class);
        super.bindHandler(GetGeoResourceOverlays.class,GetGeoResourceOverlaysHandler.class);
		super.bindHandler(GetStatisticYears.class,GetStatisticYearsHandler.class);
		super.bindHandler(GetStatisticDatasets.class,GetStatisticDatasetsHandler.class);
		super.bindHandler(GetGeoResource.class, GetGeoResourceHandler.class);
		super.bindHandler(GetSubjectDescriptions.class, GetSubjectDescriptionsHandler.class);
		super.bindHandler(GetSubjectLabel.class,GetSubjectLabelHandler.class);
		super.bindHandler(GetGeoResourcesAsKmlUrl.class,GetGeoResourcesAsKmlUrlHandler.class);
		super.bindHandler(SaveRdfFile.class,SaveRdfFIleHandler.class);
		super.bindHandler(GetConfigurationParameter.class,GetConfigurationParameterHandler.class);
		super.bindHandler(GetRoutePoints.class,GetRoutePointsHandler.class);
		super.bindHandler(GetBufferGeoResources.class,GetBufferGeoResourcesHandler.class);
		super.bindHandler(GetAddInfoConfig.class, GetAddInfoConfigHandler.class);
		super.bindHandler(GetMultipleConfigurationParameters.class, GetMultipleConfigurationParametersHandler.class);
		super.bindHandler(GetMapsConfiguration.class, GetMapsConfigurationHandler.class);
		super.bindHandler(GetAemetObsForProperty.class, GetAemetObsForPropertyHandler.class);
		super.bindHandler(GetAemetObs.class, GetAemetObsHandler.class);
		super.bindHandler(GetWebNMasUnoResource.class, GetWebNMasUnoResourceHandler.class);
	}
}
