package uk.ac.ebi.threei.rest.procedure;

public enum ZygosityType {
	
	Het,
	Hom,
	Hemi,
	Mutant;
	
	public String getName(){
		return this.toString();
	}

	
}
