package com.internetitem.simpleweb.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWebServer {

	private static final Logger logger = LoggerFactory.getLogger(SimpleWebServer.class);

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		Map<String, String> params = new HashMap<>();
		server.setHandler(new SimpleWebHandler(params));

		server.setStopTimeout(1000);
		server.setStopAtShutdown(true);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			logger.error("Failed to start server: " + e.getMessage(), e);
			server.stop();
		}
	}

}
