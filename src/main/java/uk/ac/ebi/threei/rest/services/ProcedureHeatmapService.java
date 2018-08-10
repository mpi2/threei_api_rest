package uk.ac.ebi.threei.rest.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.ProcedureHeatmapRow;
import uk.ac.ebi.threei.rest.repositories.ProcedureHeatmapRowsRepository;

@Service
public class ProcedureHeatmapService {
	
	
	@Autowired
	ProcedureHeatmapRowsRepository procedureHeatmapRowsRepository;

	public HttpEntity<Data> getProcedureHeatmapData(String keyword, String construct) {
		System.out.println("calling data controller with heatmapType" + keyword + " construct=" + construct);
		Data data = new Data();//obect that holds all the data for this chart display
		data.setHeatmapType("procedure");
		//should extract these into methods in a data service for unit testing purposes
		List<ProcedureHeatmapRow> procedureRows = procedureHeatmapRowsRepository.findAll();//get an easily readable form of the rows for the heatmap
		//loop through the rows and get the row headers for (gene symbols)
		ArrayList<String> rowHeaders=new ArrayList<>();
		ArrayList<String> constructs=new ArrayList<>();
		int rowI=0;
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
		
		ArrayList<String> procedureHeaders = new ArrayList<>(Arrays.asList(ProcedureHeatmapRowsRepository.procedureDisplayHeaderOrder));
//		ArrayList<String> constructAndProcedureHeaders=new ArrayList<>();
//		constructAndProcedureHeaders.add("construct");
//		constructAndProcedureHeaders.addAll(procedureHeaders);
		data.setColumnHeaders(procedureHeaders);
		data.setRowHeaders(rowHeaders);
		data.setConstructs(constructs);
		return new ResponseEntity<Data>(data, HttpStatus.OK);
	}


	private void addCellData(Data data, int columnI, int rowI, Integer value) {
		CommonMethods.addCellData(data, columnI, rowI, value);
		
	}
}
