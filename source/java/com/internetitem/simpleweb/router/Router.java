package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.internetitem.simpleweb.config.ConfigurationException;

public class Router {

	private List<HandlerInfo> handlers;

	public Router() {
		this.handlers = new ArrayList<>();
	}

	public HandlerDispatcher routeRequest(String httpMethod, String path) throws HttpError {
		for (HandlerInfo handler : handlers) {
			Matcher matcher = handler.pattern.matcher(path);
			if (!matcher.matches()) {
				continue;
			}

			Map<String, String> pieces = new HashMap<>();
			int groupNum = 1;
			for (String partName : handler.partNames) {
				String value = matcher.group(groupNum);
				pieces.put(partName, value);
				groupNum++;
			}
			return new HandlerDispatcher(handler.handlerName, handler.methodName, path, pieces, handler.args);
		}
		throw new HttpError("Not Found", 404);
	}

	public void addRoute(String pattern, String handlerName, String methodName, Map<String, String> args) throws ConfigurationException {
		handlers.add(new HandlerInfo(handlerName, methodName, pattern, args));
	}

	private class HandlerInfo {
		private Pattern pattern;
		private List<String> partNames;
		private String handlerName;
		private String methodName;
		private Map<String, String> args;

		public HandlerInfo(String handlerName, String methodName, String stringPattern, Map<String, String> args) throws ConfigurationException {
			this.handlerName = handlerName;
			this.methodName = methodName;
			this.args = args;
			parsePattern(stringPattern);
		}

		private void parsePattern(String stringPattern) throws ConfigurationException {
			this.partNames = new ArrayList<>();
			Pattern nameMatcher = Pattern.compile("([^:]*)(?::(\\w+))?(.*)");

			StringBuilder reBuilder = new StringBuilder();
			if (stringPattern.startsWith("/")) {
				stringPattern = stringPattern.substring(1);
			}

			String[] parts = stringPattern.split("/");
			boolean foundStar = false;
			int optionalCount = 0;
			for (int partIndex = 0; partIndex < parts.length; partIndex++) {
				boolean isLast = partIndex == parts.length - 1;

				String part = parts[partIndex];
				if (part.endsWith("?")) {
					part = part.substring(0, part.length() - 1);

					if (isLast && part.isEmpty()) {
						reBuilder.append("/?");
						continue;
					}

					optionalCount++;
				} else if (optionalCount > 0) {
					optionalCount++;
				}

				if (part.isEmpty()) {
					throw new ConfigurationException("Route for handler " + handlerName + " method " + methodName + " contains an empty part");
				}

				if (optionalCount > 0) {
					reBuilder.append("(?:");
				}

				reBuilder.append("/");
				if (part.equals("*")) {
					if (foundStar) {
						throw new ConfigurationException("Route for handler " + handlerName + " method " + methodName + " may only contain a single wildcard *");
					}
					reBuilder.append("(.+?)?");
					partNames.add("path");

					continue;
				}

				Matcher matcher = nameMatcher.matcher(part);
				if (matcher.matches()) {
					String preString = matcher.group(1);
					if (preString != null && !preString.isEmpty()) {
						reBuilder.append(Pattern.quote(preString));
					}
					String label = matcher.group(2);
					if (label != null && !label.isEmpty()) {
						reBuilder.append("([^/]+)");
						partNames.add(label);
					}
					String postString = matcher.group(3);
					if (postString != null && !postString.isEmpty()) {
						reBuilder.append(Pattern.quote(postString));
					}
				} else {
					reBuilder.append(Pattern.quote(part));
				}
			}

			for (int i = 0; i < optionalCount; i++) {
				reBuilder.append(")?");
			}
			String finalPattern = reBuilder.toString();
			System.err.println("pattern [" + finalPattern + "]");
			System.err.println("parts [" + partNames + "]");
			this.pattern = Pattern.compile(finalPattern);
		}
	}

}
