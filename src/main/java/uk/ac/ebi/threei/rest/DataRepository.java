
package uk.ac.ebi.threei.rest;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "Data", path = "data")
public interface DataRepository extends MongoRepository<Data, String> {

	public static final String[] procedureDisplayHeaderOrder={ 
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
	/**
	 * either procedure or cell type data array for the heeatmap
	 * @param heatmapType
	 * @return
	 */
	List<Data> findByHeatmapType(@Param("heatmapType") String heatmapType);

}
