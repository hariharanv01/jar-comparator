/*package com.h2v.java.comparator.reader.filter;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.regex.Pattern;

import com.h2v.java.comparator.reflect.reader.JavaCompiledFileReader;

public class AnnotationFilter {

	private Set<String> annotations;
	private JavaCompiledFileReader compiledFileReader;

	public AnnotationFilter(Set<String> annotations, JavaCompiledFileReader compiledFileReader) {
		if (annotations == null) {
			this.annotations = Collections.emptySet();
		} else {
			this.annotations = annotations;
		}
		this.compiledFileReader = compiledFileReader;
	}

	public boolean isValid(JarEntry elem) {
		String name = elem.getName().replace('/', '.');
		for (String string : annotations) {
			Pattern pattern = Pattern.compile("(.)*" + string);
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return annotations.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public boolean isValid(Class<?> clazz) {
		if (isEmpty())
			return true;
		for (String annotation : annotations) {
			try {
				if (clazz.isAnnotationPresent(
						(Class<? extends Annotation>) compiledFileReader.getCl().loadClass(annotation))) {
					return true;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
*/