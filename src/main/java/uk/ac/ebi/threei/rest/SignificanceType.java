package uk.ac.ebi.threei.rest;

public enum SignificanceType {

    // 1 not enough data yet
    // 2 not significantly different from WT calls
    // 3 high sig
    // 4 no data call it 0 before display as ranks sensibly then


    not_significant("Not Significant"),//grey
    pending("Pending"),//blue
    significant("Significant"),//red
    no_data("Not performed or applicable"),
    early_indication("Early indication of possible phenotype");//white
	
    private final String label;
    
    public String getLabel() {
		return label;
	}


	SignificanceType(String label) {
        this.label=label;
    }



    public static SignificanceType fromString(String manualCall) {
        if (manualCall != null) {
            for (SignificanceType b : SignificanceType.values()) {
                if (manualCall.equalsIgnoreCase(b.label)) {
                    return b;
                }
            }
        }
        return no_data;
    }
    
    public static Integer getRankFromSignificanceName(String significanceString) {

        //1 not enough data yet
        //2 not significantly different from WT calls
        //3 high sig
        //4 or 0 no data, call it 0 before display as ranks senisbly then
        int rank = 0;

        if (significanceString == null) {
            return 0;
        }

        switch (significanceString.toLowerCase()) {
            case "not significant":
                rank = 2;
                break;
                //if 4 then call it 0 and change all 0s to 4s at the end when creating rows.
            case "pending":
                rank = 1;
                break;
            case "not performed or applicable":
                rank = 1;
                break;
            case "significant":
                rank = 3;
                break;
            case "early indication of possible phenotype":
                rank = 1;
                break;
            default:
                rank = 0;
                break;
        }

        return rank;

    }


}
