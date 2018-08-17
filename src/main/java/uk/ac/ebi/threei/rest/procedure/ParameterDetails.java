package uk.ac.ebi.threei.rest.procedure;

import org.springframework.data.annotation.Id;


public class ParameterDetails {

	@Id
	private String id;
	private String parameterId;
	private String parameterName;
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	private String procedureName;
	private String displayProcedureName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getDisplayProcedureName() {
		return displayProcedureName;
	}

	public void setDisplayProcedureName(String displayProcedureName) {
		this.displayProcedureName = displayProcedureName;
	}

	private String gene;
	private String cellType;

	public String getCellType() {
		return cellType;
	}

	public void setCellType(String cellType) {
		this.cellType = cellType;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getConstruct() {
		return construct;
	}

	public void setConstruct(String construct) {
		this.construct = construct;
	}

	private String construct;

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}



	public ParameterDetails(String gene2, String construct2) {
		this.gene=gene2;
		this.construct=construct2;
	}

	@Override
	public String toString() {
		return "ParameterDetails [id=" + id + ", parameterId=" + parameterId + ", parameterName=" + parameterName
				+ ", procedureName=" + procedureName + ", displayProcedureName=" + displayProcedureName + ", gene="
				+ gene + ", cellType=" + cellType + ", construct=" + construct + "]";
	}

	


}
