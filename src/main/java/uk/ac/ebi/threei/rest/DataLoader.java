package uk.ac.ebi.threei.rest;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataLoader implements CommandLineRunner {
	

	
		@Autowired
		private CellParameterRepository repository;

		public static void main(String[] args) {
			SpringApplication.run(DataLoader.class, args);
		}

		@Override
		public void run(String... args) throws Exception {

			if(args.length>0){
			String dataFileLocation=args[0];
			System.out.println("data file presented as "+dataFileLocation.trim());
			File csvFile=new File(dataFileLocation);
			if(csvFile.exists()){
				System.out.println("file exists! We can now get the data");
				this.getData(csvFile);
				
				
			}
			}else{
				System.out.println("you need to specify the full path to the file as the first argument at the command line!!!");
				System.out.println("Exiting application");
				System.exit(1);
			}
			
			repository.deleteAll();

			// save a couple of customers
			repository.save(new CellParameter("MGP_PBI_001_001", "Total T cell percentage", "Blood", "Total αβ T cells", "Population size affected"));
			repository.save(new CellParameter("MGP_PBI_021_001", "Total T cell number", "Blood", "Total αβ T cells", "Population size affected"));

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

		private void getData(File csvFile) {
			// read in file line by line and add to CellParameter objects for loading into mongodb
			
			
		}

	}