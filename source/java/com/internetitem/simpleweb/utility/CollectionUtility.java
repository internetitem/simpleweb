package com.internetitem.simpleweb.utility;

import java.util.Map;
import java.util.Map.Entry;

public class CollectionUtility {
	public static <T, U> void putIfAbsent(Map<T, U> destination, Map<T, U> source) {
		for (Entry<T, U> entry : source.entrySet()) {
			if (!destination.containsKey(entry.getKey())) {
				destination.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
