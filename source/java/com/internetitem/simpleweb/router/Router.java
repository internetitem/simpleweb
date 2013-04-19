package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.router.exception.HttpRedirect;
import com.internetitem.simpleweb.utility.StringUtility;

public class Router {

	private Map<String, String> params;
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

			Map<String, String> pieces = new HashMap<>(params);
			pieces.putAll(controller.getParams());
			int groupNum = 1;
			for (String partName : controller.getPartNames()) {
				String value = matcher.group(groupNum);
				pieces.put(partName, value);
				groupNum++;
			}

			String redirect = pieces.get("redirect");
			if (redirect != null) {
				String newUrl = StringUtility.expandText(redirect, pieces);
				throw new HttpRedirect(newUrl);
			}

			return new ControllerDispatcher(path, pieces);
		}
		throw new HttpError("Not Found", 404);
	}

	public void addRoute(String pattern, Map<String, String> params) throws ConfigurationException {
		controllers.add(new ControllerInfo(pattern, params));
	}

	public void setParams(Map<String, String> params) {
		if (params == null) {
			params = new HashMap<>();
		}
		this.params = params;
	}

}
