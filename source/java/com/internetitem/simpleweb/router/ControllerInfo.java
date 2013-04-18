package com.internetitem.simpleweb.router;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.internetitem.simpleweb.config.ConfigurationException;

public class ControllerInfo {

	private static final Logger logger = LoggerFactory.getLogger(ControllerInfo.class);

	private Pattern pattern;
	private List<String> partNames;
	private Map<String, String> params;

	public ControllerInfo(String stringPattern, Map<String, String> params) throws ConfigurationException {
		this.params = params;
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
				throw new ConfigurationException("Route [" + stringPattern + "] contains an empty part");
			}

			if (optionalCount > 0) {
				reBuilder.append("(?:");
			}

			reBuilder.append("/");
			if (part.equals("*")) {
				if (foundStar) {
					throw new ConfigurationException("Route [" + stringPattern + "] may only contain a single wildcard *");
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

		logger.debug("Adding route with pattern [" + stringPattern + "] and final pattern [" + finalPattern + "] and parts [" + partNames + "]");
		this.pattern = Pattern.compile(finalPattern);
	}

	public Pattern getPattern() {
		return pattern;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public List<String> getPartNames() {
		return partNames;
	}
}
