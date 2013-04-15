package com.internetitem.simpleweb.server;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleWebServer {

	private static final Logger logger = LoggerFactory.getLogger(SimpleWebServer.class);

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		server.setHandler(new SimpleWebHandler());

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
