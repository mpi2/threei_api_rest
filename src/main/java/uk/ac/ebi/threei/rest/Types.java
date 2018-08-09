package uk.ac.ebi.threei.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * general object used for easy access of dropdowns for e.g. assay types or cell types
 * @author jwarren
 *
 */
public class Types {
	private List<String> types=new ArrayList<>();

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	
	
}
