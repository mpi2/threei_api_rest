package uk.ac.ebi.threei.rest.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.stereotype.Service;

import uk.ac.ebi.threei.rest.CellHeatmapRow;
import uk.ac.ebi.threei.rest.CellParameter;
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.Types;
import uk.ac.ebi.threei.rest.controllers.Filter;
import uk.ac.ebi.threei.rest.repositories.CellHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.CellParameterRepository;

@Service
public class CellHeatmapService {

	@Autowired
	CellHeatmapRowsRepository cellHeatmapRowsRepository;
	@Autowired
	CellParameterRepository cellRepo;
	@Autowired
	GeneService geneService;
	private SortedSet<String> uniqueCellTypes;
	private SortedSet<String> uniqueAssays;
	private List<CellHeatmapRow> cellRows;
	SortedSet<String> constructSet=new TreeSet<>();//for filter menu dropdown only short version before (
	SortedSet<String> uniqueCellSubTypes;
	Map<String, Set<String>> cellSubTypesToCellTypes=new HashMap<>();
	Map<String, Set<String>> assayToCellTypes=new HashMap<>();
	
	
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
	
	
	@PostConstruct
	public void init() {
		//inititialise the data with all data and create structures for menus etc
		this.getCellHeatmapData(new Filter());
		this.getConstructs();
		this.getAssays();
		this.cellTypes();
		this.cellSubTypes();
		this.initialiseCellSubTypesAndAssayToTypes();

		
	}
	
