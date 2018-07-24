package uk.ac.ebi.threei.rest.procedure;
import java.util.HashMap;
import java.util.Map;


public class ParameterRow {
	private String parameterName = "Parameter Name";
	private String parameterId="param id here";
	// should be female Hom or male Hom to match the possible column headers
	private Map<String, Result> headerKeyToResult = new HashMap<>();
	public Map<String, Result> getHeaderKeyToResult() {
		return headerKeyToResult;
	}
	public void setHeaderKeyToResult(Map<String, Result> headerKeyToResult) {
		this.headerKeyToResult = headerKeyToResult;
	}
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	

	public ParameterRow(String parameterName, String parameterId) {
		this.parameterName=parameterName;
		this.parameterId=parameterId;
		
	}
	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	//this method is currently used to add a blank result where there isn't one for the appropriate header
	public void addResult(Result result) {
		headerKeyToResult.put(result.getHeaderKey(), result);
		
	}

}
