package com.internetitem.simpleweb.test;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.internetitem.simpleweb.response.StaticDataResponse;
import com.internetitem.simpleweb.router.RequestHandler;

public class TestHandler implements RequestHandler {

	public StaticDataResponse test1(HttpServletRequest request, Map<String, String> pieces) {
		return new StaticDataResponse("text/plain", "Hello World: " + pieces.toString());
	}
}
