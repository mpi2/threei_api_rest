package uk.ac.ebi.threei.rest;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.threei.rest.procedure.ParameterDetails;

/**
 * Data representation for the procedure page that is linked from the procedure heatmap cells
 * @author jwarren
 *
 */
public class ProcedurePage {
	
	private List<ParameterDetails> parameterDetails;
	private List<String> columnHeaders;//parameter and then string combination of sex and genotype
	private List<DetailsRow> rows=new ArrayList<>();//hold data in an easy to display format so we can just loop over the rows and display in the same order as headers
	public List<DetailsRow> getRows() {
		return rows;
	}
	public void setRows(List<DetailsRow> rows) {
		this.rows = rows;
	}
	public List<ParameterDetails> getParameterDetails() {
		return parameterDetails;
	}
	public void setParameterDetails(List<ParameterDetails> parameterDetails) {
		this.parameterDetails = parameterDetails;
	}
	public List<String> getColumnHeaders() {
		return columnHeaders;
	}
	public void setColumnHeaders(List<String> columnHeaders) {
		this.columnHeaders = columnHeaders;
	}
	
	
	

}
