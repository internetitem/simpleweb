package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router {

	private List<Pattern> patterns;
	private List<String> handlerNames;

	public HandlerDispatcher routeRequest(String method, String path) throws HttpError {
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
			String handlerName = handlerNames.get(i);
			return new HandlerDispatcher(handlerName, method, path, pieces);
		}
		throw new HttpError("Not Found", 404);
	}

}
