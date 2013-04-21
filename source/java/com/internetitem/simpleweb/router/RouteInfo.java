package com.internetitem.simpleweb.router;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface RouteInfo {

	Pattern getPattern();

	Map<String, String> getRouteParams();

	List<String> getPartNames();

}
