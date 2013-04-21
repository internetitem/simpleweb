package com.internetitem.simpleweb.router;

import java.util.Map;

import com.internetitem.simpleweb.router.exception.HttpError;


public interface Router {

	Map<String, String> routeRequest(String httpMethod, String path) throws HttpError;

}
