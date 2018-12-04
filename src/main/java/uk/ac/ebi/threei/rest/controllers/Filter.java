package uk.ac.ebi.threei.rest.controllers;

public class Filter {
	
	private String keyword; private String constructFilter;private String cellTypeFilter; private String cellSubTypeFilter;private String assayFilter;private  String sortField; private String procedure ;


	@Override
	public String toString() {
		return "Filter [keyword=" + keyword + ", constructFilter=" + constructFilter + ", cellTypeFilter="
				+ cellTypeFilter + ", cellSubTypeFilter=" + cellSubTypeFilter + ", assayFilter=" + assayFilter
				+ ", sortField=" + sortField + "]";
	}


	public String getProcedure() {
		return procedure;
	}


	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}


	public String getKeyword() {
		return keyword;
	}


	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}


	public String getConstructFilter() {
		return constructFilter;
	}


	public void setConstructFilter(String constructFilter) {
		this.constructFilter = constructFilter;
	}


	public String getCellTypeFilter() {
		return cellTypeFilter;
	}


	public void setCellTypeFilter(String cellTypeFilter) {
		this.cellTypeFilter = cellTypeFilter;
	}


	public String getCellSubTypeFilter() {
		return cellSubTypeFilter;
	}


	public void setCellSubTypeFilter(String cellSubTypeFilter) {
		this.cellSubTypeFilter = cellSubTypeFilter;
	}


	public String getAssayFilter() {
		return assayFilter;
	}


	public void setAssayFilter(String assayFilter) {
		this.assayFilter = assayFilter;
	}


	public String getSortField() {
		return sortField;
	}


	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
	

}
