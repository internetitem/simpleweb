package com.internetitem.simpleweb.router;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface Dispatcher {

	void dispatchRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
