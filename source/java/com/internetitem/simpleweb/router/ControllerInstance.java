package com.internetitem.simpleweb.router;

import java.util.Map;

public class ControllerInstance {

	private Map<String, String> controllerParams;
	private ControllerBase controller;

	public ControllerInstance(Map<String, String> controllerParams, ControllerBase controller) {
		this.controllerParams = controllerParams;
		this.controller = controller;
	}

	public Map<String, String> getControllerParams() {
		return controllerParams;
	}

	public ControllerBase getController() {
		return controller;
	}

}
