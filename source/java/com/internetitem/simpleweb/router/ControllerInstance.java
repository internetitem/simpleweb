package com.internetitem.simpleweb.router;

import java.util.Map;

public class ControllerInstance {
	private Map<String, String> params;
	private ControllerBase controller;

	public ControllerInstance(Map<String, String> params, ControllerBase controller) {
		this.params = params;
		this.controller = controller;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public ControllerBase getController() {
		return controller;
	}

}
