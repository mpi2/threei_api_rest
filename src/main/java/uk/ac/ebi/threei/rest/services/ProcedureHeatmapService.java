package uk.ac.ebi.threei.rest.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatcher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uk.ac.ebi.threei.rest.CellHeatmapRow;
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.ProcedureHeatmapRow;
import uk.ac.ebi.threei.rest.controllers.Filter;
import uk.ac.ebi.threei.rest.repositories.ProcedureHeatmapRowsRepository;

@Service
public class ProcedureHeatmapService {
	
	
	@Autowired
	ProcedureHeatmapRowsRepository procedureHeatmapRowsRepository;
	@Autowired
	GeneService geneService;
	
	static final Map<String, String> procedureHeaderToFieldMap;
	
	SortedSet<String> constructSet=new TreeSet<>();//for filter menu dropdown only short version before (
	
	static {
		Map<String, String> aMap = new HashMap<>();
		aMap.put("Homozygous viability at P14", "homozygousViabilityAtP14");
		aMap.put("Homozygous Fertility", "homozygousFertility");
		aMap.put("Haematology", "haematology");
		aMap.put("Peripheral Blood Leukocytes", "peripheralBloodLeukocytes");
		aMap.put("Spleen", "spleen");
		aMap.put("Mesenteric Lymph Node", "mesentericLymphNode");
		aMap.put("Bone Marrow", "boneMarrow");
		aMap.put("Ear Epidermis", "earEpidermis");
		aMap.put("Anti-nuclear Antibodies", "antinuclearAntibodies");
		aMap.put("Cytotoxic T Cell Function", "cytotoxicTCellFunction");
		aMap.put("DSS Challenge", "dSSChallenge");
		aMap.put("Influenza", "influenza");
		aMap.put("Trichuris Challenge", "trichurisChallenge");
		aMap.put("Salmonella Challenge", "salmonellaChallenge");
		procedureHeaderToFieldMap = Collections.unmodifiableMap(aMap);
	}

	public List<ProcedureHeatmapRow> filterCellRows(Filter filter, Sort sort) {

		System.out.println("filter in filter function="+filter);
		ProcedureHeatmapRow exampleRow = new ProcedureHeatmapRow();
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase();
//				if(filter.getKeyword()!=null) {
//					exampleMatcher.withMatcher("gene", GenericPropertyMatcher::ignoreCase);
//					exampleRow.setGene(filter.getKeyword());
//				}
				if(filter.getConstructFilter()!=null) {
					exampleMatcher.withMatcher("construct", GenericPropertyMatcher::startsWith);
					exampleRow.setConstruct(filter.getConstructFilter());
				}
	            if(filter.getProcedure()!=null) {
	            	exampleMatcher.withMatcher(filter.getProcedure(), GenericPropertyMatcher::ignoreCase);
	            	exampleRow.setVariableFromKey(filter.getProcedure(), 3);//has to be significant so filter on significant
	            }
	            
	            Example<ProcedureHeatmapRow> example = Example.of(exampleRow, exampleMatcher);
	   		 System.out.println("example="+example);
	   		 List<ProcedureHeatmapRow> rows=new ArrayList<>();
			if(sort!=null) {
	   			 System.out.println("calling with sort="+sort);
	   		 rows= procedureHeatmapRowsRepository.findAll(example, sort);
	   		 }
	   		 else {
	   			 rows= procedureHeatmapRowsRepository.findAll(example);
	   		 }

			if(filter.getKeyword()!=null) {
				rows=this.getRowsForKeywords(filter.getKeyword(),rows);
			}
			return rows;
	}
	
	
	public HttpEntity<Data> getProcedureHeatmapData(Filter filter) {
		System.out.println("calling data controller with filter fields= " + filter.getKeyword() + " construct=" + filter.getConstructFilter()+"  sort field="+filter.getSortField()+ " filter by procedure="+filter.getProcedure());
		Data data = new Data();//obect that holds all the data for this chart display
		Sort sort = null;
		data.setHeatmapType("procedure");
		//should extract these into methods in a data service for unit testing purposes
		
//		if (!StringUtils.isEmpty(filter.getSortField())) {
//			String sortField = procedureHeaderToFieldMap.get(filter.getSortField());
//			System.out.println("sortVariable=" + sortField);
//			sort = new Sort(Sort.Direction.ASC, sortField);
//		}
		sort=new Sort(Sort.Direction.ASC, "totalSignificanceScore");
		List<ProcedureHeatmapRow> procedureRows = this.filterCellRows(filter, sort);//get an easily readable form of the rows for the heatmap
		System.out.println("procedure rows size="+procedureRows.size());
//		procedureRows.sort(new Comparator<ProcedureHeatmapRow>() {
//		    @Override
//		    public int compare(ProcedureHeatmapRow r1, ProcedureHeatmapRow r2) {
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
		
		ArrayList<String> procedureHeaders = new ArrayList<>(Arrays.asList(ProcedureHeatmapRow.procedureDisplayHeaderOrder));
//		ArrayList<String> constructAndProcedureHeaders=new ArrayList<>();
//		constructAndProcedureHeaders.add("construct");
//		constructAndProcedureHeaders.addAll(procedureHeaders);
		data.setColumnHeaders(procedureHeaders);
		data.setRowHeaders(rowHeaders);
		data.setConstructs(constructs);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}
	
	private List<ProcedureHeatmapRow> getRowsForKeywords(String keyword,List<ProcedureHeatmapRow> rows) {
		List<ProcedureHeatmapRow>filteredRows=new ArrayList<>();
		// now use our gene autosuggest field to get the appropriate gene back
		// auto_suggest:Adal
		// http://localhost:8080/data?keyword=4930578F03Rik returns Adal - also need to
		// handle spaces with quotes....!!!
		try {
			List<GeneDTO> genes = geneService.getGeneSymbolOrSynonymOrNameOrHuman(keyword);
			System.out.println(genes);
			for(GeneDTO gene: genes) {
				for(ProcedureHeatmapRow row: rows) {
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


	private void addCellData(Data data, int columnI, int rowI, Integer value) {
		CommonMethods.addCellData(data, columnI, rowI, value);
		
	}
}
