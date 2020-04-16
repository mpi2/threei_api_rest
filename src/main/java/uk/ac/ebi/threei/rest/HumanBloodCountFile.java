package uk.ac.ebi.threei.rest;

import java.util.List;

public class HumanBloodCountFile {
	
	private String file;
	
	private List<String> headers;//after cell header as this is part of core line object
	
	

	public HumanBloodCountFile(String file, List<String> headers) {
		super();
		this.file = file;
		this.headers = headers;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	
	

}
