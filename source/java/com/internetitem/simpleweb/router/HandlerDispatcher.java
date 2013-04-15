package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HandlerDispatcher {

	private String handlerName;
	private String methodName;
	private String path;
	private List<String> pieces;

	public HandlerDispatcher(String handlerName, String methodName, String path, List<String> pieces) {
		this.handlerName = handlerName;
		this.methodName = methodName;
		this.path = path;
		this.pieces = pieces;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getPath() {
		return path;
	}

	public List<String> getPieces() {
		return pieces;
	}

	public void dispatchRequest(RequestHandler handler, HttpServletRequest req, HttpServletResponse resp) throws HttpError, ServletException, IOException {
		Method method = findMethod(handler);
		dispatchInternal(handler, method, req, resp);
	}

	private void dispatchInternal(RequestHandler handler, Method method, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Class<?>[] parameterTypes = method.getParameterTypes();
		int numParameters = parameterTypes.length;
		Object[] parameterValues = new Object[numParameters];
		for (int i = 0; i < numParameters; i++) {
			Class<?> paramType = parameterTypes[i];
			if (paramType.isAssignableFrom(HttpServletRequest.class)) {
				parameterValues[i] = req;
			} else if (paramType.isAssignableFrom(HttpServletResponse.class)) {
				parameterValues[i] = resp;
			} else if (paramType.isAssignableFrom(String.class)) {
				parameterValues[i] = path;
			} else if (paramType.isAssignableFrom(String[].class)) {
				parameterValues[i] = pieces.toArray(new String[0]);
			} else {
				throw new ServletException("Unknown type " + paramType.getName() + " for method " + methodName + " in handler " + handlerName);
			}
		}

		Response response;
		try {
			response = (Response) method.invoke(handler, parameterValues);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ServletException("Internal error processing " + handlerName + "." + methodName, e);
		}

		resp.setContentType(response.getContentType());

		Collection<ResponseHeader> headers = response.getHeaders();
		if (headers != null) {
			for (ResponseHeader header : headers) {
				resp.setHeader(header.getName(), header.getValue());
			}
		}
		response.writeResponse(resp.getOutputStream());
	}

	private Method findMethod(RequestHandler handler) throws ServletException {
		Class<? extends RequestHandler> handlerClass = handler.getClass();
		for (Method method : handlerClass.getMethods()) {
			if (!method.getName().equals(methodName)) {
				continue;
			}

			if (!Response.class.isAssignableFrom(method.getReturnType())) {
				continue;
			}

			return method;
		}
		throw new ServletException("Handler " + handlerName + " does not contain method " + methodName);
	}

}
