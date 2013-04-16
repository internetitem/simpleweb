package com.internetitem.simpleweb.utility;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.internetitem.simpleweb.config.dataModel.KeyValuePair;

public class JsonUtility {

	public static final Gson gson = new GsonBuilder().create();

	public static <T> T parse(Class<T> clazz, HttpServletRequest request) throws JsonParseException, IOException {
		return parse(gson, clazz, request);
	}

	public static <T> T parse(Gson gson, Class<T> clazz, HttpServletRequest request) throws JsonParseException, IOException {
		return gson.fromJson(request.getReader(), clazz);
	}

	public static Map<String, String> toMap(List<KeyValuePair> parameters) {
		Map<String, String> map = new HashMap<String, String>();

		if (parameters != null) {
			for (KeyValuePair kv : parameters) {
				map.put(kv.getKey(), kv.getValue());
			}
		}

		return map;
	}

}
