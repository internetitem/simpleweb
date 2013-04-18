package com.internetitem.simpleweb.router.exception;

public class HttpError extends Exception {
	private String message;
	private int code;

	public HttpError(String message, int code) {
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public int getCode() {
		return code;
	}

}
