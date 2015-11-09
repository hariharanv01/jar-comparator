package com.h2v.java.comparator.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.h2v.java.comparator.reader.filter.PackageFilter;
import com.h2v.java.comparator.reflect.ComparisonResult.ComparisonStatus;
import com.h2v.java.comparator.reflect.ComparisonResult.TargetType;

public class Comparer {

	public Map<String, Set<ComparisonResult>> compare(PackageFilter filter, Map<String, Class<?>> classes1,
			Map<String, Class<?>> classes2) throws Exception {
		Map<String, Set<ComparisonResult>> res = new HashMap<String, Set<ComparisonResult>>();
		for (Entry<String, Class<?>> entry : classes1.entrySet()) {
			if (filter.isValid(entry.getKey())) {
				if (!classes2.containsKey(entry.getKey())) {
					ComparisonResult result = new ComparisonResult();
					result.setElementName(entry.getKey());
					result.setElementType(TargetType.CLASS);
					result.setStatus(ComparisonStatus.DELETED);
					setTargetDetails(entry, result);
					if (!res.containsKey(entry.getKey())) {
						res.put(entry.getKey(), new HashSet<ComparisonResult>());
					}
					res.get(entry.getKey()).add(result);
				} else {
					Class<?> clazz = entry.getValue();
					Set<ComparisonResult> annRes = compareAnnotations(clazz, clazz.getDeclaredAnnotations(),
							classes2.get(entry.getKey()).getDeclaredAnnotations(), TargetType.CLASS, entry.getKey());
					if (!annRes.isEmpty()) {
						res.put(entry.getKey(), annRes);
					}
					Set<ComparisonResult> methRes = compareMethods(clazz, clazz.getDeclaredMethods(),
							classes2.get(entry.getKey()).getDeclaredMethods());
					if (!methRes.isEmpty()) {
						if (!res.containsKey(entry.getKey())) {
							res.put(entry.getKey(), methRes);
						} else {
							res.get(entry.getKey()).addAll(methRes);
						}
					}
				}
			}
		}
		for (Entry<String, Class<?>> entry : classes2.entrySet()) {
			if (filter.isValid(entry.getKey())) {
				if (!classes1.containsKey(entry.getKey())) {
					ComparisonResult result = new ComparisonResult();
					result.setElementName(entry.getKey());
					result.setElementType(TargetType.CLASS);
					setTargetDetails(entry, result);
					result.setStatus(ComparisonStatus.ADDED);
					if (!res.containsKey(entry.getKey())) {
						res.put(entry.getKey(), new HashSet<ComparisonResult>());
					}
					res.get(entry.getKey()).add(result);
				}
			}
		}
		return res;
	}

	private void setTargetDetails(Entry<String, Class<?>> entry, ComparisonResult result) {
		if (entry.getValue().isLocalClass() || entry.getValue().isMemberClass()
				|| entry.getValue().isAnonymousClass()) {
			if (entry.getValue().getEnclosingMethod() != null) {
				result.setTargetName(entry.getValue().getEnclosingMethod().toGenericString());
				result.setTargetType(TargetType.METHOD);
			} else {
				result.setTargetName(entry.getValue().getEnclosingClass().getName());
				result.setTargetType(TargetType.CLASS);
			}
		} else {
			result.setTargetName(entry.getKey());
			result.setTargetType(TargetType.CLASS);
		}
	}

	private Set<ComparisonResult> compareMethods(Class<?> clazz, Method[] m1, Method[] m2) throws Exception {
		if (m1.length == 0 && m2.length == 0) {
			return Collections.emptySet();
		} else {
			Set<ComparisonResult> set = new HashSet<ComparisonResult>();
			Map<String, Method> map1 = convertToMap(m1);
			Map<String, Method> map2 = convertToMap(m2);
			for (Entry<String, Method> method : map1.entrySet()) {
				method.getValue().setAccessible(true);
				if (!map2.containsKey(method.getKey())) {
					ComparisonResult result = new ComparisonResult();
					result.setTargetName(clazz.getName());
					result.setTargetType(TargetType.CLASS);
					result.setElementName(method.getKey());
					result.setElementType(TargetType.METHOD);
					result.setStatus(ComparisonStatus.DELETED);
					set.add(result);
					continue;
				}
				// TODO: Is there any way to check if a method parameter list is
				// modified
				else if (!Arrays.deepToString(method.getValue().getParameterTypes())
						.equals(Arrays.deepToString(map2.get(method.getKey()).getParameterTypes()))) {
					ComparisonResult result = new ComparisonResult();
					result.setTargetName(clazz.getName());
					result.setTargetType(TargetType.CLASS);
					result.setElementName(method.getKey());
					result.setElementType(TargetType.METHOD);
					result.setLeftVal(Arrays.deepToString(method.getValue().getParameterTypes()));
					result.setRightVal(Arrays.deepToString(map2.get(method.getKey()).getParameterTypes()));
					result.setStatus(ComparisonStatus.MODIFIED);
					set.add(result);
				} else {
					set.addAll(compareAnnotations(clazz, method.getValue().getDeclaredAnnotations(),
							map2.get(method.getKey()).getDeclaredAnnotations(), TargetType.METHOD, method.getKey()));
					Annotation[][] a2 = map2.get(method.getKey()).getParameterAnnotations();
					int i = 0;
					for (Annotation[] a : method.getValue().getParameterAnnotations()) {
						set.addAll(compareAnnotations(clazz, a, a2[i++], TargetType.METHOD, method.getKey()));
					}
				}
			}
			for (Entry<String, Method> method : map2.entrySet()) {
				if (!map1.containsKey(method.getKey())) {
					ComparisonResult result = new ComparisonResult();
					result.setTargetName(clazz.getName());
					result.setTargetType(TargetType.CLASS);
					result.setElementName(method.getKey());
					result.setElementType(TargetType.METHOD);
					result.setStatus(ComparisonStatus.ADDED);
					set.add(result);
				}
			}
			return set;
		}
	}

