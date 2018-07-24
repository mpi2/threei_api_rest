
package uk.ac.ebi.threei.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

@RepositoryRestResource(collectionResourceRel = "CellHeatmapRow", path = "cellheatmaprows")
public interface CellHeatmapRowsRepository extends MongoRepository<CellHeatmapRow, String> {
	
	
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
	
	
	
	
	//List<HeatmapRow> findByHeatmapType(@Param("heatmapType") String heatmapType);
	
	
	List<CellHeatmapRow> findAll();
	

}
