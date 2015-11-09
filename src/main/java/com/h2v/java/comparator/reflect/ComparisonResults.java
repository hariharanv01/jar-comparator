package com.h2v.java.comparator.reflect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ComparisonResults {

	private List<ResultGroup> resultGroups = new ArrayList<ResultGroup>();

	public List<ResultGroup> getResultGroups() {
		return resultGroups;
	}

	public void setResultGroups(List<ResultGroup> resultGroups) {
		this.resultGroups = resultGroups;
	}

}

class ResultGroup {

	private String className;
	private Set<ComparisonResult> results = new HashSet<ComparisonResult>();

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Set<ComparisonResult> getResults() {
		return results;
	}

	public void setResults(Set<ComparisonResult> results) {
		this.results = results;
	}

}
