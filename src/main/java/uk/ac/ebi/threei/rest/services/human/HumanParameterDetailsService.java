package uk.ac.ebi.threei.rest.services.human;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.repositories.ParameterDetailsRepository;
import uk.ac.ebi.threei.rest.repositories.human.HumanParameterDetailsRepository;

@Service
public class HumanParameterDetailsService {

	@Autowired
	HumanParameterDetailsRepository parameterDetailsRepository;
	
	public List<ParameterDetails> getParameterDetailsByGeneAndProcedureAndConstruct(String gene, String procedure, String construct) {
		List<ParameterDetails> parameterDetails=new ArrayList<>();
		List<ParameterDetails> filteredDetails=new ArrayList<>();
		if(procedure!=null) {
		parameterDetails=parameterDetailsRepository.findByGeneAndDisplayProcedureName(gene, procedure);
		}else {
			parameterDetails=parameterDetailsRepository.findByGene(gene);
		}
		if(construct!=null) {
		for(ParameterDetails p: parameterDetails) {
			if(p.getConstruct().replaceAll("\"", "").startsWith(construct)){
				filteredDetails.add(p);
			}
		}
		}
			filteredDetails=parameterDetails;
		
		return filteredDetails;
	}
	
	public List<ParameterDetails> getParameterDetailsByGene(String gene) {
		List<ParameterDetails> parameterDetails=new ArrayList<>();
		parameterDetails=parameterDetailsRepository.findByGene(gene);
		return parameterDetails;
	}
}
