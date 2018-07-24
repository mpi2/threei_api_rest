package uk.ac.ebi.threei.rest.services;

import org.apache.solr.client.solrj.SolrServerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import uk.ac.ebi.threei.rest.DisplayProcedureMapper;
import uk.ac.ebi.threei.rest.SignificanceType;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.procedure.Result;
import uk.ac.ebi.threei.rest.procedure.SexType;
import uk.ac.ebi.threei.rest.procedure.ZygosityType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DetailsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String query = "SELECT ParameterName, ParameterId, Gender, Genotype, CallType FROM threei_data_for_heat_map WHERE Gene = ? AND ProcedureName = ? AND Construct like  ?";
    //private DataSource dataSource;

	private GeneService geneService;

    public DetailsService( GeneService geneService) {
        //Assert.notNull(dataSource, "Data source cannot be null");
        //this.dataSource = dataSource;
        this.geneService=geneService;
    }

    
    
    
    public Set<ParameterDetails> getParametersForGeneAndDisplayName(String gene, String construct, String displayName){
    	Map<String, Set<ParameterDetails>> procedureDetailsMap=new HashMap<>();
    	Set<ParameterDetails> combinedSet=new HashSet<>();
    	displayName=displayName.replaceAll("\"", "");
        List<String> procedureList=DisplayProcedureMapper.getProceduresFromDisplayName(displayName);
        for(String procedureName: procedureList){
        	//combinedSet.addAll(this.getParametersForGeneAndProcedure(gene, procedureName));
        	procedureDetailsMap.put(procedureName, this.getParametersForGeneAndProcedure(gene, construct, procedureName));
        }
        
        if(procedureDetailsMap.containsKey("Whole blood peripheral blood leukocyte immunophenotyping")){
        	if(procedureDetailsMap.get("Whole blood peripheral blood leukocyte immunophenotyping").size()>0){
        		System.out.println("whole blood found so just returning whole blood");
        		return procedureDetailsMap.get("Whole blood peripheral blood leukocyte immunophenotyping");
        	}else{
        		if(procedureDetailsMap.containsKey("Buffy coat peripheral blood leukocyte immunophenotyping")){
        			return procedureDetailsMap.get("Buffy coat peripheral blood leukocyte immunophenotyping");
        		}
        	}
        }else{
        	//if not blood related just add results together
        	for(String key: procedureDetailsMap.keySet()){
        		combinedSet.addAll(procedureDetailsMap.get(key));
        	}
        }
        
        return combinedSet;
    }
    
    
    
    /*
     * procedureName is now the blessed ones from Lucie and so we need to map these back to the real procedure names before giving the page
     */
    public Set<ParameterDetails> getParametersForGeneAndProcedure(String gene, String construct, String procedureName) {

        Map<String, ParameterDetails> parameters = new HashMap<>();
        //strip quotes off procedureName
        
        procedureName=procedureName.replaceAll("\"", "");
        //System.out.println("procedureName="+procedureName);
        construct=construct.replaceAll("\"", "");
		

			try (Connection conn = dataSource.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
				statement.setString(1, gene);
				statement.setString(2, procedureName);
				statement.setString(3, construct+"%");

				//System.out.println(statement);

				ResultSet r = statement.executeQuery();

				while (r.next()) {

					String parameter = r.getString("ParameterName");
					if (parameter == null || parameter.equals("")) {
						parameter = "Viability Primary Screen";// seems like all
																// the ones with
																// empty
																// parameter
																// name ar
																// viabiility??
					}

					// If we have not seen this parameter yet, add a new
					// ParameterDetails object to the map
					if (!parameters.containsKey(parameter)) {
						ParameterDetails p = new ParameterDetails();
						p.setName(parameter);
						parameters.put(parameter, p);
					}

					ParameterDetails p = parameters.get(parameter);
					String parameterId = r.getString("ParameterId");
					p.setParameterId(parameterId);
					String callType = r.getString("CallType");
					SignificanceType sig = SignificanceType.fromString(callType);
					String gender = r.getString("Gender").toLowerCase();
					//System.out.println(callType + " " + gender);
					String zygosity = r.getString("genotype");

					Result result = new Result();
					result.setZygosityType(ZygosityType.valueOf(zygosity));
					result.setSignificant(sig);
					if (gender.equals("male")) {
						result.setSexType(SexType.male);
						p.addMaleResult(result);
					}

					if (gender.toLowerCase().equals("female")) {
						result.setSexType(SexType.female);
						p.addFemaleResult(result);
						// p.setMaleSignificant(SignificanceType.not_significant);
					}
					if (gender.toLowerCase().equals("both")) {
						result.setSexType(SexType.both);
						p.addBothSexResult(result);
					}

				}
			} catch (Exception e) {
				logger.info("Sql exception when getting details for gene %s, procedure %s", gene, procedureName, e);
				e.printStackTrace();
			}
		System.out.println(parameters.size());
        return new HashSet<>(parameters.values());

    }

	public String getAccessionForGene(String gene) {
		String accession="";
        try {
			accession=this.getMgiAccessionFromGeneSymbol(gene);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SolrServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        System.out.println("accession in detail service="+accession);
		return accession;
	}
    
    private String getMgiAccessionFromGeneSymbol(String geneSymbol) throws IOException, SolrServerException{
    	return geneService.getMgiAccessionFromGeneSymbol(geneSymbol);
    }





}
