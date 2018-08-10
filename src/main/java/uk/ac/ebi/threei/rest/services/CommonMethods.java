package uk.ac.ebi.threei.rest.services;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.threei.rest.Data;

public class CommonMethods {

	public static  Data addCellData(Data data, int columnI, int rowI, int value) {
		List<Integer> cellData = new ArrayList<>();
		cellData.add(columnI);
		cellData.add(rowI);
		cellData.add(value);
		data.getData().add(cellData);
		return data;
//		CellData cell=new CellData();
//		cell.setColumnI(columnI);
//		cell.setRowI(rowI);
//		cell.setValue(value);
//		celldata.getCellData().add(cell);
		//System.out.println("adding cell data="+cellData);
	}
	
}
