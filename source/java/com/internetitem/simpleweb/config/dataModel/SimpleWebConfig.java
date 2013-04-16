package com.internetitem.simpleweb.config.dataModel;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.internetitem.simpleweb.utility.JsonUtility;

public class SimpleWebConfig {
	private String configClass;
	private List<KeyValuePair> configParameters;

	public String getConfigClass() {
		return configClass;
	}

	public void setConfigClass(String configClass) {
		this.configClass = configClass;
	}

	public List<KeyValuePair> getConfigParameters() {
		return configParameters;
	}

	public void setConfigParameters(List<KeyValuePair> configParameters) {
		this.configParameters = configParameters;
	}

	public static SimpleWebConfig parseFromStream(Reader reader) throws IOException {
		return JsonUtility.gson.fromJson(reader, SimpleWebConfig.class);
	}

}
