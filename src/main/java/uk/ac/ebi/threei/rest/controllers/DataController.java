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
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.ProcedureHeatmapRow;
import uk.ac.ebi.threei.rest.repositories.CellHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.CellParameterRepository;
import uk.ac.ebi.threei.rest.repositories.DataRepository;
import uk.ac.ebi.threei.rest.repositories.ProcedureHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.services.CellHeatmapService;
import uk.ac.ebi.threei.rest.services.GeneDTO;
import uk.ac.ebi.threei.rest.services.GeneService;
import uk.ac.ebi.threei.rest.services.ProcedureHeatmapService;
@RestController
public class DataController {
	
	@Autowired
	GeneService geneService;
	@Autowired
	DataRepository dataRepo;
//	@Autowired
//	CellParameterRepository cellRepo;
	
	@Autowired
	CellHeatmapService cellHeatmapService;
	@Autowired
	ProcedureHeatmapService procedureHeatmapservice;
	
	
	
	
	
	
	List<CellHeatmapRow> cellRows;
	
	
	
	
	/**
	 * Gets the data for the heatmap display - old method will eventually be deleted after testing new ones.
	 * @param model
	 * @param heatmapType
	 * @param keyword
	 * @param construct
	 * @return
	 */
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/data")
	@ResponseBody
	public HttpEntity<Data> dataController(Model model, @RequestParam(value = "heatmapType", required = false, defaultValue="procedure") String heatmapType, @RequestParam(value = "keywords", required = false) String keyword,
			@RequestParam(value = "construct", required = false) String construct) {
		System.out.println("calling old cell data method with "+ heatmapType+" keywords=" + keyword + " construct=" + construct);
		
		//should extract these into methods in a data service for unit testing purposes
		List<Data> dataList = dataRepo.findAll();
		Data data = new Data();
		if(heatmapType.equals("cell")){
			data=dataList.get(1);
		}else if(heatmapType.equals("procedure")){
			data=dataList.get(0);
		}
		return new ResponseEntity<Data>(data, HttpStatus.OK);
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
	public HttpEntity<Data> cellHeatmap(Model model, @RequestParam(value = "keywords", required = false) String keyword,
			@RequestParam(value = "construct", required = false) String constructFilter, @RequestParam(value = "cellType", required = false) String cellTypeFilter, @RequestParam(value = "cellSubType", required = false) String cellSubTypeFilter, @RequestParam(value = "assay", required = false) String assayFilter, @RequestParam(value = "sort", required = false) String sortField) {
		System.out.println("sortField="+sortField);
		Filter filter=new Filter();
		filter.keyword=keyword;
		filter.constructFilter=constructFilter;
		filter.cellTypeFilter=cellTypeFilter;
		filter.cellSubTypeFilter=cellSubTypeFilter;
		filter.assayFilter=assayFilter;
		filter.sortField=sortField;
		Data data = cellHeatmapService.getCellHeatmapData(filter);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}

	
	

	
	
	
	
	
	public Object get(String methodName, CellHeatmapRow a) throws Exception {
	    Object ret = null;
	    if(a != null){
	        Class<CellHeatmapRow> cl = CellHeatmapRow.class;
	        Method method = cl.getDeclaredMethod(methodName);
	        ret = method.invoke(a);
	    }
	    return ret;
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
