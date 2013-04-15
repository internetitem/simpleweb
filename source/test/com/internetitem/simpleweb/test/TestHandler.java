package com.internetitem.simpleweb.test;

import javax.servlet.http.HttpServletRequest;

import com.internetitem.simpleweb.response.StaticDataResponse;
import com.internetitem.simpleweb.router.RequestHandler;

public class TestHandler implements RequestHandler {

	public StaticDataResponse test1(HttpServletRequest request) {
		return new StaticDataResponse("text/plain", "Hello World!");
	}
}
