package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.config.Configuration;

public class HttpDispatcherServlet extends HttpServlet {

	private Router router;
	private Map<String, RequestHandler> handlers;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		InputStream stream = Configuration.class.getResourceAsStream("/routes.json");
		try {
			if (stream == null) {
				throw new IOException("Unable to load /routes.json");
			}
			Configuration config = new Configuration(new InputStreamReader(stream, "UTF-8"));
			router = config.getRouter();
			handlers = config.getHandlers();
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			HandlerDispatcher dispatcher = router.routeRequest(method, req.getPathInfo());
			String handlerName = dispatcher.getHandlerName();
			RequestHandler handler = handlers.get(handlerName);
			if (handler == null) {
				throw new ServletException("No handler for " + handlerName);
			}

			dispatcher.dispatchRequest(handler, req, resp);
		} catch (HttpError e) {
			resp.sendError(e.getCode(), e.getMessage());
		}
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
