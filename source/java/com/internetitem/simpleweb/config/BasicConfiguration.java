package com.internetitem.simpleweb.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.internetitem.simpleweb.config.dataModel.router.RouterConfig;
import com.internetitem.simpleweb.config.dataModel.router.RouterHandler;
import com.internetitem.simpleweb.config.dataModel.router.RouterMatch;
import com.internetitem.simpleweb.router.RequestHandler;
import com.internetitem.simpleweb.router.Router;

public class BasicConfiguration implements Configuration {
	private Router router;
	private Map<String, RequestHandler> handlers;

	private String configFilename;

	public BasicConfiguration() {
		this.handlers = new HashMap<>();
		this.router = new Router();
	}

	public void setConfigFilename(String configFilename) {
		this.configFilename = configFilename;
	}

	@Override
	public void init() throws ConfigurationException {
		try {
			Enumeration<URL> resources = BasicConfiguration.class.getClassLoader().getResources(configFilename);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				InputStream istream = url.openStream();
				Reader reader = new InputStreamReader(istream, "UTF-8");

				RouterConfig config = loadFromReader(reader);

				for (RouterHandler handler : config.getHandlers()) {
					String handlerName = handler.getName();
					String className = handler.getClassName();
					if (handlers.containsKey(handlerName)) {
						throw new IOException("Handler [" + handlerName + "] already exists");
					}
					handlers.put(handlerName, buildHandler(handlerName, className));
				}

				for (RouterMatch match : config.getMatches()) {
					String pattern = match.getPattern();
					String handlerName = match.getHandler();
					String methodName = match.getMethod();
					Map<String, String> args = match.getArgs();
					if (!handlers.containsKey(handlerName)) {
						throw new IOException("Handler [" + handlerName + "] for pattern [" + pattern + "] does not exist");
					}
					router.addRoute(pattern, handlerName, methodName, args);
				}
			}
		} catch (Exception e) {
			throw new ConfigurationException("Error initializing BasicConfiguration: " + e.getMessage(), e);
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
