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
import com.internetitem.simpleweb.config.dataModel.router.RouterController;
import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.router.Router;

public class BasicConfiguration implements Configuration {
	private Router router;
	private Map<String, ControllerBase> controllerMap;
	private Map<String, String> params;

	private String routes;

	public BasicConfiguration() {
		this.controllerMap = new HashMap<>();
		this.router = new Router();
	}

	public void setRoutes(String routes) {
		this.routes = routes;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public void init() throws ConfigurationException {
		router.setParams(params);
		try {
			Enumeration<URL> resources = BasicConfiguration.class.getClassLoader().getResources(routes);
			while (resources.hasMoreElements()) {
				URL url = resources.nextElement();
				InputStream istream = url.openStream();
				Reader reader = new InputStreamReader(istream, "UTF-8");

				RouterConfig config = loadFromReader(reader);

				for (RouterController controllerInfo : config.getControllers()) {
					String controllerName = controllerInfo.getName();
					String className = controllerInfo.getClassName();
					if (controllerMap.containsKey(controllerName)) {
						throw new IOException("Controller [" + controllerName + "] already exists");
					}
					controllerMap.put(controllerName, buildController(controllerName, className));
				}

				for (Map<String, String> match : config.getRoutes()) {
					String pattern = match.get("pattern");
					if (pattern == null) {
						throw new IOException("Found match with no pattern");
					}
					router.addRoute(pattern, match);
				}
			}
		} catch (Exception e) {
			throw new ConfigurationException("Error initializing BasicConfiguration: " + e.getMessage(), e);
		}
	}

	private ControllerBase buildController(String controllerName, String className) throws IOException {
		try {
			Class<?> clazz = Class.forName(className);
			if (!ControllerBase.class.isAssignableFrom(clazz)) {
				throw new IOException("Unable to create controller [" + controllerName + "] of class [" + className + "]: Not an instance of " + ControllerBase.class.getName());
			}
			return (ControllerBase) clazz.newInstance();
		} catch (Exception e) {
			throw new IOException("Unable to create controller [" + controllerName + "] of class [" + className + "]: " + e.getMessage(), e);
		}
	}

	private RouterConfig loadFromReader(Reader reader) throws IOException {
		return RouterConfig.parseFromStream(reader);
	}

	public Map<String, ControllerBase> getControllerMap() {
		return controllerMap;
	}

	public Router getRouter() {
		return router;
	}
}
