package uk.ac.ebi.threei.rest;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * Stores data from a line in the blood counts file - data array should be matched with an enclosing object containing headers
 * @author jwarren
 *
 */
public class BloodCountLine {
	
	@Id
	private String id; 
	
	private String sampleId;
	
	private String file;
	
	private Long cells;
	
	List<Long> counts;//should correspond with headers need to check the length is always same as headers?

	public BloodCountLine() {
		super();
	}

	public BloodCountLine(String id, String sampleId, String file, Long cells, List<Long> counts) {
		super();
		this.id = id;
		this.sampleId = sampleId;
		this.file = file;
		this.cells = cells;
		this.counts = counts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Long getCells() {
		return cells;
	}

	public void setCells(Long cells) {
		this.cells = cells;
	}

	public List<Long> getCounts() {
		return counts;
	}

	public void setCounts(List<Long> counts) {
		this.counts = counts;
	}

	@Override
	public String toString() {
		return "BloodCountLine [id=" + id + ", sampleId=" + sampleId + ", file=" + file + ", cells=" + cells
				+ ", counts=" + counts + "]";
	}
	
	
	

}
