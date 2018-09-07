
package uk.ac.ebi.threei.rest.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uk.ac.ebi.threei.rest.CellParameter;

@RepositoryRestResource(collectionResourceRel = "cell", path = "cell")
public interface CellParameterRepository extends MongoRepository<CellParameter, String> {

	List<CellParameter> findByParameterId(@Param("parameterId") String parameterId);
	
	List<CellParameter> findByCellType(@Param("cellType") String cellType);

}
