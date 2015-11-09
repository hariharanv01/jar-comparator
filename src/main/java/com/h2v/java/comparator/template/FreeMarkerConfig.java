package com.h2v.java.comparator.template;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import com.h2v.java.comparator.reflect.ComparisonResult;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerConfig {

	private Configuration configuration;
	private Template template;

	public FreeMarkerConfig() {
		configuration = new Configuration();
		configuration.clearTemplateCache();
		String templateStr = "<html><head><title>Jar Comparison Result</title></head><body><#list results?keys as result><h2>${result}</h2><table border=1><tr><th>Target</th><th>Target type</th><th>Element</th><th>Element type</th><th>Left Value</th><th>Right Value</th><th>Status</th></tr><#list results[result] as r><tr><td>${r.targetName}</td><td>${r.targetType}</td><td>${r.elementName}</td><td>${r.elementType}</td><td><#if r.leftVal??>${r.leftVal}<#else>'NA'</#if></td><td><#if r.rightVal??>${r.rightVal}<#else>'NA'</#if></td><td>${r.status}</td></tr></#list></table></#list></body></html>";
		try {
			template = new Template("results", templateStr, new Configuration());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String populateData(final Template template,
			final Map<String, Map<String, Set<ComparisonResult>>> res) throws TemplateException, IOException {
		StringWriter writer = new StringWriter();
		template.process(res, writer);
		return writer.toString();
	}

	public Template getTemplate() {
		return template;
	}

}
