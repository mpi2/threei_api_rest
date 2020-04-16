package uk.ac.ebi.threei.rest;

import uk.ac.ebi.threei.rest.procedure.SexType;

public class HumanPatientData {
	
	

	String covid;
	
	Integer yearOfBirth;
	
	Integer age;
	
	SexType sex;
	
	String ontologyId;
	
	String clinicalStatus;

	private String clinicalOutcome;
	
	public String getClinicalOutcome() {
		return clinicalOutcome;
	}

	public String getCovid() {
		return covid;
	}

	public void setCovid(String covid) {
		this.covid = covid;
	}

	public Integer getYearOfBirth() {
		return yearOfBirth;
	}

	public void setYearOfBirth(Integer yearOfBirth) {
		this.yearOfBirth = yearOfBirth;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public SexType getSex() {
		return sex;
	}

	public void setSex(SexType sex) {
		this.sex = sex;
	}

	public String getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getClinicalStatus() {
		return clinicalStatus;
	}

	public void setClinicalStatus(String clinicalStatus) {
		this.clinicalStatus = clinicalStatus;
	}

	public void setClinicalOutcome(String outcome) {
		this.clinicalOutcome=outcome;
		
	}

	@Override
	public String toString() {
		return "HumanPatientData [covid=" + covid + ", yearOfBirth=" + yearOfBirth + ", age=" + age + ", sex=" + sex
				+ ", ontologyId=" + ontologyId + ", clinicalStatus=" + clinicalStatus + ", clinicalOutcome="
				+ clinicalOutcome + "]";
	}

}
