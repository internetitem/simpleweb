package com.internetitem.simpleweb.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.internetitem.simpleweb.config.Configuration;
import com.internetitem.simpleweb.config.ConfigurationException;
import com.internetitem.simpleweb.config.ConfigurationFactory;
import com.internetitem.simpleweb.router.Dispatcher;
import com.internetitem.simpleweb.utility.Params;

public class SimpleWebHandler extends AbstractHandler {

	private Dispatcher dispatcher;

	public SimpleWebHandler(Params params) throws ConfigurationException {
		Configuration config = ConfigurationFactory.getConfiguration(params);
		this.dispatcher = config.getDispatcher();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		dispatcher.handleRequest(request, response);
		baseRequest.setHandled(true);
	}

}
