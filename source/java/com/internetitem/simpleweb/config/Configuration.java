package com.internetitem.simpleweb.config;

import java.util.Map;

import com.internetitem.simpleweb.router.RequestHandler;
import com.internetitem.simpleweb.router.Router;

public interface Configuration {
	Map<String, RequestHandler> getHandlers();

	Router getRouter();

	void init() throws ConfigurationException;
}
