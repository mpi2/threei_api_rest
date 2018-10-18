package uk.ac.ebi.threei.rest.procedure;

import org.springframework.data.annotation.Id;


public class ParameterDetails {

	@Id
	private String id;
	private String parameterId;
	private String parameterName;
	private String assay;//populated by cell_type info to display on procedure page
	public String getAssay() {
		return assay;
	}

	public void setAssay(String assay) {
		this.assay = assay;
	}

	private String sex;
	int significanceValue;
	
	public int getSignificanceValue() {
		return significanceValue;
	}

	public void setSignificanceValue(int significanceValue) {
		this.significanceValue = significanceValue;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	private String genotype;

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



	public ParameterDetails(String gene, String construct) {
		this.gene=gene;
		this.construct=construct;
	}

	@Override
	public String toString() {
		return "ParameterDetails [id=" + id + ", parameterId=" + parameterId + ", parameterName=" + parameterName
				+ ", sex=" + sex + ", significanceValue=" + significanceValue + ", genotype=" + genotype
				+ ", procedureName=" + procedureName + ", displayProcedureName=" + displayProcedureName + ", gene="
				+ gene + ", cellType=" + cellType + ", construct=" + construct + "]";
	}

	


}
