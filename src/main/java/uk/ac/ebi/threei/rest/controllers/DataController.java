package uk.ac.ebi.threei.rest.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
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
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.ProcedureHeatmapRow;
import uk.ac.ebi.threei.rest.UniqueSubCellTypes;
import uk.ac.ebi.threei.rest.repositories.CellHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.CellParameterRepository;
import uk.ac.ebi.threei.rest.repositories.DataRepository;
import uk.ac.ebi.threei.rest.repositories.ProcedureHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.services.GeneDTO;
import uk.ac.ebi.threei.rest.services.GeneService;
@RestController
public class DataController {
	
	@Autowired
	GeneService geneService;
	@Autowired
	DataRepository dataRepo;
	@Autowired
	CellParameterRepository cellRepo;
	
	@Autowired
	ProcedureHeatmapRowsRepository procedureHeatmapRowsRepository;
	@Autowired
	CellHeatmapRowsRepository cellHeatmapRowsRepository;
	
	private SortedSet<String>uniqueSubCellTypes;
	private SortedSet<String> uniqueAssays;
	private SortedSet<String> uniqueCellTypes;
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
		System.out.println("calling data controller with heatmapType"+ heatmapType+" keywords=" + keyword + " construct=" + construct);
		
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
		System.out.println("calling data controller with heatmapType" + keyword + " construct=" + construct);
		Data data = new Data();//obect that holds all the data for this chart display
		data.setHeatmapType("procedure");
		//should extract these into methods in a data service for unit testing purposes
		List<ProcedureHeatmapRow> procedureRows = procedureHeatmapRowsRepository.findAll();//get an easily readable form of the rows for the heatmap
		//loop through the rows and get the row headers for (gene symbols)
		ArrayList<String> rowHeaders=new ArrayList<>();
		int rowI=0;
		for(ProcedureHeatmapRow row:procedureRows) {
			rowHeaders.add(row.getGene());
			//note we are not getting the construct here as this should be added to the cells in the normal way as the first column
			//we can get the data for the rows cells here as well and just access the variables for the headers in the order they are specified - hard coded I know- but we can use spring to do sorting and paging etc.
			
			int columnI=0;
			addCellData(data, columnI, rowI, row.getHomozygousViabilityAtP14());
			columnI++;
			addCellData(data, columnI, rowI, row.getHomozygousFertility());
			columnI++;
			addCellData(data, columnI, rowI, row.getHaematology());
			columnI++;
			addCellData(data, columnI, rowI, row.getPeripheralBloodLeukocytes());
			columnI++;
			addCellData(data, columnI, rowI, row.getSpleen());
			columnI++;
			addCellData(data, columnI, rowI, row.getMesentericLymphNode());
			columnI++;
			addCellData(data, columnI, rowI, row.getBoneMarrow());
			columnI++;
			addCellData(data, columnI, rowI, row.getEarEpidermis());
			columnI++;
			addCellData(data, columnI, rowI, row.getAntinuclearAntibodies());
			columnI++;
			addCellData(data, columnI, rowI, row.getCytotoxicTCellFunction());
			columnI++;
			addCellData(data, columnI, rowI, row.getdSSChallenge());
			columnI++;
			addCellData(data, columnI, rowI, row.getInfluenza());
			columnI++;
			addCellData(data, columnI, rowI, row.getTrichurisChallenge());
			columnI++;
			addCellData(data, columnI, rowI, row.getSalmonellaChallenge());
			columnI++;
			
			rowI++;
		}
		
