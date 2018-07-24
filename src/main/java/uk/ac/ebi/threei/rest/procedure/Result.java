package uk.ac.ebi.threei.rest.procedure;

import uk.ac.ebi.threei.rest.SignificanceType;

public class Result {
		
		@Override
	public String toString() {
		return "Result [sexType=" + sexType + ", zygosityType=" + zygosityType + ", significant=" + significant + "]";
	}

		private SexType sexType;
		private ZygosityType zygosityType;
		private SignificanceType significant;

	    
		public SignificanceType getSignificant() {
			return significant;
		}

		public void setSignificant(SignificanceType significant) {
			this.significant = significant;
		}

		public SexType getSexType() {
			return sexType;
		}

		public void setSexType(SexType sexType) {
			this.sexType = sexType;
		}

		public ZygosityType getZygosityType() {
			return zygosityType;
		}

		public void setZygosityType(ZygosityType zygosityType) {
			this.zygosityType = zygosityType;
		}
		
		public String getHeaderKey(){
			String sex=this.getSexType().getName();
			if(this.getSexType().equals(SexType.both)){
				sex="Male/Female";
			}
			String headerKey=sex+" "+this.getZygosityType();
			return headerKey;
		}

	

}
