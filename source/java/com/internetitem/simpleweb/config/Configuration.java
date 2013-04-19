package com.internetitem.simpleweb.config;

import java.util.Map;

import com.internetitem.simpleweb.router.ControllerInstance;
import com.internetitem.simpleweb.router.Router;

public interface Configuration {
	Map<String, ControllerInstance> getControllerMap();

	Router getRouter();

	void init() throws ConfigurationException;
}
