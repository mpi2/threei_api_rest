package uk.ac.ebi.threei.rest.controllers;

import java.io.IOException;
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
	List<CellHeatmapRow> cellRows;
	
	static final Map<String, String> cellHeaderToFieldMap;
	static {
		Map<String, String> aMap = new HashMap<>();
		aMap.put("γδ T cells", "alphaDeltaTCells");
		aMap.put("NK cells", "nKCells");
		aMap.put("NKT cells", "nktCells");
		aMap.put("B cell precursors", "bCellPrecursors");
		aMap.put("Dendritic cells", "dendriticCells");
		aMap.put("Granulocytes", "granulocytes");
		aMap.put("Treg cells", "tregCells");
		aMap.put("CD4 T cells", "cD4TCells");
		aMap.put("Monocytes / Macrophages", "monocytesMacrophages");
		aMap.put("Total αβ T cells", "totalAlphBetaTCells");
		aMap.put("B cells", "bCells");
		aMap.put("CD8 T cells", "cd8TCells");
		aMap.put("DSS Challenge", "dSSChallenge");
		aMap.put("Influenza", "influenza");
		aMap.put("Trichuris Challenge", "trichurisChallenge");
		aMap.put("Salmonella Challenge", "salmonellaChallenge");
		cellHeaderToFieldMap = Collections.unmodifiableMap(aMap);
	}
	
	SortedSet<String> constructSet=new TreeSet<>();//for filter menu dropdown only short version before (
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
		System.out.println("calling data controller with heatmapType" + keyword + " construct=" + construct);
		Data data = new Data();//obect that holds all the data for this chart display
		data.setHeatmapType("procedure");
		//should extract these into methods in a data service for unit testing purposes
		List<ProcedureHeatmapRow> procedureRows = procedureHeatmapRowsRepository.findAll();//get an easily readable form of the rows for the heatmap
		//loop through the rows and get the row headers for (gene symbols)
		ArrayList<String> rowHeaders=new ArrayList<>();
		ArrayList<String> constructs=new ArrayList<>();
		int rowI=0;
		for(ProcedureHeatmapRow row:procedureRows) {
			rowHeaders.add(row.getGene());
			constructs.add(row.getConstruct());
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
			//if(rowI>10) break;
		}
		
		ArrayList<String> procedureHeaders = new ArrayList<>(Arrays.asList(ProcedureHeatmapRowsRepository.procedureDisplayHeaderOrder));
