package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.config.Configuration;
import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.config.ConfigurationFactory;
import com.internetitem.simpleweb.utility.Params;

public class HttpDispatcherServlet extends HttpServlet {

	private Dispatcher dispatcher;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		Params params = getParams(servletConfig);

		try {
			Configuration config = ConfigurationFactory.getConfiguration(params);
			dispatcher = config.getDispatcher();
		} catch (ConfigurationException e) {
			throw new ServletException(e);
		}
	}

	protected Params getParams(ServletConfig servletConfig) {
		Params params = new Params();
		Enumeration<String> paramNames = servletConfig.getInitParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String paramValue = servletConfig.getInitParameter(paramName);
			params.setValue(paramName, paramValue);
		}
		return params;
	}

	void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		dispatcher.handleRequest(req, resp);
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
