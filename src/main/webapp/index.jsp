<%@page import="es.upm.fi.dia.oeg.map4rdf.server.conf.Configuration"%>
<%@page import="es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames"%>
<%
	String googleMapsKey="";
	if(request.getParameter("GoogleKey")==null){
		Configuration conf = (Configuration) pageContext.getServletContext().getAttribute(Configuration.class.getName());
		googleMapsKey = conf.getConfigurationParamValue(ParameterNames.GOOGLE_MAPS_API_KEY);
	}else{
		googleMapsKey = request.getParameter("GoogleKey");
	}
%>
<!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="gwt:property" content="locale=<%=request.getLocale()%>">
    <title>Map4RDF</title>
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.7.2.min.js" ></script>
    <script src="http://openlayers.org/api/2.13/OpenLayers.js"></script>
    <script src="OpenStreetMapsByFilip.js"></script>
    <script type="text/javascript" src="http://maps.googleapis.com/maps/api/js?gwt=1&amp;file=api&amp;v=3.6&amp;sensor=true&amp;key=<%=googleMapsKey%>" ></script>
    <script type="text/javascript" src="es.upm.fi.dia.oeg.map4rdf.map4rdf/es.upm.fi.dia.oeg.map4rdf.map4rdf.nocache.js"></script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      // Load the Visualization API.
      google.load('visualization', '1.0', {'packages':['corechart']});
    </script>
  </head>

  <body>    
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
  </body>
</html>