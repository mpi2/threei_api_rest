package uk.ac.ebi.threei.rest.controllers;

import java.io.IOException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.ac.ebi.threei.rest.CellHeatmapRow;
import uk.ac.ebi.threei.rest.CellParameter;
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.DetailsRow;
import uk.ac.ebi.threei.rest.ProcedurePage;
import uk.ac.ebi.threei.rest.Types;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.repositories.CellParameterRepository;
import uk.ac.ebi.threei.rest.repositories.ParameterDetailsRepository;
import uk.ac.ebi.threei.rest.services.CellHeatmapService;
import uk.ac.ebi.threei.rest.services.GeneDTO;
import uk.ac.ebi.threei.rest.services.GeneService;
import uk.ac.ebi.threei.rest.services.ParameterDetailsService;
import uk.ac.ebi.threei.rest.services.ProcedureHeatmapService;

@RequestMapping(value = "/api")
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
			@RequestParam(value = "construct", required = false) String construct, @RequestParam(value = "procedure", required = false) String procedure) throws IOException, SolrServerException {
		System.out.println("calling get procedure page in controller");
		// this works http://localhost:8080/procedure_page?gene=Adal&procedure=Homozygous%20viability%20at%20P14&construct=tm1a
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
			Map<String, String> cellParametersToAssay=new HashMap<>();
			for(CellParameter cellP:cellParams) {
				//System.out.println("cell parameter="+cellP);
				cellParametersToAssay.put(cellP.getParameterName(), cellP.getAssay());
			}
			//then search for hits for these gene and filter on the parameters will be the quickest way rather than a request per parameter?
			List<ParameterDetails> parameterDetailsForGene = parameterDetailsServce.getParameterDetailsByGene(gene);
			//filter
			for(ParameterDetails detail:parameterDetailsForGene) {
				if(cellParametersToAssay.containsKey(detail.getParameterName())){
					//System.out.println("add detail");
					detail.setAssay(cellParametersToAssay.get(detail.getParameterName()));
					parameterDetails.add(detail);
				}
			}
		}
		
		//page.setParameterDetails(parameterDetails);//maybe we don't need these in the rest response but useful for debug at the moment
		SortedSet<String> headerKeys=getHeaderKeys(parameterDetails);//get unique column headers sorted alphabetically
		page.setGeneAccession(geneService.getMgiAccessionFromGeneSymbol(parameterDetails.get(0).getGene()));//should only be one gene per procedure/celltype page
		page.setColumnHeaders(new ArrayList<String>(headerKeys));
		
		List<DetailsRow> detailRows=new ArrayList<>();
		//generate a row for each parameter with blanks where no result for that header
		for(ParameterDetails p: parameterDetails) {
			//System.out.println("pDetails in controller="+p);
			//new row for each parameter with 0-3 added for each state like in heatmap
			DetailsRow row=new DetailsRow();
			row.setRowHeader(p.getParameterName());
			row.setParameterStableId(p.getParameterId());
			row.setAssay(p.getAssay());
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
	public HttpEntity<Data> procedureHeatmap(Model model, @RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "construct", required = false) String constructFilter, @RequestParam(value = "sort", required = false) String sortField) {
		
		Filter filter=new Filter();
		filter.setKeyword(keyword);
		filter.setConstructFilter(constructFilter);
		filter.setSortField(sortField);
		return procedureHeatmapservice.getProcedureHeatmapData(filter);
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
		Types types = cellHeatmapService.getConstructs();
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}
	
	
	

}
