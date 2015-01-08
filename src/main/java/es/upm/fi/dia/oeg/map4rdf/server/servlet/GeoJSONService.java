package es.upm.fi.dia.oeg.map4rdf.server.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import es.upm.fi.dia.oeg.map4rdf.client.util.ConfigurationUtil;
import es.upm.fi.dia.oeg.map4rdf.server.conf.multiple.MultipleConfigurations;
import es.upm.fi.dia.oeg.map4rdf.server.dao.DaoException;
import es.upm.fi.dia.oeg.map4rdf.share.FacetConstraint;
import es.upm.fi.dia.oeg.map4rdf.share.GeoResource;

public class GeoJSONService extends HttpServlet {

	private static final long serialVersionUID = 4940408910832985953L;
	private MultipleConfigurations configurations;
	private static final String [] reservedParameters={ConfigurationUtil.CONFIGURATION_ID};

	@Inject
	public GeoJSONService(MultipleConfigurations configurations) {
		this.configurations = configurations;
		;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Set<FacetConstraint> constraints = getFacetConstraints(req);
		String configID = getConfigurationID(req);
		try {
			List<GeoResource> resources = configurations.getConfiguration(configID)
					.getMap4rdfDao().getGeoResources(null, constraints);
			resp.setContentType("application/json");
			writeGeoJSON(resources, resp.getOutputStream());
		}catch(DaoException daoException){
			throw new ServletException(daoException);
		}
	}

	private void writeGeoJSON(List<GeoResource> resources,
			ServletOutputStream outputStream) {
		// TODO Auto-generated method stub
		
	}

	private Set<FacetConstraint> getFacetConstraints(HttpServletRequest req) {
		Set<FacetConstraint> constraints = new HashSet<FacetConstraint>();
		Enumeration<String> paramNames = (Enumeration<String>) req
				.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String facetId = paramNames.nextElement();
			boolean isReservedParam = false;
			for (String toTest : reservedParameters) {
				if (toTest.toLowerCase().trim()
						.equals(facetId.toLowerCase().trim())) {
					isReservedParam = true;
					break;
				}
			}
			if (!isReservedParam) {
				String[] valueIds = req.getParameterValues(facetId);
				for (String valueId : valueIds) {
					constraints.add(new FacetConstraint(facetId, valueId));
				}
			}
		}
		return constraints;
	}

	private String getConfigurationID(HttpServletRequest req)
			throws ServletException {
		String value = req.getParameter(ConfigurationUtil.CONFIGURATION_ID);
		if (value == null || value.isEmpty()) {
			throw new ServletException("Bad parameter value of parameter key: "
					+ ConfigurationUtil.CONFIGURATION_ID);
		}
		return value;
	}
}
