package com.internetitem.simpleweb.config;

import javax.servlet.ServletConfig;

public class ServletContextConfigurationParameters implements ConfigurationParameters {

	private ServletConfig servletConfig;

	public ServletContextConfigurationParameters(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	@Override
	public String getParameter(String name) {
		return servletConfig.getInitParameter(name);
	}

}