	private Set<ComparisonResult> compareAnnotations(Class<?> clazz, Annotation[] anno1, Annotation[] anno2,
			TargetType targetType, String targetName) throws Exception {
		if (anno1.length == 0 && anno2.length == 0) {
			return Collections.emptySet();
		}
		Set<ComparisonResult> set = new HashSet<ComparisonResult>();
		Map<String, Annotation> map1 = convertToMap(anno1);
		Map<String, Annotation> map2 = convertToMap(anno2);
		for (Entry<String, Annotation> ann : map1.entrySet()) {
			if (!map2.containsKey(ann.getKey())) {
				ComparisonResult result = new ComparisonResult();
				result.setElementName(ann.getKey());
				result.setElementType(TargetType.ANNOTATION);
				result.setTargetName(targetName);
				result.setTargetType(targetType);
				result.setStatus(ComparisonStatus.DELETED);
				set.add(result);
			} else {
				Annotation ann2 = map2.get(ann.getKey());
				Map<String, Pair<String, String>> methodMap = checkAnnotationEquality(ann.getValue(), ann2);
				if (!methodMap.isEmpty()) {
					ComparisonResult result = new ComparisonResult();
					result.setElementName(ann.getKey());
					result.setElementType(TargetType.ANNOTATION);
					result.setTargetName(targetName);
					result.setTargetType(targetType);
					String key = methodMap.keySet().iterator().next();
					result.setLeftVal(methodMap.get(key).getA());
					result.setRightVal(methodMap.get(key).getB());
					result.setStatus(ComparisonStatus.MODIFIED);
					set.add(result);
				}
			}
		}
		for (String annotation : map2.keySet()) {
			if (!map1.containsKey(annotation)) {
				ComparisonResult result = new ComparisonResult();
				result.setElementName(annotation);
				result.setElementType(TargetType.ANNOTATION);
				result.setTargetName(targetName);
				result.setTargetType(targetType);
				result.setStatus(ComparisonStatus.ADDED);
				set.add(result);
			}
		}
		return set;
	}

	private <T> Map<String, T> convertToMap(T[] t) {
		Map<String, T> map = new HashMap<String, T>();
		for (T temp : t) {
			if (temp instanceof Annotation) {
				Annotation annotation = (Annotation) temp;
				map.put(annotation.annotationType().getCanonicalName(), temp);
			} else if (temp instanceof Method) {
				map.put(((Method) temp).toGenericString(), temp);
			}
		}
		return map;
	}

	private Map<String, Pair<String, String>> checkAnnotationEquality(Annotation a1, Annotation a2) throws Exception {
		Map<String, Pair<String, String>> r = new HashMap<String, Pair<String, String>>();
		Map<String, String> valuesMap1 = getValuesMap(a1);
		Map<String, String> valuesMap2 = getValuesMap(a2);
		for (Entry<String, String> e : valuesMap1.entrySet()) {
			if (!valuesMap2.containsKey(e.getKey())) {
				r.put(e.getKey(), new Pair<String, String>(e.getValue(), valuesMap2.get(e.getKey())));
			} else {
				if (!valuesMap1.get(e.getKey()).equals(valuesMap2.get(e.getKey()))) {
					r.put(e.getKey(), new Pair<String, String>(e.getValue(), valuesMap2.get(e.getKey())));
				}
			}
		}
		for (Entry<String, String> e : valuesMap2.entrySet()) {
			if (!valuesMap1.containsKey(e.getKey())) {
				r.put(e.getKey(), new Pair<String, String>(e.getValue(), valuesMap1.get(e.getKey())));
			} else {
				if (!valuesMap1.get(e.getKey()).equals(valuesMap2.get(e.getKey()))) {
					r.put(e.getKey(), new Pair<String, String>(e.getValue(), valuesMap1.get(e.getKey())));
				}
			}
		}
		return r;
	}

	private Map<String, String> getValuesMap(Annotation a) throws IllegalAccessException, InvocationTargetException {
		Map<String, String> a1Map = new HashMap<String, String>();
		for (Method method : a.annotationType().getDeclaredMethods()) {
			method.setAccessible(true);
			Object o = method.invoke(a);
			if (o == null || o.toString().isEmpty())
				continue;
			if (o.getClass().isArray()) {
				if (o.getClass().getComponentType().isPrimitive()) {
					StringBuilder val = new StringBuilder();
					for (int i = 0; i < Array.getLength(o); i++) {
						val.append(Array.get(o, i)).append("-::-");
					}
				} else {
					Object[] arr = (Object[]) o;
					a1Map.put(method.getName(), Arrays.deepToString(arr));
				}
			} else {
				a1Map.put(method.getName(), o.toString());
			}
		}
		return a1Map;
	}
}
