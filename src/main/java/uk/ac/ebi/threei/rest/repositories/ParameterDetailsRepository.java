
package uk.ac.ebi.threei.rest.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import uk.ac.ebi.threei.rest.Data;
import uk.ac.ebi.threei.rest.procedure.ParameterDetails;

@RepositoryRestResource(collectionResourceRel = "ParameterDetails", path = "parameter_details")
public interface ParameterDetailsRepository extends MongoRepository<ParameterDetails, String> {


	List<ParameterDetails> findAll();

}
