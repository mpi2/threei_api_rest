package uk.ac.ebi.threei.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

//need this annotation if using the loader - comment out if not...?
//@SpringBootApplication
public class DataLoader implements CommandLineRunner {
	
	//wont run without this but doesn't use it - annoying!!!
	@Bean(name = "solrClient")
	HttpSolrClient gtSolrClient() {
		String solrBaseUrl="ballh";
		return new HttpSolrClient.Builder(solrBaseUrl+ "/gene").build();
	}
	
	private static String COMMA = ",";
	private static String KEY_DELIMITER="_";
	SortedSet<String> geneConstructSymbols = new TreeSet<>(Collections.reverseOrder());//set it up in reverse order so genes are at the top as highcharts row 0 is at the bottom - not what we want!

	private HashMap<String, Integer> geneConstructParameterToSignificance;
	
	@Autowired
	private CellParameterRepository repository;

	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private ProcedureHeatmapRowsRepository dataRowsRespository;

	private Data procedureData;
	private Data cellTypeData;
	private List<CellParameter> cellParameters;
	private Set<String> uniqueCellTypesForHeaders;
	private List<ProcedureHeatmapRow> procedureRowData;
	

	public static void main(String[] args) {
		System.out.println("running main method in DataLoader!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		SpringApplication.run(DataLoader.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Loading in DataLoader!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if (args.length > 1) {// we need two files at least - 1 for hit data per parameter and 1 for what
								// parameters are in what cell!
			String hitsDataFileLocation = args[0];// 171218_3i_data_for_heat_map.csv
			File hitsDataFile = new File(hitsDataFileLocation);
			if (hitsDataFile.exists()) {
				System.out.println("hits file exists");
				procedureData = this.getProcedureDataFromCsv(hitsDataFile);
				procedureRowData=this.getProcedureHeatmapRowsFromCsv(hitsDataFile);
				geneConstructParameterToSignificance = getGeneToParameterHitsFromFile(hitsDataFile);
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

			String dataFileLocation = args[1];
			System.out.println("data file presented as " + dataFileLocation.trim());
			// get parameter to cell information here
			File csvFile = new File(dataFileLocation);
			if (csvFile.exists()) {
				System.out.println("file exists! We can now get the data");
				cellParameters = this.getData(csvFile);
				uniqueCellTypesForHeaders=this.getUniqueCellTypesForHeaders(cellParameters);
				
				//now we need to construct the cell type map by looping though the headers for celltype and then allocating if
				//any of the parameters associated to that header for that gene are significant using the prev loaded hit file
				cellTypeData=createCellTypeHeatmap(uniqueCellTypesForHeaders, cellParameters, geneConstructParameterToSignificance );
				//System.out.println("celltypeData="+cellTypeData);

			} else {
				System.err.println("file not found!!!");
			}

			// get the hits information here (from the original csv file that Lucie gave
			// us?)

		} else {
			System.out.println(
					"you need to specify the full path to the file as the first argument at the command line!!!");
			System.out.println("Exiting application");
			System.exit(1);
		}
		
		// HashMap<String, CellParameter> uniqueCellNames=new HashMap<>();
		// procedure heatmap data
		saveDataToMongo();

		System.exit(0);
	}

	private Data createCellTypeHeatmap(Set<String> uniqueCellTypesForHeaders2, List<CellParameter> cellParameters2,
			HashMap<String, Integer> geneConstructParameterToSignificance2) {
		Data localCellTypeData=new Data();
		localCellTypeData.setHeatmapType("cellType");
		int column = 0;

		for (String header : uniqueCellTypesForHeaders2) {
			localCellTypeData.addColumnHeader(header);
			// cellData.addColumnHeader(header);
			
			int row=0;
			
			for (String gene : geneConstructSymbols) {
				localCellTypeData.addRowHeader(gene);
				
				Integer value = 0;// default is zero for each cell meaning no data.
				
				//System.out.println("looking for |"+gene + "_" + header+"|");
				//need to look at header and get the parameter names for that cell type, then look get the highest significance from that list and return it
				value = getHighestSignificanceForCellTypeHeadeer(geneConstructParameterToSignificance2, header, gene);

				List<Integer> cellData = new ArrayList<>();
				cellData.add(column);
				cellData.add(row);

				//System.out.println("value=" + value);

				cellData.add(value);
				//System.out.println("adding celldata=" + cellData);
				localCellTypeData.getData().add(cellData);
				row++;
			}
			column++;
		}
		return localCellTypeData;
	}

	private Integer getHighestSignificanceForCellTypeHeadeer(
			HashMap<String, Integer> geneConstructParameterToSignificance2, String header, String gene) {
		int highestSignficance = 0;
		List<String> parameterNamesForHeader = this.getParametersForCellType(header, cellParameters);

		for (String paramName : parameterNamesForHeader) {
			if (geneConstructParameterToSignificance2.containsKey(gene + KEY_DELIMITER + paramName)) {

				// add the cell with data here
				int value = geneConstructParameterToSignificance2.get(gene + KEY_DELIMITER + paramName);
				if (highestSignficance < value) {
					highestSignficance = value;
				}
				

			}
		}
		//System.out.println("returning highest significance=" + highestSignficance);
		return highestSignficance;
	}

	private List<String> getParametersForCellType(String header, List<CellParameter> cellParameters2) {
		List<String> cellParameterNames=new ArrayList<>();
		for(CellParameter cParam:cellParameters2) {
			if(cParam.getCellType().equals(header)){
				cellParameterNames.add(cParam.getParameterName());
			};
		}
		return cellParameterNames;
	}

	private void saveDataToMongo() {
		dataRepository.deleteAll();
		dataRowsRespository.deleteAll();
		repository.deleteAll();
		
		dataRepository.save(procedureData);
		dataRowsRespository.saveAll(procedureRowData);
		dataRepository.save(cellTypeData);
		// cellParameters
		saveParemeterToCells();
	}

	private Set<String> getUniqueCellTypesForHeaders(List<CellParameter> cellParameters2) {
		Set<String> headers=new TreeSet<>();
		for(CellParameter cell:cellParameters) {
			String cellType=cell.getCellType();
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
		// read in file line by line and add to CellParameter objects for loading into
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
					String geneSymbol = columns[1];
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
//				List<Integer> cellData = new ArrayList<>();
//				cellData.add(column);
//				cellData.add(row);

				//System.out.println("value=" + value);

				//cellData.add(value);
				//System.out.println("adding celldata=" + cellData);
				//procedureData.getData().add(cellData);
				}
				System.out.println("adding hRow ="+hRow);
				hRow.setFieldsFromMap();//set the variables from the map so we can use repo sorting on fields
				//we could empty the map after this to save space and loading time from rest service- but can keep for debugging?
				heatmapRows.add(hRow);
			}
			
		

		
		
		// loop over column/headers then loop over genes/rows - then see if we have data
		// for that cell if not create the cell and set value to 0.
//		int column = 0;
//
//		for (String header : DisplayProcedureMapper.getDisplayHeaderOrder()) {
//			procedureData.addColumnHeader(header);
//			// cellData.addColumnHeader(header);
//			boolean isColumn = false;
//			int row = 0;
//			for (String gene : geneSymbols) {
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

		//System.out.println("procedureData=" + procedureData.writeData());
		return heatmapRows;
	}


