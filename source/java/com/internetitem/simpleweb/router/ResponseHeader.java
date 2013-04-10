package com.internetitem.simpleweb.router;

public class ResponseHeader {
	private String name;
	private String value;

	public ResponseHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
