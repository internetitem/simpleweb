package com.internetitem.simpleweb.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.annotation.ControllerOptions;
import com.internetitem.simpleweb.annotation.WebAction;
import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.router.Response;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.utility.Params;

@ControllerOptions(exposeAll = false)
public class RedirectController implements ControllerBase {

	public static final RedirectController INSTANCE = new RedirectController();

	@WebAction
	public Response index(Params params, HttpServletResponse response) throws HttpError, IOException, ServletException {
		String toUrl = params.getEvaluatedValue("redirect");
		response.sendRedirect(toUrl);
		return null;
	}
}
