package uk.ac.ebi.threei.rest.controllers;

public class Filter {
	
	public String keyword; public String constructFilter;public String cellTypeFilter; public String cellSubTypeFilter;public String assayFilter;public  String sortField;


	@Override
	public String toString() {
		return "Filter [keyword=" + keyword + ", constructFilter=" + constructFilter + ", cellTypeFilter="
				+ cellTypeFilter + ", cellSubTypeFilter=" + cellSubTypeFilter + ", assayFilter=" + assayFilter
				+ ", sortField=" + sortField + "]";
	}
	

}
