package uk.ac.ebi.threei.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class CellHeatmapRow {

	// 1 not enough data yet
	// 2 not significantly different from WT calls
	// 3 high sig
	// 4 no data call it 0 before display as ranks senisbly then
	@Id
	private String id;
	private String gene;
	private String construct;
	Map<String, Integer> procedureSignificance = new HashMap<>();// map of procedure name to significance string.
	
	//hard coding these so we can use spring ordering easily - seems wierd but we already have the columns hard coded for order and display name anyway so not flexible already.

	
	private Integer alphaDeltaTCells=0;
	private Integer nKCells=0;
	private Integer nktCells=0;
	private Integer bCellPrecursors=0;
	private Integer dendriticCells=0;
	private Integer granulocytes=0;
	private Integer tregCells=0;
	private Integer cD4TCells=0;
	private Integer monocytesMacrophages=0;
	private Integer totalAlphBetaTCells=0;
	private Integer bCells=0;
	private Integer cd8TCells=0;
	
	//these are procedures but 3i people want these shown on the cell view- we just copy them in the data loader from the procedures
	private Integer dSSChallenge=0;
	private Integer influenza=0;
	private Integer trichurisChallenge=0;
	private Integer salmonellaChallenge=0;
	public Integer getdSSChallenge() {
		return dSSChallenge;
	}

	public void setdSSChallenge(Integer dSSChallenge) {
		this.dSSChallenge = dSSChallenge;
	}

	public Integer getInfluenza() {
		return influenza;
	}

	public void setInfluenza(Integer influenza) {
		this.influenza = influenza;
	}

	public Integer getTrichurisChallenge() {
		return trichurisChallenge;
	}

	public void setTrichurisChallenge(Integer trichurisChallenge) {
		this.trichurisChallenge = trichurisChallenge;
	}

	public Integer getSalmonellaChallenge() {
		return salmonellaChallenge;
	}

	public void setSalmonellaChallenge(Integer salmonellaChallenge) {
		this.salmonellaChallenge = salmonellaChallenge;
	}

	
	public Integer getAlphaDeltaTCells() {
		return alphaDeltaTCells;
	}

	public void setAlphaDeltaTCells(Integer alphaDeltaTCells) {
		this.alphaDeltaTCells = alphaDeltaTCells;
	}

	public Integer getnKCells() {
		return nKCells;
	}

	public void setnKCells(Integer nKCells) {
		this.nKCells = nKCells;
	}

	public Integer getNktCells() {
		return nktCells;
	}

	public void setNktCells(Integer nktCells) {
		this.nktCells = nktCells;
	}

	public Integer getbCellPrecursors() {
		return bCellPrecursors;
	}

	public void setbCellPrecursors(Integer bCellPrecursors) {
		this.bCellPrecursors = bCellPrecursors;
	}

	public Integer getDendriticCells() {
		return dendriticCells;
	}

	public void setDendriticCells(Integer dendriticCells) {
		this.dendriticCells = dendriticCells;
	}

	public Integer getGranulocytes() {
		return granulocytes;
	}

	public void setGranulocytes(Integer granulocytes) {
		this.granulocytes = granulocytes;
	}

	public Integer getTregCells() {
		return tregCells;
	}

	public void setTregCells(Integer tregCells) {
		this.tregCells = tregCells;
	}

	public Integer getcD4TCells() {
		return cD4TCells;
	}

	public void setcD4TCells(Integer cD4TCells) {
		this.cD4TCells = cD4TCells;
	}

	public Integer getMonocytesMacrophages() {
		return monocytesMacrophages;
	}

	public void setMonocytesMacrophages(Integer monocytesMacrophages) {
		this.monocytesMacrophages = monocytesMacrophages;
	}

	public Integer getTotalAlphBetaTCells() {
		return totalAlphBetaTCells;
	}

	public void setTotalAlphBetaTCells(Integer totalAlphBetaTCells) {
		this.totalAlphBetaTCells = totalAlphBetaTCells;
	}

	public Integer getbCells() {
		return bCells;
	}

	public void setbCells(Integer bCells) {
		this.bCells = bCells;
	}

	public Integer getCd8TCells() {
		return cd8TCells;
	}

	public void setCd8TCells(Integer cd8tCells) {
		cd8TCells = cd8tCells;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public void setConstruct(String construct) {
		this.construct = construct;
	}

	public Map<String, Integer> getProcedureSignificance() {
		return procedureSignificance;
	}

	public void setProcedureSignificance(Map<String, Integer> procedureSignificance) {
		this.procedureSignificance = procedureSignificance;
	}

	public String getGene() {
		return gene;
	}

	public String getConstruct() {
		return construct;
	}

	public CellHeatmapRow(String gene, String construct) {
		this.gene = gene;
		this.construct = construct;
	}

	@Override
	public String toString() {
		return "HeatmapRow [gene=" + gene + ", construct=" + construct + ", procedureSignificance="
				+ procedureSignificance + "]";
	}
	
	public void setFieldsFromMap() {
		for(String procedureKey:procedureSignificance.keySet()) {
			this.setVarabileFromKey(procedureKey, procedureSignificance.get(procedureKey));
		}
	}
	
//	γδ T cells: 0,
//	NK cells: 0,
//	NKT cells: 0,
//	B cell precursors: 0,
//	Dendritic cells: 0,
//	Granulocytes: 0,
//	Treg cells: 0,
//	CD4 T cells: 0,
//	Monocytes / Macrophages: 0,
//	Total αβ T cells: 0,
//	B cells: 0,
//	CD8 T cells: 0

	private void setVarabileFromKey(String procedureKey, Integer integer) {
		
			switch(procedureKey) {
			case "γδ T cells":
				this.alphaDeltaTCells=integer;
				break;
			case  "NK cells":
				this.nKCells=integer;
				break;
			case "NKT cells":
				this.nktCells=integer;
				break;
			case "B cell precursors":
				this.bCellPrecursors=integer;
				break;
			case  "Dendritic cells":
				this.dendriticCells=integer;
				break;
			case "Granulocytes":
				this.granulocytes=integer;
				break;
			case "Treg cells":
				this.tregCells=integer;
				break;
			case "CD4 T cells":
				this.cD4TCells=integer;
				break;
			case "Monocytes / Macrophages":
				this.monocytesMacrophages=integer;
				break;
			case "Total αβ T cells":
				this.totalAlphBetaTCells=integer;
				break;
			case "B cells":
				this.bCells=integer;
				break;
			case "CD8 T cells":
				this.cd8TCells=integer;
				break;
			case "DSS Challenge":
				this.dSSChallenge=integer;
				break;
			case "Influenza":
				this.influenza=integer;
				break;
			case "Trichuris Challenge":
				this.trichurisChallenge=integer;
				break;
			case "Salmonella Challenge":
				this.salmonellaChallenge=integer;
				break;
			default:
				System.err.println("no variable found for cell type header");
				
			}
			
		
	}
	
	
		
	}

	

