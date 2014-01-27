package es.upm.fi.dia.oeg.map4rdf.client.action;


import net.customware.gwt.dispatch.shared.Action;
import es.upm.fi.dia.oeg.map4rdf.share.AemetObs;

public class GetAemetObs implements Action<ListResult<AemetObs>>{

		private String uri;

		GetAemetObs() {
			// for serilization
		}

		public GetAemetObs(String uri) {
			this.uri = uri;
		}

		/**
		 * @return the uri
		 */
		public String getUri() {
			return uri;
		}

}
