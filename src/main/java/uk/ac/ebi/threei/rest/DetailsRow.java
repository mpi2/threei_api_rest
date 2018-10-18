package uk.ac.ebi.threei.rest;

import java.util.ArrayList;
import java.util.List;

public class DetailsRow {
	private String rowHeader;
	private String parameterStableId;
	private String assay;
	public String getAssay() {
		return assay;
	}
	public void setAssay(String assay) {
		this.assay = assay;
	}
	public String getParameterStableId() {
		return parameterStableId;
	}
	public void setParameterStableId(String parameterStableId) {
		this.parameterStableId = parameterStableId;
	}
	private List<Integer> cellData=new ArrayList<>();
	public String getRowHeader() {
		return rowHeader;
	}
	public void setRowHeader(String rowHeader) {
		this.rowHeader = rowHeader;
	}
	public List<Integer> getCellData() {
		return cellData;
	}
	public void setCellData(List<Integer> cellData) {
		this.cellData = cellData;
	}
	public void addSignificance(int sig) {
		this.cellData.add(sig);
		
	}
	
	
}
