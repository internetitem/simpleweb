package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.router.exception.HttpError;

public class Router {

	private List<RouteInfo> routes;

	public Router() {
		this.routes = new ArrayList<>();
	}

	public Map<String, String> routeRequest(String httpMethod, String path) throws HttpError {
		for (RouteInfo route : routes) {
			Matcher matcher = route.getPattern().matcher(path);
			if (!matcher.matches()) {
				continue;
			}

			Map<String, String> matchParams = new HashMap<>(route.getRouteParams());
			int groupNum = 1;
			for (String partName : route.getPartNames()) {
				String value = matcher.group(groupNum);
				matchParams.put(partName, value);
				groupNum++;
			}

			return matchParams;
		}
		throw new HttpError("Not Found", 404);
	}

	public void addRoute(String pattern, Map<String, String> params) throws ConfigurationException {
		routes.add(new RouteInfo(pattern, params));
	}

}
