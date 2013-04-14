package com.internetitem.simpleweb.config;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.internetitem.simpleweb.config.dataModel.RouterConfig;
import com.internetitem.simpleweb.config.dataModel.RouterHandler;
import com.internetitem.simpleweb.config.dataModel.RouterMatch;
import com.internetitem.simpleweb.router.RequestHandler;
import com.internetitem.simpleweb.router.Router;

public class Configuration {
	private Router router;
	private Map<String, RequestHandler> handlers;

	public Configuration(Reader reader) throws IOException {
		RouterConfig config = loadFromReader(reader);

		this.handlers = new HashMap<>();
		for (RouterHandler handler : config.getHandlers()) {
			String handlerName = handler.getName();
			String className = handler.getClassName();
			if (handlers.containsKey(handlerName)) {
				throw new IOException("Handler [" + handlerName + "] already exists");
			}
			handlers.put(handlerName, buildHandler(handlerName, className));
		}

		this.router = new Router();
		for (RouterMatch match : config.getMatches()) {
			String pattern = match.getPattern();
			String handlerName = match.getHandler();
			String methodName = match.getMethod();
			if (!handlers.containsKey(handlerName)) {
				throw new IOException("Handler [" + handlerName + "] for pattern [" + pattern + "] does not exist");
			}
			router.addRoute(pattern, handlerName, methodName);
		}
	}

	private RequestHandler buildHandler(String handlerName, String className) throws IOException {
		try {
			Class<?> clazz = Class.forName(className);
			if (!RequestHandler.class.isAssignableFrom(clazz)) {
				throw new IOException("Unable to create handler [" + handlerName + "] of class [" + className + "]: Not an instance of " + RequestHandler.class.getName());
			}
			return (RequestHandler) clazz.newInstance();
		} catch (Exception e) {
			throw new IOException("Unable to create handler [" + handlerName + "] of class [" + className + "]: " + e.getMessage(), e);
		}
	}

	private RouterConfig loadFromReader(Reader reader) throws IOException {
		return RouterConfig.parseFromStream(reader);
	}

	public Map<String, RequestHandler> getHandlers() {
		return handlers;
	}

	public Router getRouter() {
		return router;
	}
}
