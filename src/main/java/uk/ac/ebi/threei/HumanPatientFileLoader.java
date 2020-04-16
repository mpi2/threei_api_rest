package uk.ac.ebi.threei;

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

import uk.ac.ebi.threei.rest.HumanCellParameter;
import uk.ac.ebi.threei.rest.HumanPatientData;
import uk.ac.ebi.threei.rest.procedure.SexType;


/**
 * Load data from the preliminary file named population_names as a csv
 * @author jwarren
 *
 */
public class HumanPatientFileLoader {
	private static String SEPARATER = ",";
	
	public static void main(String args[]) {
		File populationFile=new File("/Users/jwarren/Documents/covid/firstDataExample/patient_metadata.csv");
		HumanPatientFileLoader fileLoader=new HumanPatientFileLoader();
		List<HumanPatientData> cells = fileLoader.getData(populationFile);
		for(HumanPatientData cell:cells) {
			System.out.println("Patient Data="+cell);
			
		}
	}
	
	private List<HumanPatientData> getData(File cellFile) {
		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		List<HumanPatientData> inputList = new ArrayList<HumanPatientData>();
		try {

			InputStream inputFS = new FileInputStream(cellFile);
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
	
	private Function<String, HumanPatientData> mapToItem = (line) -> {
		String[] p = line.split(SEPARATER, -1);// a CSV has comma separated lines
		HumanPatientData cell = new HumanPatientData();
//		if(p.length<7) {
//			return cell;
//		}
		cell.setCovid(p[0].trim());// <-- this is the first column in the csv file
		if (p[1] != null && p[1].trim().length() > 0) {
			cell.setYearOfBirth(Integer.parseInt(p[1].trim()));
		}
		cell.setAge(Integer.parseInt(p[2].trim()));
		cell.setSex(SexType.getByDisplayName(p[3].trim()));
		System.out.println("line="+line+" "+p.length);
		if (p[4] != null && p[4].trim().length() > 0) {
			cell.setOntologyId(p[4].trim());
		}
		if (p[5] != null && p[5].trim().length() > 0) {
			cell.setClinicalStatus(p[5].trim());
		}
		if (p[6] != null && p[6].trim().length() > 0) {
			cell.setClinicalOutcome(p[6].trim());
		}
		// more initialization goes here
		return cell;
	};

}
