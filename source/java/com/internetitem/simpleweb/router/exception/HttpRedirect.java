package com.internetitem.simpleweb.router.exception;

public class HttpRedirect extends Exception {
	private String newUrl;

	public HttpRedirect(String newUrl) {
		this.newUrl = newUrl;
	}

	public String getNewUrl() {
		return newUrl;
	}
}
