package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

	private List<Pattern> patterns;
	private List<HandlerInfo> handlers;

	public Router() {
		this.patterns = new ArrayList<>();
		this.handlers = new ArrayList<>();
	}

	public HandlerDispatcher routeRequest(String httpMethod, String path) throws HttpError {
		int numPatterns = patterns.size();
		for (int i = 0; i < numPatterns; i++) {
			Pattern pattern = patterns.get(i);
			Matcher matcher = pattern.matcher(path);
			if (!matcher.matches()) {
				continue;
			}

			int numGroups = matcher.groupCount();
			List<String> pieces = new ArrayList<>();
			for (int g = 1; g <= numGroups; g++) {
				pieces.add(matcher.group(g));
			}
			HandlerInfo handler = handlers.get(i);
			return new HandlerDispatcher(handler.handlerName, handler.methodName, path, pieces);
		}
		throw new HttpError("Not Found", 404);
	}

	public void addRoute(String pattern, String handlerName, String methodName) {
		patterns.add(Pattern.compile(pattern));
		handlers.add(new HandlerInfo(handlerName, methodName));
	}

	private class HandlerInfo {
		private String handlerName;
		private String methodName;

		public HandlerInfo(String handlerName, String methodName) {
			this.handlerName = handlerName;
			this.methodName = methodName;
		}
	}

}
