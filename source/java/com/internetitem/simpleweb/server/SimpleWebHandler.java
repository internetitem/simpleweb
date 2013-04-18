package com.internetitem.simpleweb.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.internetitem.simpleweb.config.Configuration;
import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.config.ConfigurationFactory;
import com.internetitem.simpleweb.config.ConfigurationParameters;
import com.internetitem.simpleweb.config.MapConfigurationParameters;
import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.router.RequestHandler;
import com.internetitem.simpleweb.router.Router;

public class SimpleWebHandler extends AbstractHandler {

	private Router router;
	private Map<String, ControllerBase> controllerMap;

	public SimpleWebHandler() throws ConfigurationException {
		ConfigurationParameters params = new MapConfigurationParameters(new HashMap<String, String>());
		Configuration config = ConfigurationFactory.getConfiguration(params);
		router = config.getRouter();
		controllerMap = config.getControllerMap();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RequestHandler.handleRequest(router, controllerMap, request, response);
		baseRequest.setHandled(true);
	}


}
