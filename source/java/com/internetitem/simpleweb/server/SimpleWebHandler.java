package com.internetitem.simpleweb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.internetitem.simpleweb.config.Configuration;
import com.internetitem.simpleweb.router.HandlerDispatcher;
import com.internetitem.simpleweb.router.HttpError;
import com.internetitem.simpleweb.router.RequestHandler;
import com.internetitem.simpleweb.router.Router;

public class SimpleWebHandler extends AbstractHandler {

	private Router router;
	private Map<String, RequestHandler> handlers;

	public SimpleWebHandler() throws IOException {
		InputStream stream = Configuration.class.getResourceAsStream("/routes.json");
		if (stream == null) {
			throw new IOException("Unable to load /routes.json");
		}
		Configuration config = new Configuration(new InputStreamReader(stream, "UTF-8"));
		router = config.getRouter();
		handlers = config.getHandlers();
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			HandlerDispatcher dispatcher = router.routeRequest(request.getMethod(), target);
			String handlerName = dispatcher.getHandlerName();
			RequestHandler handler = handlers.get(handlerName);
			if (handler == null) {
				throw new ServletException("No handler for " + handlerName);
			}

			dispatcher.dispatchRequest(handler, request, response);
		} catch (HttpError e) {
			response.sendError(e.getCode(), e.getMessage());
		}
		baseRequest.setHandled(true);
	}

}
