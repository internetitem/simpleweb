package com.internetitem.simpleweb.config.dataModel.router;

import java.util.Map;

public class RouterController {
	private String name;
	private String className;
	private Map<String, String> params;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

}