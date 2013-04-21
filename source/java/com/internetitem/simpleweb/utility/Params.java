package com.internetitem.simpleweb.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Params {

	private Map<String, String> values;

	public Params() {
		this.values = new HashMap<>();
	}

	private Params(Map<String, String> initValues) {
		this.values = initValues;
	}

	public String getValue(String name) {
		return values.get(name);
	}

	public Set<String> getParamNames() {
		return values.keySet();
	}

	public String getEvaluatedValue(String name) {
		String rawValue = values.get(name);
		if (rawValue == null) {
			return rawValue;
		}
		return StringUtility.expandText(rawValue, values);
	}

	public void setValue(String name, String value) {
		values.put(name, value);
	}

	public Params addParams(Map<String, String> other) {
		Map<String, String> newMap = new HashMap<>(values);
		if (other != null) {
			newMap.putAll(other);
		}
		return new Params(newMap);
	}

	public Params addMissingParams(Map<String, String> other) {
		Map<String, String> newMap = new HashMap<>(values);
		if (other != null) {
			CollectionUtility.putIfAbsent(newMap, other);
		}
		return new Params(newMap);
	}

	public static Params newParamsFromMap(Map<String, String> initialValues) {
		return new Params(new HashMap<String, String>(initialValues));
	}

	@Override
	public String toString() {
		return values.toString();
	}
}
