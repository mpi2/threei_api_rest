package uk.ac.ebi.threei.rest.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.threei.rest.procedure.ParameterDetails;
import uk.ac.ebi.threei.rest.repositories.ParameterDetailsRepository;

@Service
public class ParameterDetailsService {

	@Autowired
	ParameterDetailsRepository parameterDetailsRepository;
	
	public List<ParameterDetails> getParameterDetails(String gene, String construct, String procedure) {
		List<ParameterDetails> parameterDetails=new ArrayList<>();
		parameterDetails=parameterDetailsRepository.findByGeneAndConstructLikeAndDisplayProcedureName(gene, construct, procedure);
		return parameterDetails;
	}
}
