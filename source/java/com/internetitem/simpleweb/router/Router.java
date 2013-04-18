package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.internetitem.simpleweb.config.ConfigurationException;

public class Router {

	private List<ControllerInfo> controllers;

	public Router() {
		this.controllers = new ArrayList<>();
	}

	public ControllerDispatcher routeRequest(String httpMethod, String path) throws HttpError {
		for (ControllerInfo controller : controllers) {
			Matcher matcher = controller.getPattern().matcher(path);
			if (!matcher.matches()) {
				continue;
			}

			Map<String, String> pieces = new HashMap<>(controller.getParams());
			int groupNum = 1;
			for (String partName : controller.getPartNames()) {
				String value = matcher.group(groupNum);
				pieces.put(partName, value);
				groupNum++;
			}

			String controllerName = pieces.get("controller");
			if (controllerName == null) {
				throw new HttpError("No controller mapped for path " + path, 500);
			}

			String methodName = pieces.get("method");
			if (methodName == null) {
				methodName = "index";
			}

			return new ControllerDispatcher(controllerName, methodName, path, pieces);
		}
		throw new HttpError("Not Found", 404);
	}

	public void addRoute(String pattern, Map<String, String> params) throws ConfigurationException {
		controllers.add(new ControllerInfo(pattern, params));
	}

}
