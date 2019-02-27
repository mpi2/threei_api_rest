package uk.ac.ebi.threei.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class DataCleaner {

	public static void main(String [] args) {
		File file=new File("/Users/jwarren/Documents/3i/data/171218_3i_data_for_heat_map.csv");
		DataCleaner.cleanFile(file);
	}
	/**
	 * The csv file we have has extra newlines in it which we need to take the next
	 * line and add it to the previous one in these cases
	 * we should then remove any comments with " " and just have blank ,, as we don't need the comments for the display and this should make the data cleaner
	 * @param hitsDataFile
	 * @return
	 */
	private static void cleanFile(File hitsDataFile) {
		int linesRead = 0;
		int linesFailed=0;
		int linesWritten=0;
		int linesJoined=0;
		HashMap<String, Integer> geneProcedureDisplayNameToValueMap = new HashMap<>();
		try {

			InputStream inputFS = new FileInputStream(hitsDataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

			BufferedWriter writer = new BufferedWriter(new FileWriter(hitsDataFile.getAbsolutePath() + "cleaned"));
			System.out.println("writing to file=" + hitsDataFile.getAbsolutePath() + "cleaned");

			// skip the header of the csv
			String line;
			String prevLine = "";
			int speechMarks=0;
			while ((line = br.readLine()) != null) {
				//skip empty lines with just newline or spaces and so don't write out in new file at all.
				if (line.trim().isEmpty()) {
					System.err.println("line length is 0");
					continue;
				}
				// System.out.println(line);

				String[] columns = line.split(",");
				
				if (columns.length > 18 && line.split("\"").length > 1 ) {
					//if line has 2 lots of quotes and these contain , then strip out the quotes and just leave the field blank
					//code later saves the line if it now has 18 fields as it should have?
					String[] delete=line.split("\"");
					line=removeComments(delete);
					writer.write(line);
					writer.newLine();
					linesWritten++;
					
				}else
				if (columns.length == 18) {
					// add the line to the file if it's 18 or more columns as indicates it's not
					// been truncated.
					if(!line.contains("\"")) {
					writer.write(line);
					writer.newLine();
					linesWritten++;
					}else {
						System.err.println("line has 18 columns and contains speech marks");
					}
				} else if (columns.length <14) {
					//if(line.split("\"").length + prevLine.split("\"").length <18)
					line = prevLine + line;
					//System.out.println("adding joined line=" + line);
					if(line.contains("\"")) {
							String[] delete=line.split("\"");
							String after="";
							if(delete.length==2) {
								//if only one speech mark then we need to just add the next line at least until we get to another line with only one speech mark
								//then remove the comment then process the columns
								
								System.out.println(" line has only one speech mark ");
								speechMarks++;
								prevLine = line;
								linesRead++;
								continue;
								
							}else {
								
								line=removeComments(delete);
								linesJoined++;
								
							}
					
					}
					String[] columnsSecondPass = line.split(",");
					if(columnsSecondPass.length==18) {
						writer.write(line);
						writer.newLine();
						linesWritten++;
					}
					else {
						linesFailed++;
						System.err.println("lines read="+linesRead+" error even after joining lines the columns is not 18 it has "+columnsSecondPass.length+" line="+line);
					}
				}
					
					
				

				prevLine = line;
				linesRead++;
				// 
			}

			// dataArray = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
			br.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("linesRead=" +linesRead);
		System.out.println("lines written="+linesWritten);
		System.out.println("linesFailed=" + linesFailed);
		System.out.println("linesJoined="+linesJoined);
		
	}
	private static String removeComments(String[] delete) {
		String line;
		String after;
		after=delete[2];
//								speechMarks++;
//								speechMarks++;
		String first=delete[0];
		String comment=delete[1];
		
		
		System.out.println("first="+first+" comment="+comment+" after="+after);
		//delete comment if we have a full comment with both ends of speechmarks
		line=first+after;
		return line;
	}
}
