package uk.ac.ebi.threei.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.annotation.Id;

public class ProcedureHeatmapRow {

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

	private Integer homozygousViabilityAtP14;
	private Integer homozygousFertility;
	private Integer haematology;
	private Integer peripheralBloodLeukocytes;
	private Integer spleen;
	private Integer mesentericLymphNode;
	private Integer boneMarrow;
	private Integer earEpidermis;
	private Integer antinuclearAntibodies;
	private Integer cytotoxicTCellFunction;
	private Integer dSSChallenge;
	private Integer influenza;
	private Integer trichurisChallenge;
	private Integer salmonellaChallenge;
	private Integer totalSignificanceScore;

	public Integer getTotalSignificanceScore() {
		return totalSignificanceScore;
	}

	public void setTotalSignificanceScore(Integer totalSignificanceScore) {
		this.totalSignificanceScore = totalSignificanceScore;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getHomozygousViabilityAtP14() {
		return homozygousViabilityAtP14;
	}

	public void setHomozygousViabilityAtP14(Integer homozygousViabilityAtP14) {
		this.homozygousViabilityAtP14 = homozygousViabilityAtP14;
	}

	public Integer getHomozygousFertility() {
		return homozygousFertility;
	}

	public void setHomozygousFertility(Integer homozygousFertility) {
		this.homozygousFertility = homozygousFertility;
	}

	public Integer getHaematology() {
		return haematology;
	}

	public void setHaematology(Integer haematology) {
		this.haematology = haematology;
	}

	public Integer getPeripheralBloodLeukocytes() {
		return peripheralBloodLeukocytes;
	}

	public void setPeripheralBloodLeukocytes(Integer peripheralBloodLeukocytes) {
		this.peripheralBloodLeukocytes = peripheralBloodLeukocytes;
	}

	public Integer getSpleen() {
		return spleen;
	}

	public void setSpleen(Integer spleen) {
		this.spleen = spleen;
	}

	public Integer getMesentericLymphNode() {
		return mesentericLymphNode;
	}

	public void setMesentericLymphNode(Integer mesentericLymphNode) {
		this.mesentericLymphNode = mesentericLymphNode;
	}

	public Integer getBoneMarrow() {
		return boneMarrow;
	}

	public void setBoneMarrow(Integer boneMarrow) {
		this.boneMarrow = boneMarrow;
	}

	public Integer getEarEpidermis() {
		return earEpidermis;
	}

	public void setEarEpidermis(Integer earEpidermis) {
		this.earEpidermis = earEpidermis;
	}

	public Integer getAntinuclearAntibodies() {
		return antinuclearAntibodies;
	}

	public void setAntinuclearAntibodies(Integer antinuclearAntibodies) {
		this.antinuclearAntibodies = antinuclearAntibodies;
	}

	public Integer getCytotoxicTCellFunction() {
		return cytotoxicTCellFunction;
	}

	public void setCytotoxicTCellFunction(Integer cytotoxicTCellFunction) {
		this.cytotoxicTCellFunction = cytotoxicTCellFunction;
	}

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

	public static String[] getProceduredisplayheaderorder() {
		return procedureDisplayHeaderOrder;
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

	public ProcedureHeatmapRow(String gene, String construct) {
		this.gene = gene;
		this.construct = construct;
	}

	public ProcedureHeatmapRow() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "HeatmapRow [gene=" + gene + ", construct=" + construct + ", procedureSignificance="
				+ procedureSignificance + "]";
	}
	
	public void setFieldsFromMap() {
		for(String procedureKey:procedureSignificance.keySet()) {
			this.setVariableFromKey(procedureKey, procedureSignificance.get(procedureKey));
		}
	}

	public  void setVariableFromKey(String procedureKey, Integer integer) {
		switch(procedureKey) {
		case "Homozygous viability at P14":
			this.homozygousViabilityAtP14=integer;
			break;
		case  "Homozygous Fertility":
			this.homozygousFertility=integer;
			break;
		case "Haematology":
			this.haematology=integer;
		case "Peripheral Blood Leukocytes":
			this.peripheralBloodLeukocytes=integer;
			break;
		case  "Spleen":
			this.spleen=integer;
			break;
		case "Mesenteric Lymph Node":
			this.mesentericLymphNode=integer;
			break;
		case "Bone Marrow":
			this.boneMarrow=integer;
			break;
		case "Ear Epidermis":
			this.earEpidermis=integer;
			break;
		case "Anti-nuclear Antibodies":
			this.antinuclearAntibodies=integer;
			break;
		case "Cytotoxic T Cell Function":
			this.cytotoxicTCellFunction=integer;
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
		
		}
		
	}

	
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



public void calculateTotalSignificantScore() {
	Integer score=0;
	for( Entry<String, Integer> entry: this.procedureSignificance.entrySet()) {
		if(entry.getValue()>2) {//limit it to red boxes as we don't care if less or more grey ones???
		score+=entry.getValue();
		}
	}
	
	this.totalSignificanceScore=score;
}

}
