package com.h2v.java.comparator.reflect.reader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JavaCompiledFileReader {

	private URLClassLoader cl;
	private JarFile file;

	public JavaCompiledFileReader(String jarLoc) throws IOException {
		file = new JarFile(jarLoc);
		URL[] urls = { new URL("jar:file:" + jarLoc + "!/") };
		cl = URLClassLoader.newInstance(urls);
	}

	public Map<String, Class<?>> getClassFiles() throws Exception {
		Map<String, Class<?>> classFiles = new HashMap<String, Class<?>>();
		Enumeration<JarEntry> entries = file.entries();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			if (jarEntry.getName().endsWith(".class")) {
				String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
				className = className.replaceFirst("WEB-INF/classes/", "").replace('/', '.');
				classFiles.put(className, cl.loadClass(className));
			}
		}
		return classFiles;
	}

	public JarFile getFile() {
		return file;
	}

	public URLClassLoader getCl() {
		return cl;
	}

}
