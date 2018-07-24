package uk.ac.ebi.threei.rest.procedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.threei.rest.SignificanceType;


public class ParameterDetails {
	
	private Map<SexType, List<Result>>resultsBySex;
	private String parameterId="";
	
	public String getParameterId() {
		return parameterId;
	}




	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}




	public ParameterDetails(){
		this.resultsBySex=new HashMap<>();
		this.resultsBySex.put(SexType.male, new ArrayList<Result>());
		this.resultsBySex.put(SexType.female, new ArrayList<Result>());
		this.resultsBySex.put(SexType.both, new ArrayList<Result>());
		this.resultsBySex.put(SexType.no_data, new ArrayList<Result>());
	}


    

	public Map<SexType, List<Result>> getResultsBySex() {
		return resultsBySex;
	}

	public void setResultsBySex(Map<SexType, List<Result>> resultsBySex) {
		this.resultsBySex = resultsBySex;
	}

	private String name;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   

    
    
    




	@Override
	public String toString() {
		return "ParameterDetails [resultsBySex=" + resultsBySex + ", parameterId=" + parameterId + ", name=" + name
				+ "]";
	}




	public List<Result> getMaleResults(){
    	return resultsBySex.get(SexType.male);
    }
    
    public List<Result> getFemaleResults(){
    	return this.resultsBySex.get(SexType.female);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ParameterDetails that = (ParameterDetails) o;
//        return Objects.equals(name, that.name) &&
//                maleSignificant == that.maleSignificant &&
//                femaleSignificant == that.femaleSignificant;
//    }

//    @Override
//    public int hashCode() {
//
//        return Objects.hash(name, maleSignificant, femaleSignificant);
//    }

    /**
     * get all results for a parameter in a list
     * @return
     */
	public List<Result> getResults() {
		List<Result> allResults=new ArrayList<>();
		for(SexType key: resultsBySex.keySet()){
			List<Result> sexResults = resultsBySex.get(key);
			allResults.addAll(sexResults);
		}
		
		return allResults;
	}
	/**
	 * used by procedure page to only display data for each header type and not multiples per header
	 * @return
	 */
	public List<Result> getMostSignificantResults() {
		//some parameters have multiple entries for the same sex zyg combination which we just need the most significant one
		List<Result> allResults=new ArrayList<>();
		for(SexType key: resultsBySex.keySet()){
			HashMap<String, Result> zygositySet=new HashMap<String, Result>();
			List<Result> sexResults = resultsBySex.get(key);
			for(Result result: sexResults) {
				
				if(zygositySet.containsKey(result.getHeaderKey())) {
					//System.out.println("headerkey"+result.getHeaderKey());
					zygositySet.put(result.getHeaderKey(), getOnlyMostSignificantResultForHeader(zygositySet, result));
				}else {
					//System.out.println("headerkey added"+result.getHeaderKey() +" sign "+result.getSignificant());
					zygositySet.put(result.getHeaderKey(),result);
				}
				
			}
			allResults.addAll(zygositySet.values());//add all the values for the sex that have had duplicates with different sig values removed to be just the highest significant one.
		}
		return allResults;
	}




	private Result getOnlyMostSignificantResultForHeader(HashMap<String, Result> zygositySet,
			Result result) {
		SignificanceType resSig = result.getSignificant();
		SignificanceType prevSig= zygositySet.get(result.getHeaderKey()).getSignificant();
		//System.out.println(resSig +" "+prevSig);
		if(SignificanceType.getRankFromSignificanceName(resSig.name()) >= SignificanceType.getRankFromSignificanceName(prevSig.name())){
			return result;
		}else {
			return zygositySet.get(result.getHeaderKey());
		}
		
	}
	
	
	/**
	 * get keys that will represent headers male het, female hom etc
	 * @return
	 */
	public Set<String> getHeaderKeysForParameter() {
		Set<String> headerKeys=new TreeSet<>();
		for(SexType key: resultsBySex.keySet()){
			List<Result> sexResults = resultsBySex.get(key);
			for(Result result: sexResults){
				headerKeys.add(result.getHeaderKey());
			}
		}
		
		return headerKeys;
	}

	public void addMaleResult(Result result) {
		this.getMaleResults().add(result);
	}
	
	public void addFemaleResult(Result result){
		this.getFemaleResults().add(result);
	}




	public void addBothSexResult(Result result) {
		this.getBothSexResults().add(result);
		
	}




	public List<Result> getBothSexResults() {
		return this.resultsBySex.get(SexType.both);
	}
	
	
}
