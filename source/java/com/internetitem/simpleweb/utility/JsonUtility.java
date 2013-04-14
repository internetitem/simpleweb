package com.internetitem.simpleweb.utility;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class JsonUtility {

	public static final Gson gson = new GsonBuilder().create();

	public static <T> T parse(Class<T> clazz, HttpServletRequest request) throws JsonParseException, IOException {
		return parse(gson, clazz, request);
	}

	public static <T> T parse(Gson gson, Class<T> clazz, HttpServletRequest request) throws JsonParseException, IOException {
		return gson.fromJson(request.getReader(), clazz);
	}

}
