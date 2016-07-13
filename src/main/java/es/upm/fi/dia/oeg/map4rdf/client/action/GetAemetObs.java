package es.upm.fi.dia.oeg.map4rdf.client.action;


import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.aemet.AemetObs;

public class GetAemetObs extends MultipleConfigurationAction implements Action<ListResult<AemetObs>>{

		private static final long serialVersionUID = 1182571218686724089L;
		
		private String uri;

		private GetAemetObs() {
			// for serilization
			super("");
		}

		public GetAemetObs(String configID,String uri) {
			super(configID);
			this.uri = uri;
		}

		/**
		 * @return the uri
		 */
		public String getUri() {
			return uri;
		}

}
