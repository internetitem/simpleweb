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

		Map<String, String> params = parseCommandLine(args);
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

	private static Map<String, String> parseCommandLine(String[] args) {
		Map<String, String> params = new HashMap<>();

		for (String arg : args) {
			String parts[] = arg.split("=", 2);
			if (parts.length != 2) {
				continue;
			}
			String key = parts[0];
			String value = parts[1];
			params.put(key, value);
		}

		return params;
	}

}
