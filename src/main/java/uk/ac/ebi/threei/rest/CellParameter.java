package uk.ac.ebi.threei.rest;

import org.springframework.data.annotation.Id;

/**
 * Cell Parameter is parameters and associated information related to cell/assay information
 * The entities themselfs are parameter related as they are the primary key whereas we have multiple cell types per parameter
 * Fields and values are based on the spreadsheet provided by Lucie.
 * @author jwarren
 *
 */
public class CellParameter {

	@Id private String id;

	private String parameterId;
	private String parameterName;
	private String assay;
	private String cellType;
	private String cellSubtype;
	
	
	public CellParameter(String parameterId, String parameterName, String assay, String cellType, String cellSubtype){
		this.parameterId=parameterId;
		this.parameterName=parameterName;
		this.assay=assay;
		this.cellType=cellType;
		this.cellSubtype=cellSubtype;
	}
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public String getAssay() {
		return assay;
	}
	public void setAssay(String assay) {
		this.assay = assay;
	}
	public String getCellType() {
		return cellType;
	}
	public void setCellType(String cellType) {
		this.cellType = cellType;
	}
	public String getCellSubtype() {
		return cellSubtype;
	}
	public void setCellSubtype(String cellSubtype) {
		this.cellSubtype = cellSubtype;
	}



	@Override
	public String toString() {
		return "CellParameter [id=" + id + ", parameterId=" + parameterId + ", parameterName=" + parameterName
				+ ", assay=" + assay + ", cellType=" + cellType + ", cellSubtype=" + cellSubtype + "]";
	}

	
}
