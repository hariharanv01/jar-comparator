package com.h2v.java.comparator.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ComparatorConfig {

	private Set<String> packages = Collections.emptySet();

	public ComparatorConfig() {
		String pkgList = System.getProperty("config.pkgs");
		if (pkgList != null && !pkgList.isEmpty()) {
			packages = new HashSet<String>(Arrays.asList(pkgList.split(",")));
		}
	}

	public Set<String> getPackages() {
		return packages;
	}

}
