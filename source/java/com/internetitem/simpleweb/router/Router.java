package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.floreysoft.jmte.Engine;
import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.router.exception.HttpRedirect;

public class Router {

	private List<ControllerInfo> controllers;

	public Router() {
		this.controllers = new ArrayList<>();
	}

	public ControllerDispatcher routeRequest(String httpMethod, String path) throws HttpError, HttpRedirect {
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

			String redirect = pieces.get("redirect");
			if (redirect != null) {
				String newUrl = expandText(redirect, pieces);
				throw new HttpRedirect(newUrl);
			}

			return new ControllerDispatcher(path, pieces);
		}
		throw new HttpError("Not Found", 404);
	}

	private String expandText(String text, Map<String, String> vars) {
		Engine engine = new Engine();
		Map<String, Object> temp = new HashMap<String, Object>(vars);
		String newUrl = engine.transform(text, temp);
		return newUrl;
	}

	public void addRoute(String pattern, Map<String, String> params) throws ConfigurationException {
		controllers.add(new ControllerInfo(pattern, params));
	}

}