	private Data getProcedureDataFromCsv(File hitsDataFile) {
		// read in file line by line and add to CellParameter objects for loading into
		// mongodb
		Data procedureData = new Data();
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
					String geneSymbol = columns[1];
					String construct=columns[3];
					geneConstructSymbols.add(geneSymbol+KEY_DELIMITER+construct);
					if (columns.length >= 6) {
						procedureName = columns[5];
					} else {
						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
					}
					String displayProcedureName = DisplayProcedureMapper.getDisplayNameForProcedure(procedureName);
					String significance = columns[11];
					int significanceScore = SignificanceType.getRankFromSignificanceName(columns[11]).intValue();
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
		// should only have highest score for each cell now (gene and proceureName
		// combo)
		// but we need to loop over all the cells possible not just the ones we will get
		// so we can fill in the blanks!!???

		// loop over column/headers then loop over genes/rows - then see if we have data
		// for that cell if not create the cell and set value to 0.
		int column = 0;

		for (String header : DisplayProcedureMapper.getDisplayHeaderOrder()) {
			procedureData.addColumnHeader(header);
			// cellData.addColumnHeader(header);
			boolean isColumn = false;
			int row = 0;
			for (String gene : geneConstructSymbols) {
				procedureData.addRowHeader(gene);
				boolean isRow = false;
				Integer value = 0;// default is zero for each cell meaning no data.

				if (geneProcedureDisplayNameToValueMap.containsKey(gene + KEY_DELIMITER + header)) {

					// add the cell with data here
					value = geneProcedureDisplayNameToValueMap.get(gene + KEY_DELIMITER + header);
					// System.out.println("value from data="+value);

				}

				List<Integer> cellData = new ArrayList<>();
				cellData.add(column);
				cellData.add(row);

				//System.out.println("value=" + value);

				cellData.add(value);
				//System.out.println("adding celldata=" + cellData);
				procedureData.getData().add(cellData);
				row++;
			}
			column++;
		}

		//System.out.println("procedureData=" + procedureData.writeData());
		return procedureData;
	}

	
	
