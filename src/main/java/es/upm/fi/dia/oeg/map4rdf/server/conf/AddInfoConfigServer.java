package es.upm.fi.dia.oeg.map4rdf.server.conf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import es.upm.fi.dia.oeg.map4rdf.share.conf.ParametersNamesAddInfo;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.AdditionalInfo;
import es.upm.fi.dia.oeg.map4rdf.share.conf.util.QueryParameterResult;

public class AddInfoConfigServer{
	public List<AdditionalInfo> additionalsInfo;
	private Logger logger = Logger.getLogger(AddInfoConfigServer.class);
	public AddInfoConfigServer(){
		additionalsInfo=new ArrayList<AdditionalInfo>();
	}
	public AddInfoConfigServer(GetServletContext servletContext, String addInfo) {
		additionalsInfo=new ArrayList<AdditionalInfo>();
		Properties properties = new Properties();
		String[] splitInfo = addInfo.split(";");
		for (int i = 0; i < splitInfo.length; i++) {
			try {
				String file = Constants.ADDITIONAL_INFO_CONFIG_FOLDER
						+ splitInfo[i];
				properties.load(servletContext.getServletContext()
						.getResourceAsStream(file));
				boolean correctInfo = true;
				AdditionalInfo info = AdditionalInfo.newInstance();
				if (properties.getProperty(ParametersNamesAddInfo.ENDPOINT_URL) != null
						&& !properties.getProperty(
								ParametersNamesAddInfo.ENDPOINT_URL).isEmpty()) {
					info.setEndpoint(properties
							.getProperty(ParametersNamesAddInfo.ENDPOINT_URL));
				} else {
					logger.error("Empty "
							+ ParametersNamesAddInfo.ENDPOINT_URL + " in "
							+ file + " file.");
					correctInfo = false;
				}
				if (properties.getProperty(ParametersNamesAddInfo.QUERY) != null
						&& !properties
								.getProperty(ParametersNamesAddInfo.QUERY)
								.isEmpty()) {
					info.setQuery(properties
							.getProperty(ParametersNamesAddInfo.QUERY));
				} else {
					logger.error("Empty " + ParametersNamesAddInfo.QUERY
							+ " in " + file + " file.");
					correctInfo = false;
				}
				if (properties
						.getProperty(ParametersNamesAddInfo.PARAMETERS_AND_LABELS) != null
						&& !properties.getProperty(ParametersNamesAddInfo.PARAMETERS_AND_LABELS).isEmpty()) {
					String[] splitParameters = properties.getProperty(
							ParametersNamesAddInfo.PARAMETERS_AND_LABELS).split("#");
					if(splitParameters.length<1){
						logger.error("Not parameters in "+ file);
						correctInfo=false;
					}
					for (int j = 0; j < splitParameters.length; j++) {
						String[] parameter = splitParameters[j].split(":");
						QueryParameterResult result=new QueryParameterResult("");
						if (splitParameters[j].split(":").length == 2) {
							result = new QueryParameterResult(parameter[0]);
							String[] splitLabels=parameter[1].split(";");
							if(splitLabels.length<1){
								logger.error("Not labels for parameter "+parameter[0]+" in "+file);
								correctInfo=false;
							}
							for(int k=0;k<splitLabels.length;k++){
								String[] complexLabel=splitLabels[k].split("@");
								if(complexLabel.length!=2){
									logger.error("Malformed label "+splitLabels[k]+" of parameter "
											+ parameter[0] +" in "+ file);
									correctInfo=false;
								}else{
									result.addLabel(complexLabel[1], complexLabel[0]);
								}
							}
							info.addQueryResult(result);
						} else {
							logger.error("Malformed parameter: "
									+ splitParameters[j] + " in " + file);
							correctInfo = false;
						}
					}
				} else {
					logger.error("Empty "
							+ ParametersNamesAddInfo.PARAMETERS_AND_LABELS
							+ " in " + file + " file.");
					correctInfo = false;
				}
				if (properties
						.getProperty(ParametersNamesAddInfo.INPUT_PARAMETERS) != null
						&& !properties.getProperty(ParametersNamesAddInfo.INPUT_PARAMETERS).isEmpty()) {
					info.setInputParameters(properties
							.getProperty(ParametersNamesAddInfo.INPUT_PARAMETERS));
				}else{
					logger.error("Empty parameter "+ParametersNamesAddInfo.INPUT_PARAMETERS+" in "
							+ file + " file." );
					correctInfo=false;
				}
				if (properties
						.getProperty(ParametersNamesAddInfo.HAS_IMAGES_LIMIT) != null
						&& !properties.getProperty(ParametersNamesAddInfo.HAS_IMAGES_LIMIT).isEmpty()){
					boolean hasImagesLimit=Boolean.parseBoolean(properties.getProperty(ParametersNamesAddInfo.HAS_IMAGES_LIMIT));
					boolean isCorrectImagesLimit=true;
					if(hasImagesLimit){
						if(properties
							.getProperty(ParametersNamesAddInfo.IMAGES_LIMIT) != null
							&& !properties.getProperty(ParametersNamesAddInfo.IMAGES_LIMIT).isEmpty()){
							String images_limit=properties.getProperty(ParametersNamesAddInfo.IMAGES_LIMIT);
							String[] splitImages_limit=images_limit.split(";");
							if(splitImages_limit.length==2 && !splitImages_limit[0].isEmpty() && !splitImages_limit[1].isEmpty()){
								try{
									info.setInferiorLimit(Double.parseDouble(splitImages_limit[0]));
									info.setSuperiorLimit(Double.parseDouble(splitImages_limit[1]));
								}catch (NumberFormatException e){
									logger.warn("Error to parse parameter "+ParametersNamesAddInfo.IMAGES_LIMIT+" in "+file+" file.");
									isCorrectImagesLimit=false;
								}
							}else{
								logger.warn("Malformed parameter "+ParametersNamesAddInfo.IMAGES_LIMIT+" in "
									+ file + " file." );
								isCorrectImagesLimit=false;
							}
						}else{
							logger.warn("Empty parameter "+ParametersNamesAddInfo.IMAGES_LIMIT+" in "
									+ file + " file." );
							isCorrectImagesLimit=false;
						}
						if(properties
								.getProperty(ParametersNamesAddInfo.IMAGES) != null
								&& !properties.getProperty(ParametersNamesAddInfo.IMAGES).isEmpty()){
							String[] splitImages=properties.getProperty(ParametersNamesAddInfo.IMAGES).split(";");
							if(splitImages.length==3 && !splitImages[0].isEmpty() && !splitImages[1].isEmpty() && !splitImages[2].isEmpty()){
								info.setImages(splitImages);
							}else{
								logger.warn("Malformed parameter "+ParametersNamesAddInfo.IMAGES+" in "
										+ file + " file." );
								isCorrectImagesLimit=false;
							}
						}else{
							logger.warn("Empty parameter "+ParametersNamesAddInfo.IMAGES+" in "
									+ file + " file." );
							isCorrectImagesLimit=false;
						}
						if(properties
								.getProperty(ParametersNamesAddInfo.IMAGES_LIMIT_PARAMETER) != null
								&& !properties.getProperty(ParametersNamesAddInfo.IMAGES_LIMIT_PARAMETER).isEmpty()){
							info.setImage_parameter(properties
								.getProperty(ParametersNamesAddInfo.IMAGES_LIMIT_PARAMETER));
						}else{
							logger.warn("Empty parameter "+ParametersNamesAddInfo.IMAGES_LIMIT_PARAMETER+" in "
									+ file + " file." );
							isCorrectImagesLimit=false;
						}
					}
					info.setHas_image_limit(isCorrectImagesLimit);
				}
				if (correctInfo) {
					additionalsInfo.add(info);
				} else {
					logger.error("Check " + file + " properties file.");
				}
			} catch (IOException e) {
				logger.error("Can't obtain the additional info file:"
						+ Constants.ADDITIONAL_INFO_CONFIG_FOLDER
						+ splitInfo[i],e);
			}
		}
	}
	public List<AdditionalInfo> getAdditionalsInfo() {
		return additionalsInfo;
	}
}
