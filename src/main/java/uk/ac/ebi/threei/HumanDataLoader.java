package uk.ac.ebi.threei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.CrudRepository;

import uk.ac.ebi.threei.rest.CellHeatmapRow;
import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.DisplayProcedureMapper;
import uk.ac.ebi.threei.rest.HumanCellParameter;
import uk.ac.ebi.threei.rest.ProcedureHeatmapRow;
import uk.ac.ebi.threei.rest.SignificanceType;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.repositories.CellHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.ParameterDetailsRepository;
import uk.ac.ebi.threei.rest.repositories.ProcedureHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.human.HumanCellHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.repositories.human.HumanProcedureHeatmapRowsRepository;
import uk.ac.ebi.threei.rest.services.GeneService;

//need this annotation if using the loader - comment out if not...?
@ComponentScan({"uk.ac.ebi.threei.rest", "uk.ac.ebi.threei.rest.repositories"})
@SpringBootApplication
public class HumanDataLoader implements CommandLineRunner {
	
	//wont run without this but doesn't use it - annoying!!!
	@Bean(name = "solrClient")
	HttpSolrClient gtSolrClient() {
		String solrBaseUrl="https://www.ebi.ac.uk/mi/impc/solr";
		return new HttpSolrClient.Builder(solrBaseUrl+ "/gene").build();
	}
	
	private static String COMMA = ",";
	private static int COLUMNS_IN_HEATMAP_CSV=17;
	private static String KEY_DELIMITER="&&";
	SortedSet<String> geneConstructSymbols = new TreeSet<>(Collections.reverseOrder());//set it up in reverse order so genes are at the top as highcharts row 0 is at the bottom - not what we want!

	private HashMap<String, Integer> geneConstructParameterToSignificance;
	
	@Autowired
	GeneService geneService;
	
	@Autowired
	private HumanProcedureHeatmapRowsRepository procedureRowsRespository;
	@Autowired
	private HumanCellHeatmapRowsRepository cellRowsRespository;
	@Autowired
	ParameterDetailsRepository parameterDetailsRepository;


	private List<HumanCellParameter> humanCellParameters;
	private Set<String> uniqueCellTypesForHeaders;
	private List<ProcedureHeatmapRow> procedureRowData;
	private List<CellHeatmapRow> cellHeatmapRows;
	private List<ParameterDetails> parameterDetails;
	HashMap<String,String> ensemblToSymbolMap=new HashMap<>();
	private int hitsFileErrorCount=0;
	//private HumanCellParameterRepository humanCellParameterRepository;
	

