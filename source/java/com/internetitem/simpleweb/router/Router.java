package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.router.exception.HttpRedirect;
import com.internetitem.simpleweb.utility.Params;

public class Router {

	private Params params;
	private List<ControllerInfo> controllers;

	public Router() {
		this.controllers = new ArrayList<>();
	}

	public Params routeRequest(String httpMethod, String path) throws HttpError, HttpRedirect {
		for (ControllerInfo controller : controllers) {
			Matcher matcher = controller.getPattern().matcher(path);
			if (!matcher.matches()) {
				continue;
			}

			Params newParams = params.addParams(controller.getParams());
			int groupNum = 1;
			for (String partName : controller.getPartNames()) {
				String value = matcher.group(groupNum);
				newParams.setValue(partName, value);
				groupNum++;
			}

			String redirect = newParams.getEvaluatedValue("redirect");
			if (redirect != null) {
				throw new HttpRedirect(redirect);
			}

			return newParams;
		}
		throw new HttpError("Not Found", 404);
	}

	public void addRoute(String pattern, Map<String, String> params) throws ConfigurationException {
		controllers.add(new ControllerInfo(pattern, params));
	}

	public void setParams(Params params) {
		this.params = params;
	}

}