		ArrayList<String> procedureHeaders = new ArrayList<>(Arrays.asList(ProcedureHeatmapRowsRepository.procedureDisplayHeaderOrder));
		data.setColumnHeaders(procedureHeaders);
		data.setRowHeaders(rowHeaders);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}

	private void addCellData(Data data, int columnI, int rowI, int value) {
		List<Integer> cellData = new ArrayList<>();
		cellData.add(columnI);
		cellData.add(rowI);
		cellData.add(value);
		data.getData().add(cellData);
		System.out.println("adding cell data="+cellData);
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
			@RequestParam(value = "construct", required = false) String construct) {
System.out.println("calling cell heatmap controller with " + keyword + " construct=" + construct);
		
		//should extract these into methods in a data service for unit testing purposes
		List<CellHeatmapRow> cellRows = cellHeatmapRowsRepository.findAll();//get an easily readable form of the rows for the heatmap
		//loop through the rows and get the row headers for (gene symbols)
		ArrayList<String> rowHeaders=new ArrayList<>();
		for(CellHeatmapRow row:cellRows) {
			rowHeaders.add(row.getGene());
			//note we are not getting the construct here as this should be added to the cells in the normal way as the first column
			
			//we can get the data for the rows cells here as well and just access the variables for the headers in the order they are specified - hard coded I know- but we can use spring to do sorting and paging etc.
			//row.get
		}
		Data data = new Data();
		ArrayList<String> cellHeaders = new ArrayList<>(Arrays.asList(CellHeatmapRowsRepository.cellDisplayHeaderOrder));
		data.setColumnHeaders(cellHeaders);
		data.setRowHeaders(rowHeaders);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}
	

	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cellTypes")
	@ResponseBody
	public HttpEntity<Set<String>> cellTypes(Model model) {
		System.out.println("calling get cell subtypes");
		
		if(uniqueCellTypes==null) {
			uniqueCellTypes=new TreeSet<>();
		//should extract these into methods in a data service for unit testing purposes
		List<CellParameter> dataList = cellRepo.findAll();
		
		for(CellParameter cellP: dataList) {
			uniqueCellTypes.add(cellP.getCellType());
		}
		}
		return new ResponseEntity<Set<String>>(uniqueCellTypes, HttpStatus.OK);
	}
	
	
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cellSubTypes")
	@ResponseBody
	public HttpEntity<UniqueSubCellTypes> cellSubTypes(Model model) {
		System.out.println("calling get cell subtypes");

		if (uniqueSubCellTypes == null) {
			uniqueSubCellTypes = new TreeSet<>();
			// should extract these into methods in a data service for unit testing purposes
			List<CellParameter> dataList = cellRepo.findAll();

			for (CellParameter cellP : dataList) {
				uniqueSubCellTypes.add(cellP.getCellSubtype());
			}
		}
		UniqueSubCellTypes types=new UniqueSubCellTypes();
		types.getTypes().addAll(uniqueSubCellTypes);
		return new ResponseEntity<UniqueSubCellTypes>(types, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/assays")
	@ResponseBody
	public HttpEntity<Set<String>> assays(Model model) {
		System.out.println("calling get cell subtypes");
		
		if(uniqueAssays==null) {
			uniqueAssays=new TreeSet<String>();
		//should extract these into methods in a data service for unit testing purposes
		List<CellParameter> dataList = cellRepo.findAll();
		
		for(CellParameter cellP: dataList) {
			uniqueAssays.add(cellP.getAssay());
		}
		}
		return new ResponseEntity<Set<String>>(uniqueSubCellTypes, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/constructs")
	@ResponseBody
	public HttpEntity<List<String>> constructController(Model model, @RequestParam(value = "heatmapType", required = false, defaultValue="procedure") String heatmapType) {
		System.out.println("calling data controller with heatmapType"+ heatmapType);
		List<String>uniqueConstructs=new ArrayList<>();
		//should extract these into methods in a data service for unit testing purposes
		List<Data> dataList = dataRepo.findAll();
		Data data = new Data();
		if(heatmapType.equals("cell")){
			data=dataList.get(1);
			
		}else if(heatmapType.equals("procedure")){
			data=dataList.get(0);
		}
		return new ResponseEntity<List<String>>(uniqueConstructs, HttpStatus.OK);
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
