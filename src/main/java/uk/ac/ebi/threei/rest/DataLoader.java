package uk.ac.ebi.threei.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class DataLoader implements CommandLineRunner {
	private static String COMMA=",";

	
		@Autowired
		private CellParameterRepository repository;


		private List<CellParameter> cellParameters;

		public static void main(String[] args) {
			SpringApplication.run(DataLoader.class, args);
		}

		@Override
		public void run(String... args) {

			if(args.length>0){
			String dataFileLocation=args[0];
			System.out.println("data file presented as "+dataFileLocation.trim());
			File csvFile=new File(dataFileLocation);
			if(csvFile.exists()){
				System.out.println("file exists! We can now get the data");
				cellParameters=this.getData(csvFile);
				
				
			}
			}else{
				System.out.println("you need to specify the full path to the file as the first argument at the command line!!!");
				System.out.println("Exiting application");
				System.exit(1);
			}
			
			repository.deleteAll();

			// cellParameters
			for(CellParameter cellParam: cellParameters){
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

			System.exit(0);
		}

		private List<CellParameter> getData(File csvFile) {
			// read in file line by line and add to CellParameter objects for loading into mongodb
			 List<CellParameter> inputList = new ArrayList<CellParameter>();
			    try{
			      
			      InputStream inputFS = new FileInputStream(csvFile);
			      BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));
			      // skip the header of the csv
			      inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			      br.close();
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
			    return inputList ;
			
		}
		
		private Function<String, CellParameter> mapToItem = (line) -> {
			  String[] p = line.split(COMMA);// a CSV has comma separated lines
			  CellParameter cell = new CellParameter();
			  cell.setParameterId(p[0]);//<-- this is the first column in the csv file
			  cell.setParameterName(p[1]);
			  cell.setAssay(p[2]);
			  if (p[3] != null && p[3].trim().length() > 0) {
			    cell.setCellType(p[3]);
			  }
			  cell.setCellSubtype(p[4]);
			  //more initialization goes here
			  return cell;
			};

	}