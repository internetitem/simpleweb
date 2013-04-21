package com.internetitem.simpleweb.config;

import com.internetitem.simpleweb.router.Dispatcher;
import com.internetitem.simpleweb.utility.Params;

public interface Configuration {
	Dispatcher getDispatcher() throws ConfigurationException;

	void init(Params params);
}
