package com.internetitem.simpleweb.router;

import java.util.Map;

public class ControllerDispatcher {

	private String path;
	private Map<String, String> params;

	public ControllerDispatcher(String path, Map<String, String> params) {
		this.path = path;
		this.params = params;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getPath() {
		return path;
	}
}
