package uk.ac.ebi.threei.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Data {
	
	
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

	private List<List<String>> data=new ArrayList<>();

	public List<List<String>> getData() {
		return data;
	}

	public void setData(List<List<String>> data) {
		this.data = data;
	}
	
	public String writeData() {
		String allData="";//should use String builder?
		for(List<String> cell:data) {
			allData+=cell;
		}
		return allData;
	}

}
