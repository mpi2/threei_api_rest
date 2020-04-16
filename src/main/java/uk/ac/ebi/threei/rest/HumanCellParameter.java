package uk.ac.ebi.threei.rest;

import org.springframework.data.annotation.Id;

/**
 * Cell Parameter is parameters and associated information related to cell/marker information
 * The entities themselfs are parameter related as they are the primary key whereas we have multiple cell types per parameter
 * Fields and values are based on the spreadsheet provided by Lucie.
 * @author jwarren
 *
 */
public class HumanCellParameter {

	@Id private String id;

	private String parameterId;
	private String publicPopulationName;
	private String marker;
	private String panel;
	private String presentedAs;
	
	public HumanCellParameter(){
		
	}
	
	public HumanCellParameter(String parameterId, String publicPopulationName, String marker, String panel, String presentedAs){
		this.parameterId=parameterId;
		this.publicPopulationName=publicPopulationName;
		this.marker=marker;
		this.panel=panel;
		this.presentedAs=presentedAs;
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
	public String getPublicPopulationName() {
		return publicPopulationName;
	}
	public void setPublicPopulationName(String publicPopulationName) {
		this.publicPopulationName = publicPopulationName;
	}
	public String getMarker() {
		return marker;
	}
	public void setMarker(String marker) {
		this.marker = marker;
	}
	public String getPanel() {
		return panel;
	}
	public void setPanel(String panel) {
		this.panel = panel;
	}
	public String getPresentedAs() {
		return presentedAs;
	}
	public void setPresentedAs(String presentedAs) {
		this.presentedAs = presentedAs;
	}



	@Override
	public String toString() {
		return "CellParameter [id=" + id + ", parameterId=" + parameterId + ", parameterName=" + publicPopulationName
				+ ", marker=" + marker + ", panel=" + panel + ", presentedAs=" + presentedAs + "]";
	}

	
}
