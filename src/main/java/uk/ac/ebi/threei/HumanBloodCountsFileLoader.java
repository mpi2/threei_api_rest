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

import uk.ac.ebi.threei.rest.BloodCountLine;
import uk.ac.ebi.threei.rest.HumanBloodCountFile;
import uk.ac.ebi.threei.rest.HumanCellParameter;


/**
 * Load data from the preliminary file named population_names as a csv
 * @author jwarren
 *
 */
public class HumanBloodCountsFileLoader {
	private static String SEPARATER = ",";
	
	public static void main(String args[]) {
		File populationFile=new File("/Users/jwarren/Documents/covid/firstDataExample/P5_Whole_blood_counts_20200330.csv");
		HumanBloodCountsFileLoader fileLoader=new HumanBloodCountsFileLoader();
		List<BloodCountLine> countFiles = fileLoader.getData(populationFile);
		for(BloodCountLine counts:countFiles) {
			System.out.println("cell="+counts);
			
		}
	}
	
	public List<BloodCountLine> getData(File cellFile) {
		// read in file line by line and add to HumanCellParamter objects for loading into
		// mongodb
		List<BloodCountLine> inputList = new ArrayList<BloodCountLine>();
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
	
	private Function<String, BloodCountLine> mapToItem = (line) -> {
		String properCsvLine;
		BloodCountLine countLine = new BloodCountLine();
		
		String[] speechSep = line.split("\"", -1);//should get 3 items
		if(speechSep.length>2) {
		System.out.println("speechSep"+speechSep[0]);
		String fileName=speechSep[1];
		String fileNameReplacedCommas=fileName.replace(",", "_");
		System.out.println(fileNameReplacedCommas);
		properCsvLine=line.replace(fileName, fileNameReplacedCommas);
		}else {
			properCsvLine=line;
		}
		
		String[] p = properCsvLine.split(SEPARATER, -1);// a CSV has comma separated lines -1 means it doesn' ignore empty strings
		System.out.println("line="+properCsvLine+" "+p.length);
		if(p.length<1) {
			return countLine;
		}
		
		if (p[0] != null && p[0].trim().length() > 0) {
			countLine.setId(p[0].trim());// <-- this is the first column in the csv file
		}
		if (p[1] != null && p[1].trim().length() > 0) {
			countLine.setSampleId(p[1].trim());
		}
		if (p[2] != null && p[2].trim().length() > 0) {
			countLine.setFile(p[2].trim());
		}
		if (p[6] != null && p[6].trim().length() > 0) {
			countLine.setCells(Long.parseLong(p[6].trim()));
		}
		
		List<Long> counts=new ArrayList<>();
		for(int i=7; i<p.length;i++) {
			if (p[i] != null && p[i].trim().length() > 0) {
				counts.add(Long.parseLong(p[i]));
			}
		}
		countLine.setCounts(counts);
		//cell.setPresentedAs(p[4].trim());
		// more initialization goes here
		return countLine;
	};

}
