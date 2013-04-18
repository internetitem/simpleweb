package com.internetitem.simpleweb.config.dataModel.router;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import com.internetitem.simpleweb.utility.JsonUtility;

public class RouterConfig {
	private List<RouterController> controllers;
	private List<Map<String, String>> matches;

	public List<Map<String, String>> getMatches() {
		return matches;
	}

	public void setMatches(List<Map<String, String>> matches) {
		this.matches = matches;
	}

	public List<RouterController> getControllers() {
		return controllers;
	}

	public void setControllers(List<RouterController> controllers) {
		this.controllers = controllers;
	}

	public static RouterConfig parseFromStream(Reader reader) throws IOException {
		return JsonUtility.gson.fromJson(reader, RouterConfig.class);
	}
}