//		ArrayList<String> constructAndProcedureHeaders=new ArrayList<>();
//		constructAndProcedureHeaders.add("construct");
//		constructAndProcedureHeaders.addAll(procedureHeaders);
		data.setColumnHeaders(procedureHeaders);
		data.setRowHeaders(rowHeaders);
		data.setConstructs(constructs);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}

	private void addCellData(Data data, int columnI, int rowI, int value) {
		List<Integer> cellData = new ArrayList<>();
		cellData.add(columnI);
		cellData.add(rowI);
		cellData.add(value);
		data.getData().add(cellData);
//		CellData cell=new CellData();
//		cell.setColumnI(columnI);
//		cell.setRowI(rowI);
//		cell.setValue(value);
//		celldata.getCellData().add(cell);
		//System.out.println("adding cell data="+cellData);
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
		Data data = getCellHeatmapData(filter);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}

	private Data getCellHeatmapData(Filter filter) {
		// System.out.println("filter="+filter);
		Data data = new Data();// obect that holds all the data for this chart display

		if (!StringUtils.isEmpty(filter.sortField)) {
			String sortField = cellHeaderToFieldMap.get(filter.sortField);
			System.out.println("sortVariable=" + sortField);
			Sort sort = new Sort(Sort.Direction.ASC, sortField);
			cellRows = cellHeatmapRowsRepository.findAll(sort);

		} else {
			// should extract these into methods in a data service for unit testing purposes
			cellRows = cellHeatmapRowsRepository.findAll();// get an easily readable form of the rows for the heatmap
		}

		// List<CellHeatmapRow> cellRows = cellHeatmapRowsRepository.findAll(sort);

		System.out.println("cellrows size=" + cellRows.size());
		
		//put filter method in here to only return the rows we need
		cellRows=this.filterCellRows(filter, cellRows);
		
		
		// loop through the rows and get the row headers for (gene symbols)
		ArrayList<String> rowHeaders = new ArrayList<>();
		ArrayList<String> constructs = new ArrayList<>();
		int rowI = 0;
		for (CellHeatmapRow row : cellRows) {

			rowHeaders.add(row.getGene());
			String constructString = row.getConstruct();
			constructs.add(constructString);
			if (constructString.contains("(")) {
				constructString = constructString.substring(0, row.getConstruct().indexOf("("));
			}
			if (!constructString.equals("")) {
				constructSet.add(constructString);
			}

			// we can get the data for the rows cells here as well and just access the
			// variables for the headers in the order they are specified - hard coded I
			// know- but we can use spring to do sorting and paging etc.
			// row.get

			// note we are not getting the construct here as this should be added to the
			// cells in the normal way as the first column
			// we can get the data for the rows cells here as well and just access the
			// variables for the headers in the order they are specified - hard coded I
			// know- but we can use spring to do sorting and paging etc.
			// "γδ T cells",
			// "NK cells",
			// "NKT cells",
			// "B cell precursors",
			// "Dendritic cells",
			// "Granulocytes",
			// "Treg cells",
			// "CD4 T cells",
			// "Monocytes / Macrophages",
			// "Total αβ T cells",
			// "B cells",
			// "CD8 T cells",
			// "DSS Challenge",
			// "Influenza",
			// "Trichuris Challenge",
			// "Salmonella Challenge"};

			// oh this is horrible as we need to make sure these are in the same order as
			// the column headers otherwise madness ensues
			int columnI = 0;
			addCellData(data, columnI, rowI, row.getAlphaDeltaTCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getnKCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getNktCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getbCellPrecursors());
			columnI++;
			addCellData(data, columnI, rowI, row.getDendriticCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getGranulocytes());
			columnI++;
			addCellData(data, columnI, rowI, row.getTregCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getcD4TCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getMonocytesMacrophages());
			columnI++;
			addCellData(data, columnI, rowI, row.getTotalAlphBetaTCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getbCells());
			columnI++;
			addCellData(data, columnI, rowI, row.getCd8TCells());
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
		ArrayList<String> cellHeaders = new ArrayList<>(
				Arrays.asList(CellHeatmapRowsRepository.cellDisplayHeaderOrder));
		data.setColumnHeaders(cellHeaders);
		data.setRowHeaders(rowHeaders);
		data.setConstructs(constructs);
		return data;
	}
	

	
	private List<CellHeatmapRow> filterCellRows(Filter filter, List<CellHeatmapRow> cellRows) {
		List<CellHeatmapRow> filteredRows=new ArrayList<>();
		for(CellHeatmapRow row: cellRows) {
			boolean addRow=true;
			if(filter.constructFilter!=null && !filter.constructFilter.equals("")) {
				if(!row.getConstruct().startsWith(filter.constructFilter)) {
					addRow=false;
				}
			}
			if(filter.cellTypeFilter!=null && !filter.cellTypeFilter.equals("")) {
				System.out.println("cellTypeFilter="+filter.cellTypeFilter);
				//need to check if the variable in camel case has a number of 4 to indicate it's significant for this row
				if(row.getVarabileFromKey(filter.cellTypeFilter)<3) {
					addRow=false;
				}
			}
			if(addRow) {
				filteredRows.add(row);
			}
		}
		return filteredRows;
	}

	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cellTypes")
	@ResponseBody
	public ResponseEntity<Types> cellTypes(Model model) {
		
		
		if(uniqueCellTypes==null) {
			uniqueCellTypes=new TreeSet<>();
		//should extract these into methods in a data service for unit testing purposes
		List<CellParameter> dataList = cellRepo.findAll();
		
		for(CellParameter cellP: dataList) {
			uniqueCellTypes.add(cellP.getCellType());
		}
		}
		Types types=new Types();
		types.getTypes().addAll(uniqueCellTypes);
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}
	
	
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/cellSubTypes")
	@ResponseBody
	public HttpEntity<Types> cellSubTypes(Model model) {
		

		if (uniqueSubCellTypes == null) {
			uniqueSubCellTypes = new TreeSet<>();
			// should extract these into methods in a data service for unit testing purposes
			List<CellParameter> dataList = cellRepo.findAll();

			for (CellParameter cellP : dataList) {
				uniqueSubCellTypes.add(cellP.getCellSubtype());
			}
		}
		Types types=new Types();
		types.getTypes().addAll(uniqueSubCellTypes);
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/assays")
	@ResponseBody
	public ResponseEntity<Types> assays(Model model) {
		
		if(uniqueAssays==null) {
			uniqueAssays=new TreeSet<String>();
		//should extract these into methods in a data service for unit testing purposes
		List<CellParameter> dataList = cellRepo.findAll();
		
		for(CellParameter cellP: dataList) {
			uniqueAssays.add(cellP.getAssay());
		}
		}
		Types types=new Types();
		types.getTypes().addAll(uniqueAssays);
		return new ResponseEntity<Types>(types, HttpStatus.OK);
	}
	
	@CrossOrigin(origins = "*", maxAge = 3600)
	@RequestMapping("/constructs")
	@ResponseBody
	public HttpEntity<Types> constructController(Model model, @RequestParam(value = "heatmapType", required = false, defaultValue="procedure") String heatmapType) {
		System.out.println("calling data controller with heatmapType"+ heatmapType);
		if(constructSet.isEmpty()) {
			this.getCellHeatmapData(new Filter());
		}
		List<String>uniqueConstructs=new ArrayList<>();
		uniqueConstructs.addAll(constructSet);
		Types types=new Types();
		types.getTypes().addAll(uniqueConstructs);
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
