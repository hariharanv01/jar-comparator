package com.h2v.java.comparator.reader.filter;

import java.util.Collections;
import java.util.Set;

public class PackageFilter {

	private Set<String> packages;

	public PackageFilter(Set<String> packages) {
		if (packages == null) {
			this.packages = Collections.emptySet();
		} else {
			this.packages = packages;
		}
	}

	public boolean isValid(String classQName) {
		if (packages.isEmpty())
			return true;
		for (String pkg : packages) {
			if (classQName.startsWith(pkg)) {
				return true;
			}
		}
		return false;
	}
}
