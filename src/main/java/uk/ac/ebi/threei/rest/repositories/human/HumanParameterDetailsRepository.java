
package uk.ac.ebi.threei.rest.repositories.human;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uk.ac.ebi.threei.rest.procedure.ParameterDetails;

@RepositoryRestResource(collectionResourceRel = "HumanParameterDetails", path = "parameter_details")
public interface HumanParameterDetailsRepository extends MongoRepository<ParameterDetails, String> {


	List<ParameterDetails> findAll();

	//http://localhost:8080/parameter_details/search/findByGeneAndDisplayProcedureName?gene=Adal&procedure=Haematology
	List<ParameterDetails> findByGeneAndDisplayProcedureName(@Param(value = "gene") String gene,@Param(value = "procedure") String procedure);

	//http://localhost:8080/parameter_details/search/findByGene?gene=Adal
	List<ParameterDetails> findByGene(@Param(value = "gene") String gene);

}
