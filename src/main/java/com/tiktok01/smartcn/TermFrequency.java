package com.tiktok01.smartcn;

public class TermFrequency {

	private String term;
	private int frequency;
	
	public TermFrequency(String term, int frequency) {
		super();
		this.term = term;
		this.frequency = frequency;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
