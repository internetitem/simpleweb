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
import com.internetitem.simpleweb.router.ControllerInstance;
import com.internetitem.simpleweb.router.Dispatcher;
import com.internetitem.simpleweb.router.Router;
import com.internetitem.simpleweb.utility.BeanUtility;
import com.internetitem.simpleweb.utility.Params;

public class BasicConfiguration implements Configuration {

	private Params params;
	private String routes;

	public BasicConfiguration() {
	}

	public void setRoutes(String routes) {
		this.routes = routes;
	}

	@Override
	public void init(Params params) {
		this.params = params;
	}

	@Override
	public Dispatcher getDispatcher() throws ConfigurationException {
		Map<String, ControllerInstance> controllerMap = new HashMap<>();
		Router router = new Router();
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
					Map<String, String> controllerParams = controllerInfo.getParams();
					Params newParams = params.addParams(controllerParams);
					controllerMap.put(controllerName, buildController(className, controllerParams, newParams));
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
		return new Dispatcher(params, router, controllerMap);
	}

	private ControllerInstance buildController(String className, Map<String, String> controllerParams, Params params) throws Exception {
		ControllerBase controller = BeanUtility.createObject(className, ControllerBase.class, params);
		return new ControllerInstance(controllerParams, controller);
	}

	private RouterConfig loadFromReader(Reader reader) throws IOException {
		return RouterConfig.parseFromStream(reader);
	}

}
