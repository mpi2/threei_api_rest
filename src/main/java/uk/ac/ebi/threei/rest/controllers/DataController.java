package uk.ac.ebi.threei.rest.controllers;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.util.Hash;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import uk.ac.ebi.threei.rest.CellHeatmapRow;
import uk.ac.ebi.threei.rest.CellParameter;
import uk.ac.ebi.threei.rest.Types;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.DetailsRow;
import uk.ac.ebi.threei.rest.ProcedureHeatmapRow;
import uk.ac.ebi.threei.rest.ProcedurePage;
import uk.ac.ebi.threei.rest.repositories.CellHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.CellParameterRepository;
import uk.ac.ebi.threei.rest.repositories.ParameterDetailsRepository;
import uk.ac.ebi.threei.rest.repositories.ProcedureHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.services.CellHeatmapService;
import uk.ac.ebi.threei.rest.services.GeneDTO;
import uk.ac.ebi.threei.rest.services.GeneService;
import uk.ac.ebi.threei.rest.services.ParameterDetailsService;
import uk.ac.ebi.threei.rest.services.ProcedureHeatmapService;
@RestController
public class DataController {
	
	@Autowired
	GeneService geneService;
	@Autowired
	ParameterDetailsRepository dataRepo;
//	@Autowired
//	CellParameterRepository cellRepo;
	
	@Autowired
	CellHeatmapService cellHeatmapService;
	@Autowired
	ProcedureHeatmapService procedureHeatmapservice;
	@Autowired
	CellParameterRepository cellParameterRepository;
	
	
	
	
	
	
	List<CellHeatmapRow> cellRows;
	@Autowired
	private ParameterDetailsService parameterDetailsServce;
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/procedure_page")
	@ResponseBody
	public HttpEntity<ProcedurePage> getProcedurePage(Model model, @RequestParam(value = "gene", required = false) String gene,
			@RequestParam(value = "construct", required = false) String construct, @RequestParam(value = "procedure", required = false) String procedure) {
		System.out.println("calling get procedure page in controller");
		// this works http://localhost:8080/procedure_page?gene=Adal&procedure=Homozygous%20viability%20at%20P14&construct=tm1a
		//but why don't we have a result with Homozygous%20Fertility in our results now????
		ProcedurePage page=new ProcedurePage();
		//spring data didn't like spaces in procedure names so doing it old school once filtered on gene
		List<ParameterDetails> parameterDetails = parameterDetailsServce.getParameterDetailsByGeneAndProcedureAndConstruct(gene, procedure , construct);
		//if nothing then we try the cell type to parameter approach as could come from cell heatmap link
		if(parameterDetails.size()==0) {
			//new method here to convert cell type to parameters using the mapping we already have
			//http://localhost:8080/cell query by cell to get the parameters for this cell type
			//CellParameterRepository add method there.
			List<CellParameter> cellParams = cellParameterRepository.findByCellType(procedure);
			//search these for the list of parameters
			List<String> cellParameters=new ArrayList<>();
			for(CellParameter cellP:cellParams) {
				cellParameters.add(cellP.getParameterName());
			}
			//then search for hits for these gene and filter on the parameters will be the quickest way rather than a request per parameter?
			List<ParameterDetails> parameterDetailsForGene = parameterDetailsServce.getParameterDetailsByGene(gene);
			//filter
			for(ParameterDetails detail:parameterDetailsForGene) {
				if(cellParameters.contains(detail.getParameterName())){
					System.out.println("add detail");
					parameterDetails.add(detail);
				}
			}
		}
		
		//page.setParameterDetails(parameterDetails);//maybe we don't need these in the rest response but useful for debug at the moment
		SortedSet<String> headerKeys=getHeaderKeys(parameterDetails);//get unique column headers sorted alphabetically
		page.setColumnHeaders(new ArrayList<String>(headerKeys));
		
		List<DetailsRow> detailRows=new ArrayList<>();
		//generate a row for each parameter with blanks where no result for that header
		for(ParameterDetails p: parameterDetails) {
			//System.out.println("pDetails in controller="+p);
			//new row for each parameter with 0-3 added for each state like in heatmap
			DetailsRow row=new DetailsRow();
			row.setRowHeader(p.getParameterName());
			//loop over the header strings and get the significance score from each
			for(String header:headerKeys) {
				//System.out.println("header is "+header);
				int sig=0;//no data bye default
				if(this.getHeaderString(p).equals(header)){
					sig = p.getSignificanceValue();
					
				}
				row.addSignificance(sig);
			}
			detailRows.add(row);
		}
		page.setRows(detailRows);
		return new ResponseEntity<ProcedurePage>(page, HttpStatus.OK);
	
	}


