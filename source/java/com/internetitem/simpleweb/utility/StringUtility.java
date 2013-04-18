package com.internetitem.simpleweb.utility;

import java.util.HashMap;
import java.util.Map;

import com.floreysoft.jmte.Engine;

public class StringUtility {
	private static Engine engine = new Engine();

	public static String expandText(String text, Map<String, String> vars) {
		Map<String, Object> temp = new HashMap<String, Object>(vars);
		String transformed = engine.transform(text, temp);
		return transformed;
	}

}
