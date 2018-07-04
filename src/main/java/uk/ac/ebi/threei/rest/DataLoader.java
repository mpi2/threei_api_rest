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
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

//need this annotation if using the loader - comment out if not...?
//@SpringBootApplication
public class DataLoader implements CommandLineRunner {
	private static String COMMA = ",";
	SortedSet<String> geneSymbols = new TreeSet<>();

	@Autowired
	private CellParameterRepository repository;

	@Autowired
	private DataRepository dataRepository;

	private Data procedureData;
	private List<CellParameter> cellParameters;

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
				// cleanFile(hitsDataFile);
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
		dataRepository.deleteAll();
		repository.deleteAll();
		// HashMap<String, CellParameter> uniqueCellNames=new HashMap<>();
		// procedure heatmap data
		this.saveProcedureHeatmapData();
		// cellParameters
		saveParemeterToCells();

		System.exit(0);
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
					geneSymbols.add(geneSymbol);
					if (columns.length >= 6) {
						procedureName = columns[5];
					} else {
						System.out.println("columns size is not 6 so can't get procedureName for line=" + line);
					}
					String displayProcedureName = DisplayProcedureMapper.getDisplayNameForProcedure(procedureName);
					String significance = columns[11];
					int significanceScore = SignificanceType.getRankFromSignificanceName(columns[11]).intValue();
					String key = geneSymbol + "_" + displayProcedureName;
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
			System.out.println("number of genes/rows=" + geneSymbols.size());
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
			for (String gene : geneSymbols) {
				procedureData.addRowHeader(gene);
				boolean isRow = false;
				Integer value = 0;// default is zero for each cell meaning no data.

				if (geneProcedureDisplayNameToValueMap.containsKey(gene + "_" + header)) {

					// add the cell with data here
					value = geneProcedureDisplayNameToValueMap.get(gene + "_" + header);
					// System.out.println("value from data="+value);

				}

				List<Integer> cellData = new ArrayList<>();
				cellData.add(column);
				cellData.add(row);

				System.out.println("value=" + value);

				cellData.add(value);
				System.out.println("adding celldata=" + cellData);
				procedureData.getData().add(cellData);
				row++;
			}
			column++;
		}

		System.out.println("procedureData=" + procedureData.writeData());
		return procedureData;
	}

	private Integer getGeneIndex(String geneString) {
		int i = 0;
		for (String gene : geneSymbols) {
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

	/**
	 * just save data into an array for the Procedure heatmap that should match the
	 * column headers for procedures
	 */
	private void saveProcedureHeatmapData() {
		// save just as a data array of arrays so looks like this at return from REST
		// service:
		// [column, row (going up), value] note value should be 0,1,2,3 so we can order
		// in significance
		// data: [[0, 0, 0], [0, 1, 1], [0, 2, 2], [0, 3, 3], [0, 4, 3], [1, 0, 0], [1,
		// 1, 1], [1, 2, 2], [1, 3, 3], [1, 4, 3]]

		dataRepository.save(procedureData);

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Data data : dataRepository.findAll()) {
			System.out.println(data);
		}
		System.out.println();

		// fetch an individual customer

		// System.out.println("Customers found with
		// findByParameterId('MGP_PBI_001_001'):");
		// System.out.println("--------------------------------");
		// for (CellParameter customer :
		// dataRepository.findByParameterId("MGP_PBI_001_001")) {
		// System.out.println(customer);
		// }

	}

	private void saveParemeterToCells() {
		for (CellParameter cellParam : cellParameters) {
			System.out.println("cellParam=" + cellParam.toString());
			repository.save(cellParam);
		}

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (CellParameter customer : repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		// fetch an individual customer

		System.out.println("Customers found with findByParameterId('MGP_PBI_001_001'):");
		System.out.println("--------------------------------");
		for (CellParameter customer : repository.findByParameterId("MGP_PBI_001_001")) {
			System.out.println(customer);
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