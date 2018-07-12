package uk.ac.ebi.threei.rest.services;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.QueryResponse;

public class GeneDTO {

	
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";

	public static final String MARKER_SYMBOL = "marker_symbol";
	
	public static final String MARKER_NAME="marker_name";
	
	public static final String MARKER_SYNONYM= "marker_synonym";
	
	public static final String LATEST_PHENOTYPING_CENTER = "latest_phenotyping_center";
	
	@Field(MARKER_SYMBOL)
	private String markerSymbol;
	
	@Field(MARKER_NAME)
	private String markerName;
	
	@Field(MARKER_SYNONYM)
	private List<String> markerSynonym;
	
	@Field(MGI_ACCESSION_ID)
	private String accession;

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public List<String> getMarkerSynonym() {
		return markerSynonym;
	}

	public void setMarkerSynonym(List<String> markerSynonym) {
		this.markerSynonym = markerSynonym;
	}

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public static String getMgiAccessionId() {
		return MGI_ACCESSION_ID;
	}

	@Override
	public String toString() {
		return "GeneDTO [markerSymbol=" + markerSymbol + ", markerName=" + markerName + ", markerSynonym="
				+ markerSynonym + ", accession=" + accession + "]";
	}
	
}
