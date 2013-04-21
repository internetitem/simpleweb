package com.internetitem.simpleweb.config;

import java.util.Map;

import com.internetitem.simpleweb.router.ControllerInstance;
import com.internetitem.simpleweb.router.Router;
import com.internetitem.simpleweb.utility.Params;

public interface Configuration {
	Map<String, ControllerInstance> getControllerMap();

	Router getRouter();
	
	Params getParams();

	void init(Params params) throws ConfigurationException;
}
