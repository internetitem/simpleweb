package com.internetitem.simpleweb.config;

import java.util.Map;

public class MapConfigurationParameters implements ConfigurationParameters {

	private Map<String, String> map;

	public MapConfigurationParameters(Map<String, String> map) {
		this.map = map;
	}

	@Override
	public String getParameter(String name) {
		return map.get(name);
	}

}
