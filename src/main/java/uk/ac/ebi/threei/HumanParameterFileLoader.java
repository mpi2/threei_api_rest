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


/**
 * Load data from the preliminary file named population_names as a csv
 * @author jwarren
 *
 */
public class HumanParameterFileLoader {
	private static String SEPARATER = ",";
	
	public static void main(String args[]) {
		File populationFile=new File("/Users/jwarren/Documents/covid/firstDataExample/population_names_200331 - Sheet1.csv");
		HumanParameterFileLoader fileLoader=new HumanParameterFileLoader();
		List<HumanCellParameter> cells = fileLoader.getData(populationFile);
		for(HumanCellParameter cell:cells) {
			System.out.println("cell="+cell);
			
		}
	}
	
	public List<HumanCellParameter> getData(File cellFile) {
		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		List<HumanCellParameter> inputList = new ArrayList<HumanCellParameter>();
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
	
	private Function<String, HumanCellParameter> mapToItem = (line) -> {
		HumanCellParameter cell = new HumanCellParameter();
		String[] p = line.split(SEPARATER, -1);// a CSV has comma separated lines -1 means it doesn' ignore empty strings
		System.out.println("line="+line+" "+p.length);
		if(p.length<1) {
			return cell;
		}
		
		if (p[0] != null && p[0].trim().length() > 0) {
			cell.setPanel(p[0].trim());// <-- this is the first column in the csv file
		}
		if (p[1] != null && p[1].trim().length() > 0) {
			cell.setMarker(p[1].trim());
		}
		if (p[2] != null && p[2].trim().length() > 0) {
			cell.setParameterId(p[2].trim());
		}
		if (p[3] != null && p[3].trim().length() > 0) {
			cell.setPublicPopulationName(p[3].trim());
		}
		cell.setPresentedAs(p[4].trim());
		// more initialization goes here
		return cell;
	};

}
