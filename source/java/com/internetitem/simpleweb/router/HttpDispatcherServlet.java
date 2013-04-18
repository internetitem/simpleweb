package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.config.Configuration;
import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.config.ConfigurationFactory;
import com.internetitem.simpleweb.config.ConfigurationParameters;
import com.internetitem.simpleweb.config.ServletContextConfigurationParameters;

public class HttpDispatcherServlet extends HttpServlet {

	private Router router;
	private Map<String, ControllerBase> controllerMap;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		ConfigurationParameters params = new ServletContextConfigurationParameters(servletConfig);
		Configuration config;
		try {
			config = ConfigurationFactory.getConfiguration(params);
		} catch (ConfigurationException e) {
			throw new ServletException(e);
		}
		router = config.getRouter();
		controllerMap = config.getControllerMap();
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
