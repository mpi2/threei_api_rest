
package uk.ac.ebi.threei.rest.repositories;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import uk.ac.ebi.threei.rest.CellHeatmapRow;

@RepositoryRestResource(collectionResourceRel = "CellHeatmapRow", path = "cellheatmaprows")
public interface CellHeatmapRowsRepository extends  MongoRepository<CellHeatmapRow, String>{
	
	
	public static final String[] cellDisplayHeaderOrder={ 
			"γδ T cells",
			"NK cells",
			"NKT cells",
			"B cell precursors",
			"Dendritic cells",
			"Granulocytes",
			"Treg cells",
			"CD4 T cells",
			"Monocytes / Macrophages",
			"Total αβ T cells",
			"B cells",
			"CD8 T cells",
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
	
	
	List<CellHeatmapRow> findAll(Sort sort);
	
	
	 
}
