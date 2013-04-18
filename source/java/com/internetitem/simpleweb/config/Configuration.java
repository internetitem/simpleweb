package com.internetitem.simpleweb.config;

import java.util.Map;

import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.router.Router;

public interface Configuration {
	Map<String, ControllerBase> getControllerMap();

	Router getRouter();

	void init() throws ConfigurationException;
}
