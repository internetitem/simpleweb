package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.config.Configuration;
import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.config.ConfigurationFactory;

public class HttpDispatcherServlet extends HttpServlet {

	private Router router;
	private Map<String, ControllerInstance> controllerMap;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		Map<String, String> params = getParams(servletConfig);

		try {
			Configuration config = ConfigurationFactory.getConfiguration(params);
			router = config.getRouter();
			controllerMap = config.getControllerMap();
		} catch (ConfigurationException e) {
			throw new ServletException(e);
		}
	}

	protected Map<String, String> getParams(ServletConfig servletConfig) {
		Map<String, String> params = new HashMap<>();
		Enumeration<String> paramNames = servletConfig.getInitParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String paramValue = servletConfig.getInitParameter(paramName);
			params.put(paramName, paramValue);
		}
		return params;
	}

	void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestHandler.handleRequest(router, controllerMap, req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest("GET", req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest("POST", req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest("PUT", req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest("DELETE", req, resp);
	}

}
