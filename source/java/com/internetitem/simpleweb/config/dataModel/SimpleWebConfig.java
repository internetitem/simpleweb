package com.internetitem.simpleweb.config.dataModel;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import com.internetitem.simpleweb.utility.JsonUtility;

public class SimpleWebConfig {
	private String configClass;
	private Map<String, String> params;

	public String getConfigClass() {
		return configClass;
	}

	public void setConfigClass(String configClass) {
		this.configClass = configClass;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public static SimpleWebConfig parseFromStream(Reader reader) throws IOException {
		return JsonUtility.gson.fromJson(reader, SimpleWebConfig.class);
	}

}
