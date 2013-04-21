package com.internetitem.simpleweb.test;

import javax.servlet.http.HttpServletRequest;

import com.internetitem.simpleweb.response.StaticDataResponse;
import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.utility.Params;

public class TestController implements ControllerBase {

	public StaticDataResponse test1(HttpServletRequest request, Params params) {
		return new StaticDataResponse("text/plain", "Hello World: " + params.toString());
	}
}
