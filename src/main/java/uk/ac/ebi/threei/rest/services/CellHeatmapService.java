package uk.ac.ebi.threei.rest.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
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
	private SortedSet<String> uniqueCellTypes;
	private SortedSet<String> uniqueAssays;
	private List<CellHeatmapRow> cellRows;
	SortedSet<String> constructSet=new TreeSet<>();//for filter menu dropdown only short version before (
	SortedSet<String> uniqueCellSubTypes;
	
	
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
	
	public Data getCellHeatmapData(Filter filter) {
		// System.out.println("filter="+filter);
		Data data = new Data();// obect that holds all the data for this chart display
		Sort sort = null;
		if (!StringUtils.isEmpty(filter.getSortField())) {
			String sortField = cellHeaderToFieldMap.get(filter.getSortField());
			System.out.println("sortVariable=" + sortField);
			sort = new Sort(Sort.Direction.ASC, sortField);
		}
//		 else {
//			// should extract these into methods in a data service for unit testing purposes
//			cellRows = cellHeatmapRowsRepository.findAll();// get an easily readable form of the rows for the heatmap
//		}

		// List<CellHeatmapRow> cellRows = cellHeatmapRowsRepository.findAll(sort);

		
		
		//put filter method in here to only return the rows we need
		cellRows = this.filterCellRows(filter, sort);
		System.out.println("cellrows size=" + cellRows.size());
//		for(CellHeatmapRow tmpRow:cellRows) {
//			if(!cellRowsFiltered.contains(tmpRow)) {
//				System.out.println("row not in filtered="+tmpRow);
//			}
//		}
		
		//System.out.println("cellRows after filter size="+cellRows.size());
		
		
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
				if(filter.getKeyword()!=null) {
					exampleMatcher.withMatcher("gene", GenericPropertyMatcher::ignoreCase);
					exampleRow.setGene(filter.getKeyword());
				}
				if(filter.getConstructFilter()!=null) {
					exampleMatcher.withMatcher("construct", GenericPropertyMatcher::startsWith);
					exampleRow.setConstruct(filter.getConstructFilter());
				}
	            if(filter.getCellTypeFilter()!=null) {
	            	exampleMatcher.withMatcher(filter.getCellTypeFilter(), GenericPropertyMatcher::ignoreCase);
	            	exampleRow.setVariableFromKey(filter.getCellTypeFilter(), 3);//has to be significant so filter on significant
	            }
	            Example<CellHeatmapRow> example = Example.of(exampleRow, exampleMatcher);
	   		 System.out.println("example="+example);
	   		 if(sort!=null) {
	   			 System.out.println("calling with sort="+sort);
	   		 return cellHeatmapRowsRepository.findAll(example, sort);
	   		 }
	   		 else {
	   			 return cellHeatmapRowsRepository.findAll(example);
	   		 }
//	            .withIgnoreNullValues()
//	            .withIgnoreCase();  

		
		
		
		


//		 Example<Animal> example = Example.of(form.getAnimal(), exampleMatcher);
//	        
//		    Page<Animal> animals = animalrepository.findAll(example, 
//		                new PageRequest((first / pageSize), pageSize, sortOrder == SortOrder.ASCENDING ? Direction.ASC : Direction.DESC, sortField));
//		    
//		    this.dataSet = animals.getContent();
//		    setRowCount((int) animals.getTotalElements());
		 
		 
	 
		
//		List<CellHeatmapRow> filteredRows=new ArrayList<>();
//		for(CellHeatmapRow row: cellRows) {
//			boolean addRow=true;
//			if(filter.constructFilter!=null && !filter.constructFilter.equals("")) {
//				if(!row.getConstruct().startsWith(filter.constructFilter)) {
//					addRow=false;
//				}
//			}
//			if(filter.cellTypeFilter!=null && !filter.cellTypeFilter.equals("")) {
//				System.out.println("cellTypeFilter="+filter.cellTypeFilter);
//				//need to check if the variable in camel case has a number of 4 to indicate it's significant for this row
//				Integer value=null;
//				try {
//					value = (Integer) get("getbCellPrecursors",row);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			    System.out.println(value);
//			    if(value<3)addRow=false;
////				if(row.getVarabileFromKey(filter.cellTypeFilter)<3) {
////					addRow=false;
////				}
//			}
//			if(addRow) {
//				filteredRows.add(row);
//			}
//		}
//		return filteredRows;
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
		if (uniqueCellSubTypes == null) {
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
		if(constructSet.isEmpty()) {
			this.getCellHeatmapData(new Filter());
		}
		List<String>uniqueConstructs=new ArrayList<>();
		uniqueConstructs.addAll(constructSet);
		Types types=new Types();
		types.getTypes().addAll(uniqueConstructs);
		return types;
	}
}