	public Data getCellHeatmapData(Filter filter) {
		// System.out.println("filter="+filter);
		Data data = new Data();// obect that holds all the data for this chart display
		Sort sort = null;
//		if (!StringUtils.isEmpty(filter.getSortField())) {
//			String sortField = cellHeaderToFieldMap.get(filter.getSortField());
//			System.out.println("sortVariable=" + sortField);
//			sort = new Sort(Sort.Direction.ASC, sortField);
//		}
		sort=new Sort(Sort.Direction.ASC, "totalSignificanceScore");
		//put filter method in here to only return the rows we need
		cellRows = this.filterCellRows(filter, sort);
		System.out.println("cellrows size=" + cellRows.size());

		//by default we need to sort the rows by ones with most significant hits at the top
//		cellRows.sort(new Comparator<CellHeatmapRow>() {
//		    @Override
//		    public int compare(CellHeatmapRow r1, CellHeatmapRow r2) {
//		        if(r1.getTotalSignificanceScore() == r2.getTotalSignificanceScore()){
//		            return 0;
//		        }
//		        return r1.getTotalSignificanceScore()< r2.getTotalSignificanceScore() ? -1 : 1;
//		     }
//		});
		
		// loop through the rows and get the row headers for (gene symbols)
		ArrayList<String> rowHeaders = new ArrayList<>();
		ArrayList<String> constructs = new ArrayList<>();
		int rowI = 0;
		for (CellHeatmapRow row : cellRows) {
//System.out.println("row="+row);
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
	
	private void addCellData(Data data, int columnI, int rowI, Integer value) {
		CommonMethods.addCellData(data, columnI, rowI, value);
		
	}

	public List<CellHeatmapRow> filterCellRows(Filter filter, Sort sort) {

		System.out.println("filter in filter function="+filter);
		CellHeatmapRow exampleRow = new CellHeatmapRow();
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
//				if(filter.getKeyword()!=null) {
//					exampleMatcher.withMatcher("gene", GenericPropertyMatcher::ignoreCase);
//					exampleRow.setGene(filter.getKeyword());
//				}
				if(filter.getConstructFilter()!=null) {
					exampleMatcher.withMatcher("construct", GenericPropertyMatcher::startsWith);
					exampleRow.setConstruct(filter.getConstructFilter());
				}
	            if(filter.getCellTypeFilter()!=null) {
	            	exampleMatcher.withMatcher(filter.getCellTypeFilter(), GenericPropertyMatcher::ignoreCase);
	            	exampleRow.setVariableFromKey(filter.getCellTypeFilter(), 3);//has to be significant so filter on significant
	            }
	            if(filter.getCellSubTypeFilter()!=null) {
	            	//get the list of cell types that have this cell subtype
	            	Set<String> cellTypes=this.cellSubTypesToCellTypes.get(filter.getCellSubTypeFilter());
	            	//then set up a variable in the filter example for each one and say it has to be significant
	            	for(String cellType: cellTypes) {
	            		exampleMatcher.withMatcher(cellType, GenericPropertyMatcher::ignoreCase);
		            	exampleRow.setVariableFromKey(cellType, 3);//has to be significant so filter on significant
	            	}
	            }
	            
	            if(filter.getAssayFilter()!=null) {
	            	//get the list of cell types that have this cell subtype
	            	Set<String> cellTypes=this.assayToCellTypes.get(filter.getAssayFilter());
	            	//then set up a variable in the filter example for each one and say it has to be significant
	            	for(String cellType: cellTypes) {
	            		exampleMatcher.withMatcher(cellType, GenericPropertyMatcher::ignoreCase);
		            	exampleRow.setVariableFromKey(cellType, 3);//has to be significant so filter on significant
	            	}
	            }
	            Example<CellHeatmapRow> example = Example.of(exampleRow, exampleMatcher);
	   		 System.out.println("example="+example);
	   		List<CellHeatmapRow> rows=new ArrayList<>();
	   		 if(sort!=null) {
	   			 System.out.println("calling with sort="+sort);
	   			 rows = cellHeatmapRowsRepository.findAll(example, sort);
	   		 }
	   		 else {
	   			 rows= cellHeatmapRowsRepository.findAll(example);
	   		 }
			if(filter.getKeyword()!=null) {
				//now we use solr to filter based on gene and give it the best chance of finding a match
				rows=this.getRowsForKeywords(filter.getKeyword(), rows);
				
			}
			return rows;
	}
	
	private List<CellHeatmapRow> getRowsForKeywords(String keyword,List<CellHeatmapRow> rows) {
		List<CellHeatmapRow>filteredRows=new ArrayList<>();
		// now use our gene autosuggest field to get the appropriate gene back
		// auto_suggest:Adal
		// http://localhost:8080/data?keyword=4930578F03Rik returns Adal - also need to
		// handle spaces with quotes....!!!
		try {
			List<GeneDTO> genes = geneService.getGeneSymbolOrSynonymOrNameOrHuman(keyword);
			System.out.println(genes);
			for(GeneDTO gene: genes) {
				for(CellHeatmapRow row: rows) {
					if(row.getGene().equalsIgnoreCase(gene.getMarkerSymbol())) {
						filteredRows.add(row);
					}else {
						for(String synonym:gene.getMarkerSynonym()) {
							if(row.getGene().equalsIgnoreCase(synonym)) {
								filteredRows.add(row);
							}
						}
					}
				}
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filteredRows;
	}
	
	



	public List<CellHeatmapRow> queryForSingleRow(){
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withIgnoreCase()
	            .withMatcher("gene", GenericPropertyMatcher::ignoreCase);
//	            .withIgnoreNullValues()
//	            .withIgnoreCase();  
		CellHeatmapRow exampleRow = new CellHeatmapRow();
		exampleRow.setGene("Zranb1");
		 Example<CellHeatmapRow> example = Example.of(exampleRow, exampleMatcher);
		 System.out.println("example="+example);
		 Optional<CellHeatmapRow> singleRow = cellHeatmapRowsRepository.findOne(example);
		System.out.println("singleRow="+singleRow);
		List<CellHeatmapRow> rows=new ArrayList<>();
		rows.add(singleRow.get());
		return rows;
		 //return cellHeatmapRowsRepository.findAll(example);


//		 Example<Animal> example = Example.of(form.getAnimal(), exampleMatcher);
//	        
//		    Page<Animal> animals = animalrepository.findAll(example, 
//		                new PageRequest((first / pageSize), pageSize, sortOrder == SortOrder.ASCENDING ? Direction.ASC : Direction.DESC, sortField));
//		    
//		    this.dataSet = animals.getContent();
//		    setRowCount((int) animals.getTotalElements());
		 
		 
	 }
	
	
	public Types cellTypes() {
		if (uniqueCellTypes == null) {
			uniqueCellTypes = new TreeSet<>();
			// should extract these into methods in a data service for unit testing purposes
			List<CellParameter> dataList = cellRepo.findAll();

			for (CellParameter cellP : dataList) {
				uniqueCellTypes.add(cellP.getCellType());
			}
		}
		Types types = new Types();
		types.getTypes().addAll(uniqueCellTypes);
		return types;
	}
	
	public Types cellSubTypes() {
		if (uniqueCellSubTypes == null || cellSubTypesToCellTypes==null) {
			uniqueCellSubTypes = new TreeSet<>();
			// should extract these into methods in a data service for unit testing purposes
			List<CellParameter> dataList = cellRepo.findAll();

			for (CellParameter cellP : dataList) {
				uniqueCellSubTypes.add(cellP.getCellSubtype());
			}
		}
		Types types = new Types();
		types.getTypes().addAll(uniqueCellSubTypes);
		return types;
	}
	
	public  Types getAssays() {
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
		return types;
	}
	
	public  Types getConstructs() {
		List<String>uniqueConstructs=new ArrayList<>();
		uniqueConstructs.addAll(constructSet);
		Types types=new Types();
		types.getTypes().addAll(uniqueConstructs);
		return types;
	}
	
	private void initialiseCellSubTypesAndAssayToTypes() {
	
		
		// should extract these into methods in a data service for unit testing purposes
		List<CellParameter> dataList = cellRepo.findAll();

		for (CellParameter cellP : dataList) {
			String cellSubType=cellP.getCellSubtype();
			if(!cellSubTypesToCellTypes.containsKey(cellSubType)) {
				cellSubTypesToCellTypes.put(cellSubType, new HashSet<String>());
			}else {
				cellSubTypesToCellTypes.get(cellSubType).add(cellP.getCellType());
			}
			//same for assays
			String assay=cellP.getAssay();
			if(!assayToCellTypes.containsKey(assay)) {
				assayToCellTypes.put(assay, new HashSet<String>());
			}else {
				//System.out.println("assay adding is "+assay+ " with cellType="+cellP.getCellType());
				assayToCellTypes.get(assay).add(cellP.getCellType());
			}
			
		}
	
	
	}
}
