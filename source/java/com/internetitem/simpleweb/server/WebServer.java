package com.internetitem.simpleweb.server;

import org.eclipse.jetty.server.Server;

public class WebServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		server.setHandler(new SimpleWebHandler());

		server.setStopTimeout(1000);
		server.setStopAtShutdown(true);

		server.start();
		server.join();
	}

}
