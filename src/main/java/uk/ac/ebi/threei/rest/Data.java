package uk.ac.ebi.threei.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Data {
	
	//procedures for procedureHeatmap
	List<String> columnHeaders=new ArrayList<>();
	public List<String> getColumnHeaders() {
		return columnHeaders;
	}

	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}

	//genes
	public List<String> getRowHeaders() {
		return rowHeaders;
	}

	public void setRowHeaders(List<String> rowHeaders) {
		this.rowHeaders = rowHeaders;
	}

	List<String> rowHeaders=new ArrayList<>();
	
	@Override
	public String toString() {
		return "Data [id=" + id + ", heatmapType=" + heatmapType + ", data=" + data + "]";
	}

	@Id private String id;
	
	private String heatmapType;
	
	public String getHeatmapType() {
		return heatmapType;
	}

	public void setHeatmapType(String heatmapType) {
		this.heatmapType = heatmapType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private List<List<Integer>> data=new ArrayList<>();

	public List<List<Integer>> getData() {
		return data;
	}

	public void setData(List<List<Integer>> data) {
		this.data = data;
	}
	
	public String writeData() {
		String allData="";//should use String builder?
		for(List<Integer> cell:data) {
			allData+=cell;
		}
		return allData;
	}

	public void addColumnHeader(String header) {
		this.columnHeaders.add(header);
		
	}

	public void addRowHeader(String rowHeader) {
		this.rowHeaders.add(rowHeader);
		
	}

}