	private SortedSet<String> getHeaderKeys(List<ParameterDetails> parameterDetails) {
		SortedSet<String> headerKeys=new TreeSet<>();//get unique set of headers
		for(ParameterDetails p: parameterDetails) {
			String header = getHeaderString(p);
			headerKeys.add(header);	
		}
		return headerKeys;
	}


	private String getHeaderString(ParameterDetails p) {
		String sex=p.getSex();
		if(sex.equalsIgnoreCase("both")) {
			sex="Male/Female";
		}
		String header=sex+" "+p.getGenotype();
		return header;
	}
	
	
	/**
	 * 
	 * @param model
	 * @param keyword
	 * @param construct
	 * @return
	 */
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/procedure_heatmap")
	@ResponseBody
	public HttpEntity<Data> procedureHeatmap(Model model, @RequestParam(value = "keywords", required = false) String keyword,
			@RequestParam(value = "construct", required = false) String construct) {
		return procedureHeatmapservice.getProcedureHeatmapData(keyword, construct);
	}

	
	
	
	
	/**
	 * 
	 * @param model
	 * @param keyword
	 * @param construct
	 * @return
	 */
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cell_heatmap")
	@ResponseBody
	public HttpEntity<Data> cellHeatmap(Model model, @RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "construct", required = false) String constructFilter, @RequestParam(value = "cellType", required = false) String cellTypeFilter, @RequestParam(value = "cellSubType", required = false) String cellSubTypeFilter, @RequestParam(value = "assay", required = false) String assayFilter, @RequestParam(value = "sort", required = false) String sortField) {
		System.out.println("sortField="+sortField);
		Filter filter=new Filter();
		filter.setKeyword(keyword);
		filter.setConstructFilter(constructFilter);
		filter.setCellTypeFilter(cellTypeFilter);
		filter.setCellSubTypeFilter(cellSubTypeFilter);
		filter.setAssayFilter(assayFilter);
		filter.setSortField(sortField);
		Data data = cellHeatmapService.getCellHeatmapData(filter);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}

	

	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cellTypes")
	@ResponseBody
	public ResponseEntity<Types> cellTypes(Model model) {
		
		
		Types types=cellHeatmapService.cellTypes();
		
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}
	
	
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cellSubTypes")
	@ResponseBody
	public HttpEntity<Types> cellSubTypes(Model model) {
		

		Types types = cellHeatmapService.cellSubTypes();
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/assays")
	@ResponseBody
	public ResponseEntity<Types> assays(Model model) {
		
		Types types = cellHeatmapService.getAssays();
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}

	
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/constructs")
	@ResponseBody
	public HttpEntity<Types> constructController(Model model, @RequestParam(value = "heatmapType", required = false, defaultValue="procedure") String heatmapType) {
		System.out.println("calling data controller with heatmapType"+ heatmapType);
		Types types = cellHeatmapService.getConstructs();
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}

	
	
	
	
//	@ExceptionHandler()
//    public void handleException() {
//        System.out.println("exception thown in DataController");
//    }
	
	
	private Map<String, GeneDTO> getGenesForKeywords(String keyword, Map<String, GeneDTO> genes) {
		System.out.println("keyword is "+keyword);
    	//now use our gene autosuggest field to get the appropriate gene back
    	//auto_suggest:Adal
    	//http://localhost:8080/data?keyword=4930578F03Rik returns Adal - also need to handle spaces with quotes....!!!
      try {
			genes=geneService.getGeneByKeywords(keyword);
			//use the symbol from the gene returned to request the page with the gene
			//maybe use a redirect?
			
			System.out.println(genes);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return genes;
	}
	
	
	

}
