package com.internetitem.simpleweb.test;

import javax.servlet.http.HttpServletRequest;

import com.internetitem.simpleweb.response.StaticDataResponse;
import com.internetitem.simpleweb.router.RequestHandler;
import com.internetitem.simpleweb.router.Response;

public class TestHandler implements RequestHandler {

	public Response test1(HttpServletRequest request) {
		return new StaticDataResponse("text/plain", "Hello World!");
	}
}
