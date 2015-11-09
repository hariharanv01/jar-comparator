package com.h2v.java.comparator.reflect;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.h2v.java.comparator.config.ComparatorConfig;
import com.h2v.java.comparator.reader.filter.PackageFilter;
import com.h2v.java.comparator.reflect.reader.JavaCompiledFileReader;
import com.h2v.java.comparator.template.FreeMarkerConfig;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class JarComparator {

	private static ComparatorConfig config;
	private static FreeMarkerConfig markerConfig = new FreeMarkerConfig();

	public static void main(final String[] args) throws Exception {
		if (args.length < 2) {
			printUsage();
			return;
		} else {
			config = new ComparatorConfig();
			if (args.length == 2) {
				Map<String, Set<ComparisonResult>> results = compare(args[0], args[1]);
				for (Set<ComparisonResult> res : results.values()) {
					for (ComparisonResult comparisonResult : res) {
						System.out.println(comparisonResult);
					}
				}
			} else {
				Path path = Paths.get(args[2]);
				if (!Files.exists(path)) {
					path = Files.createFile(path);
				}
				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(path.toFile());
					if (path.toString().endsWith(".xml")) {
						getResultXML(args[0], args[1], outputStream);
					} else if (path.toString().endsWith(".html")) {
						getResultHTML(args[0], args[1], outputStream);
					}
				} finally {
					if (outputStream != null)
						outputStream.close();
				}
			}
		}
	}

	public static void getResultHTML(String jar1, String jar2, OutputStream outputStream)
			throws IOException, TemplateException {
		Map<String, Set<ComparisonResult>> results = compare(jar1, jar2);
		Template template = markerConfig.getTemplate();
		Map<String, Map<String, Set<ComparisonResult>>> res = new HashMap<String, Map<String, Set<ComparisonResult>>>();
		res.put("results", results);
		String populatedData = FreeMarkerConfig.populateData(template, res);
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new OutputStreamWriter(outputStream));
			w.write(populatedData);
		} finally {
			if (w != null)
				w.close();
		}
	}

	public static void getResultXML(String jar1, String jar2, OutputStream outputStream) throws Exception {
		JAXBContext context = JAXBContext.newInstance(ComparisonResults.class);
		Marshaller marshaller = context.createMarshaller();
		Map<String, Set<ComparisonResult>> results = compare(jar1, jar2);
		ComparisonResults result = new ComparisonResults();
		for (Entry<String, Set<ComparisonResult>> entry : results.entrySet()) {
			ResultGroup group = new ResultGroup();
			group.setClassName(entry.getKey());
			group.setResults(entry.getValue());
			result.getResultGroups().add(group);
		}
		marshaller.marshal(result, outputStream);
	}

	public static Map<String, Set<ComparisonResult>> compare(String jar1, String jar2) {
		ExecutorService executor = null;
		Map<String, Set<ComparisonResult>> results = null;
		try {
			Comparer comparer = new Comparer();
			JavaCompiledFileReader reader1 = new JavaCompiledFileReader(jar1);
			JavaCompiledFileReader reader2 = new JavaCompiledFileReader(jar2);
			executor = Executors.newFixedThreadPool(2);
			Future<Map<String, Class<?>>> future1 = executor.submit(new FileReaderTask(reader1));
			Future<Map<String, Class<?>>> future2 = executor.submit(new FileReaderTask(reader2));
			Map<String, Class<?>> classes1 = future1.get();
			Map<String, Class<?>> classes2 = future2.get();
			results = comparer.compare(new PackageFilter(config.getPackages()), classes1, classes2);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executor != null)
				executor.shutdown();
		}
		return results;
	}

	private static void printLine() {
		System.out.println("#################################################################################");
	}

	private static class FileReaderTask implements Callable<Map<String, Class<?>>> {

		private JavaCompiledFileReader compFileReader;

		public FileReaderTask(JavaCompiledFileReader compFileReader) {
			this.compFileReader = compFileReader;
		}

		@Override
		public Map<String, Class<?>> call() throws Exception {
			return compFileReader.getClassFiles();
		}
	}

	private static void printUsage() {
		System.out.println("Usage:");
		printLine();
		System.out.println(
				"java [-Dconfig.pks=Comma separted package list] -jar <location to JarComparator jar> compiled-jar1 compiled-jar2 [output XML file]");
	}
}
