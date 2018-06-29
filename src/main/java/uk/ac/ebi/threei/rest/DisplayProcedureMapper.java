package uk.ac.ebi.threei.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DisplayProcedureMapper {
	
	private static final Map<String, String> realProcedureTodDisplayName= DisplayProcedureMapper.createMap();//for use internally as a couple of procedure display names map to more than one procedure so use getDisplayName method externally 
	private static final String[] displayHeaderOrder={ 
			"Homozygous viability at P14",
	         "Homozygous Fertility",
	        "Haematology",
	         "Peripheral Blood Leukocytes",
	       "Spleen",
	         "Mesenteric Lymph Node",
	       "Bone Marrow",
	        "Ear Epidermis",
	        "Anti-nuclear Antibodies",
	        "Cytotoxic T Cell Function",
	        "DSS Challenge",
	         "Influenza",
	        "Trichuris Challenge",
	        "Salmonella Challenge"};
	
			
			
			
			private static Map<String,String> createMap() {
	
        Map<String, String> aMap = new HashMap<>();
        aMap.put("Viability Primary Screen", "Homozygous viability at P14");
        aMap.put("Fertility of Homozygous Knock-out Mice", "Homozygous Fertility");
        aMap.put("Haematology", "Haematology");
        aMap.put("Buffy coat peripheral blood leukocyte immunophenotyping", "Peripheral Blood Leukocytes");
        aMap.put("Whole blood peripheral blood leukocyte immunophenotyping", "Peripheral Blood Leukocytes");
        aMap.put("Spleen Immuno Phenotyping","Spleen");
        aMap.put("Mesenteric Lymph Node Immunophenotyping", "Mesenteric Lymph Node");
        aMap.put("Bone marrow immunophenotyping", "Bone Marrow");
        aMap.put("Ear epidermis immunophenotyping", "Ear Epidermis");
        aMap.put("Anti-nuclear antibody assay","Anti-nuclear Antibodies");
        aMap.put("Antigen Specific Immunoglobulin Assay", "Salmonella Challenge");
        aMap.put("Salmonella Challenge", "Salmonella Challenge");
        aMap.put("CTL assay","Cytotoxic T Cell Function");
        aMap.put("3i DSS Challenge","DSS Challenge");
        aMap.put("Infection Challenge Weights", "Influenza");
        aMap.put("3i Trichurus Challenge","Trichuris Challenge");
        return Collections.unmodifiableMap(aMap);
    }
		public static String getDisplayNameForProcedure(String procedureName){
			if(realProcedureTodDisplayName.containsKey(procedureName)){
			realProcedureTodDisplayName.get(procedureName);
			}else{
				System.err.println("procedureName not found in Real Procedure to display Map "+procedureName);
			}
			return realProcedureTodDisplayName.get(procedureName);
		}
	
	
	public static String[] getDisplayHeaderOrder(){
		return displayHeaderOrder;
	}
	
	public static List<String> getProceduresFromDisplayName(String displayName){
		List<String> procedures=new ArrayList<>();
		for(Entry<String, String> entry:realProcedureTodDisplayName.entrySet()){
			if(displayName.equalsIgnoreCase(entry.getValue())){
			procedures.add(entry.getKey());
			}
		}
		
//		Set<T> keys = new HashSet<T>();
//	    for (Entry<T, E> entry : map.entrySet()) {
//	        if (Objects.equals(value, entry.getValue())) {
//	            keys.add(entry.getKey());
//	        }
//	    }
		
		return procedures;
	}
//	private class ProcedureBean{
//		
//		
//		
//	}
}
