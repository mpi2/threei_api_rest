package uk.ac.ebi.threei.rest.services;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

	
	@Autowired
	@Qualifier("solrClient")
	private HttpSolrClient solrClient;

    
   
    
    
    public String getMgiAccessionFromGeneSymbol(String geneSymbol) throws IOException, SolrServerException {

		if (geneSymbol == null) {
			System.err.println("null entered for gene symbol to get accession");
			return null;
		}

		SolrQuery query = new SolrQuery();
		query.setQuery(GeneDTO.MARKER_SYMBOL + ":" + geneSymbol+"  OR "+ GeneDTO.MARKER_SYNONYM+":"+geneSymbol);
		query.setRows(Integer.MAX_VALUE);
		query.setFields(GeneDTO.MARKER_SYMBOL, GeneDTO.MGI_ACCESSION_ID);
		
		QueryResponse rsp = solrClient.query(query);

		List<GeneDTO> genes = rsp.getBeans(GeneDTO.class);
		if(genes.size()>0 && genes.size()<2){
			return genes.get(0).getAccession();
		}else{
			System.err.println("too many or too few genes returned from 3i solr service");
			return "";
		}

	}







	public Map<String, GeneDTO> getGeneByKeywords(String keyword) throws SolrServerException, IOException {
		//all 3i data is from WTSI so filter genes on latest_phenotyping_centre:WTSI

		SolrQuery query = new SolrQuery();
		query.setQuery("auto_suggest"+":"+"\""+keyword+"\"");
		query.addFilterQuery("latest_phenotyping_centre" + ":" + "WTSI");//all 3i are WTSI phenotyping center??
		query.setRows(Integer.MAX_VALUE);
		
		QueryResponse rsp = solrClient.query(query);

		List<GeneDTO> genes = rsp.getBeans(GeneDTO.class);
		if(genes.size()>0){
			Map<String, GeneDTO> geneSymbolToGene=new HashMap<>();
			for(GeneDTO gene: genes) {
				geneSymbolToGene.put(gene.getMarkerSymbol(), gene);
			}
			
			return geneSymbolToGene;
		}else{
			System.err.println("too few genes returned from 3i solr service for keywords");
			return null;
		}
		
	}
	
	
	//geneQf use this field for gene, synonym and human gene info. Seems to work with lower and upper case mixes
	public List<GeneDTO> getGeneSymbolOrSynonymOrNameOrHuman(String keyword) throws SolrServerException, IOException {
		//all 3i data is from WTSI so filter genes on latest_phenotyping_centre:WTSI

		SolrQuery query = new SolrQuery();
		query.setQuery("geneQf"+":"+"\""+keyword+"\"");
		//query.addFilterQuery("latest_phenotyping_centre" + ":" + "WTSI");//all 3i are WTSI phenotyping center??
		query.setRows(Integer.MAX_VALUE);
		
		QueryResponse rsp = solrClient.query(query);

		List<GeneDTO> genes = rsp.getBeans(GeneDTO.class);
		if(genes.size()>0){	
			return genes;
		}else{
			System.err.println("too few genes returned from 3i gene solr service for keywords");
			return Collections.emptyList();
		}
		
	}
	 public String getGeneSymbolFromEnsemblId(String ensemblId) throws IOException, SolrServerException {

			if (ensemblId == null) {
				System.err.println("null entered for ensemblId to get gene symbol");
				return null;
			}

			SolrQuery query = new SolrQuery();
			query.setQuery(GeneDTO.ENSEMBL_ID + ":" + ensemblId);
			query.setRows(Integer.MAX_VALUE);
			query.setFields(GeneDTO.MARKER_SYMBOL);
			
			QueryResponse rsp = solrClient.query(query);

			List<GeneDTO> genes = rsp.getBeans(GeneDTO.class);
			if(genes.size()>0 && genes.size()<2){
				return genes.get(0).getMarkerSymbol();
			}else{
				System.err.println("too many or too few genes returned from 3i solr service");
				return "";
			}

		}
    
}