	public static void main(String[] args) {
		System.out.println("running main method in DataLoader!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		new SpringApplicationBuilder(HumanDataLoader.class)
        .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
        .run(args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Loading in DataLoader!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		
		if (args.length > 1) {// we need two files at least - 1 for hit data per parameter and 1 for what
								// parameters are in what cell!
			String hitsDataFileLocation = args[0];// 171218_3i_data_for_heat_map.csv, 171218_3i_data_for_heat_map.csvcleaned
			File hitsDataFile = new File(hitsDataFileLocation);
			if (hitsDataFile.exists()) {
				System.out.println("hits file exists");
				//procedureData = this.getProcedureDataFromCsv(hitsDataFile);
				
				procedureRowData=this.getProcedureHeatmapRowsFromCsv(hitsDataFile);
				geneConstructParameterToSignificance = getGeneToParameterHitsFromFile(hitsDataFile);
				parameterDetails = getDetailsFromCsv(hitsDataFile);
				int i=0;
				for(String key: geneConstructParameterToSignificance.keySet()) {
					System.out.println("geneConstructParameterToSignificance key="+key);
					i++;
					if(i>10)break;
				}
				
				
				// cleanFile(hitsDataFile);//only to be used on original file to remove wierd newlines
			} else {
				System.err.println("hits data file doesn't exist here:" + hitsDataFileLocation);
			}

			String cellFileLocation = args[1];
			System.out.println("data file presented as " + cellFileLocation.trim());
			// get parameter to cell information here
			File cellFile = new File(cellFileLocation);
			if (cellFile.exists()) {
				System.out.println("Cell file exists! We can now get the data");
				HumanParameterFileLoader humanParameterFileLoader=new HumanParameterFileLoader();
				humanCellParameters = humanParameterFileLoader.getData(cellFile);
				uniqueCellTypesForHeaders=this.getUniqueCellTypesForHeaders(humanCellParameters);
				int i=0;
				for(String key: uniqueCellTypesForHeaders) {
					System.out.println("uniqueCellTypesForHeaders="+key);
					i++;
					if(i>10)break;
				}
				//now we need to construct the cell type map by looping though the headers for celltype and then allocating if
				//any of the parameters associated to that header for that gene are significant using the prev loaded hit file
				//cellTypeData=createCellTypeHeatmap(uniqueCellTypesForHeaders, HumanCellParamters, geneConstructParameterToSignificance );
				cellHeatmapRows=getCellHeatmapRowsFromCsv(uniqueCellTypesForHeaders, humanCellParameters, geneConstructParameterToSignificance );
				//we need to add some of the procedure data to the cell rows as requested by Lucie
				cellHeatmapRows=addSomeProceduresToCellRows(cellHeatmapRows, procedureRowData);
				//need to add significance score after adding the extra procedure rows otherwise can't order based on significance for whole row
				
				for(CellHeatmapRow row: cellHeatmapRows) {
					row.calculateTotalSignificantScore();
					row.procedureSignificance=Collections.emptyMap();
				}
				//System.out.println("celltypeData="+cellTypeData);

			} else {
				System.err.println("cell file not found!!!");
			}

			// get the hits information here (from the original csv file that Lucie gave
			// us?)

		} else {
			System.out.println(
					"you need to specify the full path to the file as the first argument at the command line!!!");
			System.out.println("Exiting application");
			System.exit(1);
		}
		
		// HashMap<String, HumanCellParamter> uniqueCellNames=new HashMap<>();
		// procedure heatmap data
		String path = System.getProperty("user.home") + File.separator + "Documents";
		File outputFile=new File(path+File.separator+"humanCellOutput.csv");
		try {
			saveDataToFile(outputFile, cellHeatmapRows);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//saveDataToMongo();

		System.exit(0);
	}
	

	/**
	 * Load the data required for a details view of the data to display when clicked through from heatmap.
	 * @param hitsDataFile
	 * @return
	 */
	private List<ParameterDetails> getDetailsFromCsv(File hitsDataFile) {
		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		
		List<ParameterDetails> parameterDetails=new ArrayList<>();
		HashMap<String, ParameterDetails> geneConstructProcedureParameterIdSexGenotypeToValueMap = new HashMap<>();//these are the unique result and we just want
		//the most significant of these combinations?

		try {

			InputStream inputFS = new FileInputStream(hitsDataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			String line;
			int linesRead = 0;
			while ((line = br.readLine()) != null) {

				// System.out.println(line);
				String procedureName = "";
		
				String[] columns = line.split(COMMA);
				if (!columns[0].equals("Id")) {// if id is headers so want to ignore
					String geneSymbol = this.getTrueGeneSymbol(columns[1]);
					
					String construct=columns[3];
					if(columns.length<=4) {
						System.err.println("not enough columns in row="+line+" skipping!!!!!!!!!!!");
						continue;
					}
					String parameterId=columns[6];
					String parameterName=columns[7];
					String sex=columns[9];
					String significanceString=columns[11];//integers don't really make sense so convert string to logical interers ascending in significance
					int significanceScore=SignificanceType.getRankFromSignificanceName(significanceString);
					String genotype="";
					if(columns.length>=COLUMNS_IN_HEATMAP_CSV) {
					genotype=columns[16];
					if(!genotype.equals("Hom") && !genotype.equals("Het") && !genotype.equals("Both") && !genotype.equals("Hemi") && !genotype.equals("Mutant")) {
						System.err.println("genotype not as expected "+genotype);
						System.err.println("line="+line);
						hitsFileErrorCount++;
						System.out.println("hits File number of errors="+hitsFileErrorCount);
					}
					}
					geneConstructSymbols.add(geneSymbol+KEY_DELIMITER+construct);
					if (columns.length >= 6) {
						procedureName = columns[5];
						
					} else {
						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
					}
					String displayProcedureName = DisplayProcedureMapper.getDisplayNameForProcedure(procedureName);
					ParameterDetails pDetails=new ParameterDetails(geneSymbol, construct);
					pDetails.setProcedureName(procedureName);
					pDetails.setDisplayProcedureName(displayProcedureName);
					if(parameterId==null)continue;//we don't want to do anything if we don't have any  param info with this line...
					pDetails.setParameterId(parameterId);
					pDetails.setParameterName(parameterName);
					pDetails.setSex(sex);
					pDetails.setGenotype(genotype);
					pDetails.setSignificanceValue(significanceScore);
					
						
					if(pDetails.getProcedureName().equals("Fertility of Homozygous Knock-out Mice")) {	
					//System.out.println("adding parameterDetails ="+pDetails);
					}
					//pDetails.setFieldsFromMap();//set the variables from the map so we can use repo sorting on fields
					//we could empty the map after this to save space and loading time from rest service- but can keep for debugging?
					//parameterDetails.add(pDetails);
					//int significanceScore = SignificanceType.getRankFromSignificanceName(significanceScore).intValue();
					String key = geneSymbol + KEY_DELIMITER + construct+KEY_DELIMITER+displayProcedureName+KEY_DELIMITER+parameterId+KEY_DELIMITER+sex+KEY_DELIMITER+genotype;
					if (geneConstructProcedureParameterIdSexGenotypeToValueMap.containsKey(key)) {
						 ParameterDetails tmpPDetails = geneConstructProcedureParameterIdSexGenotypeToValueMap.get(key);
						 int oldScore=tmpPDetails.getSignificanceValue();
						if (oldScore < significanceScore) {
							geneConstructProcedureParameterIdSexGenotypeToValueMap.put(key, pDetails);
						} 
					} else {
						geneConstructProcedureParameterIdSexGenotypeToValueMap.put(key, pDetails);
					}
					linesRead++;
					// System.out.println(linesRead);
				}
				//if(linesRead>10)break;
				linesRead++;
				//System.out.println(linesRead);
			}
			System.out.println("generated map");
			System.out.println("number of genes/rows=" + geneConstructSymbols.size());
			System.out.println("number of procedures/columns=" + DisplayProcedureMapper.getDisplayHeaderOrder().length);

			// dataArray = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//this code should now be moved to the RestController that will give back the heatmap formatted data
		// should only have highest score for each cell now (gene and proceureName
		// combo)
		// but we need to loop over all the cells possible not just the ones we will get
		// so we can fill in the blanks!!???

		

		//System.out.println("procedureData=" + procedureData.writeData());
		parameterDetails.addAll(geneConstructProcedureParameterIdSexGenotypeToValueMap.values());
		return parameterDetails;
	}

	private List<CellHeatmapRow> addSomeProceduresToCellRows(List<CellHeatmapRow> cellHeatmapRows2,
			List<ProcedureHeatmapRow> procedureRowData2) {
		//to each row that should be in the same order we need to add the extra columns for DSS, influenza,Trichurus Challenge and Salmonella challenge
		int rowIndex=0;
		for(CellHeatmapRow cellRow: cellHeatmapRows2) {
			cellRow.setdSSChallenge(procedureRowData2.get(rowIndex).getdSSChallenge());
			cellRow.setInfluenza(procedureRowData2.get(rowIndex).getInfluenza());
			cellRow.setTrichurisChallenge(procedureRowData2.get(rowIndex).getTrichurisChallenge());
			cellRow.setSalmonellaChallenge(procedureRowData2.get(rowIndex).getSalmonellaChallenge());	
			rowIndex++;
		}
		
		return cellHeatmapRows2;
	}

	

	private Integer getHighestSignificanceForCellTypeHeadeer(
			HashMap<String, Integer> geneConstructParameterToSignificance2, String header, String gene, String construct) {
		int highestSignficance = 0;
		List<String> parameterIdsForHeader = this.getParametersForCellType(header, humanCellParameters);

		for (String paramId : parameterIdsForHeader) {
			String key=gene + KEY_DELIMITER +construct+KEY_DELIMITER+ paramId;//parameter name isn't enough here as they belong to multiple cellt types but parameter ids don't, need procedure name as well to match cell file and hits file unique row.
			if (geneConstructParameterToSignificance2.containsKey(key)) {

				// add the cell with data here
				int value = geneConstructParameterToSignificance2.get(key);
				//System.out.println("value is not null or 0="+value);
				if (highestSignficance < value) {
					highestSignficance = value;
				}
				

			}
		}
		//System.out.println("returning highest significance=" + highestSignficance);
		return highestSignficance;
	}

	private List<String> getParametersForCellType(String header, List<HumanCellParameter> humanCellParameters) {
		List<String> humanCellParamterNames=new ArrayList<>();
		for(HumanCellParameter cParam:humanCellParameters) {
			if(cParam.getParameterId().equals(header)){
				humanCellParamterNames.add(cParam.getParameterId());
			};
		}
		return humanCellParamterNames;
	}

	private void saveDataToFile(File outputFile, List<CellHeatmapRow> rows) throws IOException {
		//lets save data to a file before updload to allow easier debugging and a seperation of data collection, integration and load.
		//we don't know what type of data we are getting but at the moment we know we will get some sort of heatmap data so lets use the cellHeatmapRows
		 if ( !outputFile.exists() )
			 outputFile.createNewFile();
		 
		 System.out.println("saving human output file to "+outputFile.getAbsolutePath());

	        FileWriter fw = new FileWriter(outputFile);
	        BufferedWriter writer = new BufferedWriter( fw );
	        for(CellHeatmapRow row:rows) {
        	String columnData=row.getGene();
        	writeColumn(writer, columnData);
	        writeLastInRow(writer, row.getConstruct());
	        writer.newLine();
	        }
	        writer.flush();
	        writer.close();
	        fw.close();
		
	}
	
	private void writeLastInRow(BufferedWriter writer, String data) throws IOException {
		
	      writer.write("\"");	
	      writer.write(data);
	      writer.write("\"");
		}

	private void writeColumn(BufferedWriter writer, String data) throws IOException {
		
		writeLastInRow(writer, data);
		writer.write(",");
	}
	
	private void saveDataToMongo() {
		//dataRepository.deleteAll();
		System.out.println("deleting data from mongodb!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		cellRowsRespository.deleteAll();
		procedureRowsRespository.deleteAll();
		//humanCellParameterRepository.deleteAll();
		parameterDetailsRepository.deleteAll();
		//dataRepository.save(procedureData);
		System.out.println("saving data to mongodb!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		parameterDetailsRepository.saveAll(parameterDetails);
		procedureRowsRespository.saveAll(procedureRowData);
		cellRowsRespository.saveAll(cellHeatmapRows);
		//dataRepository.save(cellTypeData);
		// HumanCellParamters
		//saveParemeterToCells();
	}

	private Set<String> getUniqueCellTypesForHeaders(List<HumanCellParameter> humanCellParamters) {
		Set<String> headers=new TreeSet<>();
		for(HumanCellParameter cell:humanCellParameters) {
			String cellType=cell.getPublicPopulationName();
			headers.add(cellType);
		}
		return headers;
	}
	
	/**
	 * replacement for data repository so we get rows and can reorder/filter them using the repository
	 * @param hitsDataFile
	 * @return
	 */
	private List<ProcedureHeatmapRow> getProcedureHeatmapRowsFromCsv(File hitsDataFile) {
		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		Data procedureData = new Data();
		List<ProcedureHeatmapRow> heatmapRows=new ArrayList<>();
		procedureData.setHeatmapType("procedure");
		HashMap<String, Integer> geneProcedureDisplayNameToValueMap = new HashMap<>();

		try {

			InputStream inputFS = new FileInputStream(hitsDataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			String line;
			int linesRead = 0;
			while ((line = br.readLine()) != null) {

				// System.out.println(line);
				String procedureName = "";
				String[] columns = line.split(COMMA);
				if (!columns[0].equals("Id")) {// if id is headers so want to ignore
					if (columns.length == 17) {
						System.err.println("column number is not equal to what we expect for this line "+line);
					}
					String geneSymbol = this.getTrueGeneSymbol(columns[1]);
					String construct=columns[3];
					geneConstructSymbols.add(geneSymbol+KEY_DELIMITER+construct);
					if (columns.length >= 6) {
						procedureName = columns[5];
					} else {
						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
					}
					String displayProcedureName = DisplayProcedureMapper.getDisplayNameForProcedure(procedureName);
					String significance = columns[11];
					int significanceScore = SignificanceType.getRankFromSignificanceName(significance).intValue();
					String key = geneSymbol + KEY_DELIMITER + construct+KEY_DELIMITER+displayProcedureName;
					if (geneProcedureDisplayNameToValueMap.containsKey(key)) {
						int oldScore = geneProcedureDisplayNameToValueMap.get(key);
						if (oldScore < significanceScore) {
							geneProcedureDisplayNameToValueMap.put(key, significanceScore);
							// System.out.println("geneSymbol="+geneSymbol+" procedureName="+procedureName+"
							// displayName="+DisplayProcedureMapper.getDisplayNameForProcedure(procedureName)+"
							// significance="+significance +" new
							// significance="+SignificanceType.getRankFromSignificanceName(columns[11]));

							// System.out.println("old score is"+oldScore+" which should be different to
							// "+geneProcedureDisplayNameToValueMap.get(key));
						} // otherwise do nothing
					} else {
						geneProcedureDisplayNameToValueMap.put(key, significanceScore);
					}
					linesRead++;
					// System.out.println(linesRead);
				}
			}
			System.out.println("generated map");
			System.out.println("number of genes/rows=" + geneConstructSymbols.size());
			System.out.println("number of procedures/columns=" + DisplayProcedureMapper.getDisplayHeaderOrder().length);

			// dataArray = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//this code should now be moved to the RestController that will give back the heatmap formatted data
		// should only have highest score for each cell now (gene and proceureName
		// combo)
		// but we need to loop over all the cells possible not just the ones we will get
		// so we can fill in the blanks!!???

		int column = 0;
		//SortedSet<String> geneConstructSymbols = new TreeSet<>(Collections.reverseOrder());//set it up in reverse order so genes are at the top as highcharts row 0 is at the bottom - not what we want!

			// cellData.addColumnHeader(header);
			boolean isColumn = false;
			int row = 0;
			for (String geneConstruct : geneConstructSymbols) {
				String geneConstructArray[]=geneConstruct.split(KEY_DELIMITER);
				String gene=geneConstructArray[0];
				String construct=geneConstructArray[1];
				
				ProcedureHeatmapRow hRow=new ProcedureHeatmapRow(gene, construct);
				for (String header : DisplayProcedureMapper.getDisplayHeaderOrder()) {
				//procedureData.addRowHeader(gene);
				//boolean isRow = false;
					String key=gene + KEY_DELIMITER +construct+KEY_DELIMITER+ header;
				Integer value = 0;// default is zero for each cell meaning no data.

				if (geneProcedureDisplayNameToValueMap.containsKey(key)) {

					// add the cell with data here
					value = geneProcedureDisplayNameToValueMap.get(key);
					
					// System.out.println("value from data="+value);

				}
				hRow.getProcedureSignificance().put(header, value);//will add the value wether sig or zero so we don't have non existent cells and rows are same length
				}
				//System.out.println("adding hRow ="+hRow);
				hRow.setFieldsFromMap();//set the variables from the map so we can use repo sorting on fields
				hRow.calculateTotalSignificantScore();
				//we could empty the map after this to save space and loading time from rest service- but can keep for debugging?
				hRow.procedureSignificance=Collections.emptyMap();
				heatmapRows.add(hRow);
			}

		//System.out.println("procedureData=" + procedureData.writeData());
		return heatmapRows;
	}
	
	
	private List<CellHeatmapRow> getCellHeatmapRowsFromCsv(Set<String> uniqueCellTypesForHeaders2, List<HumanCellParameter> HumanCellParamters2,
			HashMap<String, Integer> geneConstructParameterToSignificance2) {
		List<CellHeatmapRow> cellHeatmapRows=new ArrayList<>();
		Map<String, CellHeatmapRow> cellHeatmapRowsMap=new LinkedHashMap<>();
		//We only want row for each gene construct combination so lets get back any row previously created for that to then test highest score?
		
			for (String geneConstruct : geneConstructSymbols) {
				CellHeatmapRow row;
				String geneConstructArray[]=geneConstruct.split(KEY_DELIMITER);
				String gene=geneConstructArray[0];
				String construct=geneConstructArray[1];
				if(!cellHeatmapRowsMap.containsKey(geneConstruct)) {
					row=new CellHeatmapRow(gene, construct);
					cellHeatmapRowsMap.put(geneConstruct, row);
				}
				row=cellHeatmapRowsMap.get(geneConstruct);//should always be one as if not already created and added above
				
				for (String header : uniqueCellTypesForHeaders2) {
				
				Integer value = 0;// default is zero for each cell meaning no data.
				
				//System.out.println("looking for |"+gene + "_" + header+"|");
				//need to look at header and get the parameter names for that cell type, then look get the highest significance from that list and return it
				
				value = getHighestSignificanceForCellTypeHeadeer(geneConstructParameterToSignificance2, header, gene, construct);
				
				row.getProcedureSignificance().put(header,value);
				
				//cell
				
				}
				
				
			}
			
			//loop over the oredered set and add to list after setting variables from map and emptying map
			for(Entry<String, CellHeatmapRow> entry : cellHeatmapRowsMap.entrySet()) {
			CellHeatmapRow row=entry.getValue();
			row.setFieldsFromMap();
			//System.out.println("adding row to cellHeatmapRows="+row);
			cellHeatmapRows.add(row);
			}
					
		System.out.println("cellHeatmapRows size="+cellHeatmapRows.size());
		return cellHeatmapRows;
	}


//	private Data getProcedureDataFromCsv(File hitsDataFile) {
//		// read in file line by line and add to HumanCellParamter objects for loading into
//		// mongodb
//		Data procedureData = new Data();
//		procedureData.setHeatmapType("procedure");
//		HashMap<String, Integer> geneProcedureDisplayNameToValueMap = new HashMap<>();
//
//		try {
//
//			InputStream inputFS = new FileInputStream(hitsDataFile);
//			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
//			// skip the header of the csv
//			String line;
//			int linesRead = 0;
//			while ((line = br.readLine()) != null) {
//
//				// System.out.println(line);
//				String procedureName = "";
//				String[] columns = line.split(COMMA);
//				if (!columns[0].equals("Id")) {// if id is headers so want to ignore
//					String geneSymbol = columns[1];
//					String construct=columns[3];
//					geneConstructSymbols.add(geneSymbol+KEY_DELIMITER+construct);
//					if (columns.length >= 6) {
//						procedureName = columns[5];
//					} else {
//						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
//					}
//					String displayProcedureName = DisplayProcedureMapper.getDisplayNameForProcedure(procedureName);
//					String significance = columns[11];
//					int significanceScore = SignificanceType.getRankFromSignificanceName(columns[11]).intValue();
//					String key = geneSymbol + KEY_DELIMITER + construct+KEY_DELIMITER+displayProcedureName;
//					if (geneProcedureDisplayNameToValueMap.containsKey(key)) {
//						int oldScore = geneProcedureDisplayNameToValueMap.get(key);
//						if (oldScore < significanceScore) {
//							geneProcedureDisplayNameToValueMap.put(key, significanceScore);
//							
//						} // otherwise do nothing
//					} else {
//						geneProcedureDisplayNameToValueMap.put(key, significanceScore);
//					}
//					linesRead++;
//					// System.out.println(linesRead);
//				}
//			}
//			System.out.println("generated map");
//			System.out.println("number of genes/rows=" + geneConstructSymbols.size());
//			System.out.println("number of procedures/columns=" + DisplayProcedureMapper.getDisplayHeaderOrder().length);
//
//			// dataArray = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// should only have highest score for each cell now (gene and proceureName
//		// combo)
//		// but we need to loop over all the cells possible not just the ones we will get
//		// so we can fill in the blanks!!???
//
//		// loop over column/headers then loop over genes/rows - then see if we have data
//		// for that cell if not create the cell and set value to 0.
//		int column = 0;
//
//		for (String header : DisplayProcedureMapper.getDisplayHeaderOrder()) {
//			procedureData.addColumnHeader(header);
//			// cellData.addColumnHeader(header);
//			boolean isColumn = false;
//			int row = 0;
//			for (String gene : geneConstructSymbols) {
//				procedureData.addRowHeader(gene);
//				boolean isRow = false;
//				Integer value = 0;// default is zero for each cell meaning no data.
//
//				if (geneProcedureDisplayNameToValueMap.containsKey(gene + KEY_DELIMITER + header)) {
//
//					// add the cell with data here
//					value = geneProcedureDisplayNameToValueMap.get(gene + KEY_DELIMITER + header);
//					// System.out.println("value from data="+value);
//
//				}
//
//				List<Integer> cellData = new ArrayList<>();
//				cellData.add(column);
//				cellData.add(row);
//
//				//System.out.println("value=" + value);
//
//				cellData.add(value);
//				//System.out.println("adding celldata=" + cellData);
//				procedureData.getData().add(cellData);
//				row++;
//			}
//			column++;
//		}
//
//		//System.out.println("procedureData=" + procedureData.writeData());
//		return procedureData;
//	}

	
	
	public HashMap<String, Integer> getGeneToParameterHitsFromFile(File hitsDataFile) {

		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		//Data procedureData = new Data();
		//procedureData.setHeatmapType("procedure");
		HashMap<String, Integer> geneParameterToSigValueMap = new HashMap<>();
		

		try {

			InputStream inputFS = new FileInputStream(hitsDataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			String line;
			int linesRead = 0;
			while ((line = br.readLine()) != null) {

				// System.out.println(line);
				String parameterName = "";
				String parameterId="";
				String[] columns = line.split(COMMA);
				if (!columns[0].equals("Id")) {// if id is headers so want to ignore
					String geneSymbol =this.getTrueGeneSymbol(columns[1]);
					
					//geneSymbols.add(geneSymbol);
					String construct=columns[3];
					if (columns.length >= 6) {
						parameterId= columns[6];
					}
					if (columns.length >= 7) {
						parameterName = columns[7];
					} else {
						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
					}
					//String significance = columns[11];
					int significanceScore = SignificanceType.getRankFromSignificanceName(columns[11]).intValue();
					String key = geneSymbol + KEY_DELIMITER +construct+KEY_DELIMITER+ parameterId;
					if (geneParameterToSigValueMap.containsKey(key)) {
						int oldScore = geneParameterToSigValueMap.get(key);
						if (oldScore < significanceScore) {
							geneParameterToSigValueMap.put(key, significanceScore);
							// System.out.println("geneSymbol="+geneSymbol+" procedureName="+procedureName+"
							// displayName="+DisplayProcedureMapper.getDisplayNameForProcedure(procedureName)+"
							// significance="+significance +" new
							// significance="+SignificanceType.getRankFromSignificanceName(columns[11]));

							// System.out.println("old score is"+oldScore+" which should be different to
							// "+geneProcedureDisplayNameToValueMap.get(key));
						} // otherwise do nothing
					} else {
						//System.out.println("adding key in GeneToParameterHitsFromFile"+key);
						geneParameterToSigValueMap.put(key, significanceScore);
					}
					linesRead++;
					// System.out.println(linesRead);
				}
			}
			System.out.println("generated gene to parameter sig map");
			System.out.println("number of rows=" + geneParameterToSigValueMap.keySet().size());
			//System.out.println("number of parameters/columns=" + DisplayProcedureMapper.getDisplayHeaderOrder().length);

			// dataArray = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//System.out.println("procedureData=" + procedureData.writeData());
		return geneParameterToSigValueMap;
	
	}

	private String getTrueGeneSymbol(String geneSymbol) throws IOException {
		if(geneSymbol.startsWith("ENSMUSG")) {
			String ensId=geneSymbol;
			if(!ensemblToSymbolMap.containsKey(ensId)) {
			try {
				geneSymbol=geneService.getGeneSymbolFromEnsemblId(ensId);
			System.out.println("getting gene symbol from ensembl id");
			ensemblToSymbolMap.put(ensId, geneSymbol);
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}else {
				geneSymbol=ensemblToSymbolMap.get(ensId);
			}
		}
		return geneSymbol;
	}
	
	
	

	/**
	 * The csv file we have has extra newlines in it which we need to take the next
	 * line and add it to the previous one in these cases
	 * 
	 * @param hitsDataFile
	 * @return
	 */
	private Data cleanFile(File hitsDataFile) {
		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		Data procedureData = new Data();
		HashMap<String, Integer> geneProcedureDisplayNameToValueMap = new HashMap<>();
		try {

			InputStream inputFS = new FileInputStream(hitsDataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

			BufferedWriter writer = new BufferedWriter(new FileWriter(hitsDataFile.getAbsolutePath() + "outCommmentsStripped"));
			System.out.println("writing to file=" + hitsDataFile.getAbsolutePath() + "out");

			// skip the header of the csv
			String line;
			int linesRead = 0;
			String prevLine = "";
			while ((line = br.readLine()) != null) {

				// System.out.println(line);

				String procedureName = "";
				String[] columns = line.split(",");
				System.out.println("columns length=" + columns.length);
				if (columns.length >= 18) {
					// add the line to the file if it's 18 or more columns as indicates it's not
					// been truncated.
					System.out.println("adding line to file");
					writer.write(line);
					writer.newLine();
				} else if (columns.length == 14) {
					System.out.println(
							"columns size is not 18 so shouldn't add line but storing as previousline=" + line);
				} else if (columns.length < 1) {
					System.err.println("line has no seperators =" + line);
				} else if (columns.length < 6) {
					line = prevLine + line;
					System.out.println("adding joined line=" + line);
					writer.write(line);
					writer.newLine();
				}

				prevLine = line;
				linesRead++;
				// System.out.println(linesRead);
			}

			// dataArray = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("procedureData=" + procedureData.writeData());
		return procedureData;
	}
	
	
	

}