	public HashMap<String, Integer> getGeneToParameterHitsFromFile(File hitsDataFile) {

		// read in file line by line and add to CellParameter objects for loading into
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
				String[] columns = line.split(COMMA);
				if (!columns[0].equals("Id")) {// if id is headers so want to ignore
					String geneSymbol = columns[1];
					//geneSymbols.add(geneSymbol);
					String construct=columns[3];
					if (columns.length >= 7) {
						parameterName = columns[7];
					} else {
						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
					}
					String significance = columns[11];
					int significanceScore = SignificanceType.getRankFromSignificanceName(columns[11]).intValue();
					String key = geneSymbol + KEY_DELIMITER +construct+KEY_DELIMITER+ parameterName;
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
		
//		int column = 0;
//
//		for (String header : DisplayProcedureMapper.getDisplayHeaderOrder()) {
//			procedureData.addColumnHeader(header);
//			// cellData.addColumnHeader(header);
//			boolean isColumn = false;
//			int row = 0;
//			for (String gene : geneSymbols) {
//				procedureData.addRowHeader(gene);
//				boolean isRow = false;
//				Integer value = 0;// default is zero for each cell meaning no data.
//
//				if (geneProcedureDisplayNameToValueMap.containsKey(gene + "_" + header)) {
//
//					// add the cell with data here
//					value = geneProcedureDisplayNameToValueMap.get(gene + "_" + header);
//					// System.out.println("value from data="+value);
//
//				}
//
//				List<Integer> cellData = new ArrayList<>();
//				cellData.add(column);
//				cellData.add(row);
//
//				System.out.println("value=" + value);
//
//				cellData.add(value);
//				System.out.println("adding celldata=" + cellData);
//				procedureData.getData().add(cellData);
//				row++;
//			}
//			column++;
//		}

		//System.out.println("procedureData=" + procedureData.writeData());
		return geneParameterToSigValueMap;
	
	}
	
	
	private Integer getGeneIndex(String geneString) {
		int i = 0;
		for (String gene : geneConstructSymbols) {
			if (gene.equalsIgnoreCase(geneString)) {
				return i;
			}
			i++;
		}
		return i;
	}

	/**
	 * The csv file we have has extra newlines in it which we need to take the next
	 * line and add it to the previous one in these cases
	 * 
	 * @param hitsDataFile
	 * @return
	 */
	private Data cleanFile(File hitsDataFile) {
		// read in file line by line and add to CellParameter objects for loading into
		// mongodb
		Data procedureData = new Data();
		HashMap<String, Integer> geneProcedureDisplayNameToValueMap = new HashMap<>();
		try {

			InputStream inputFS = new FileInputStream(hitsDataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

			BufferedWriter writer = new BufferedWriter(new FileWriter(hitsDataFile.getAbsolutePath() + "out"));
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

	
	
	

	private void saveParemeterToCells() {
		for (CellParameter cellParam : cellParameters) {
			//System.out.println("cellParam=" + cellParam.toString());
			repository.save(cellParam);
		}

	}

	private List<CellParameter> getData(File csvFile) {
		// read in file line by line and add to CellParameter objects for loading into
		// mongodb
		List<CellParameter> inputList = new ArrayList<CellParameter>();
		try {

			InputStream inputFS = new FileInputStream(csvFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			// skip the header of the csv
			// String line;
			// while ((line = br.readLine()) != null) {
			// System.out.println(line);
			// }

			inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputList;

	}

	private Function<String, CellParameter> mapToItem = (line) -> {
		String[] p = line.split(COMMA);// a CSV has comma separated lines
		CellParameter cell = new CellParameter();
		cell.setParameterId(p[0].trim());// <-- this is the first column in the csv file
		cell.setParameterName(p[1].trim());
		cell.setCellType(p[2].trim());
		if (p[3] != null && p[3].trim().length() > 0) {
			cell.setCellSubtype(p[3].trim());
		}
		cell.setAssay(p[4].trim());
		// more initialization goes here
		return cell;
	};

}