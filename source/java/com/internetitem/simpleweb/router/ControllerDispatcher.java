package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.router.exception.HttpError;

public class ControllerDispatcher {

	private String controllerName;
	private String methodName;
	private String path;
	private Map<String, String> params;

	public ControllerDispatcher(String controllerName, String methodName, String path, Map<String, String> params) {
		this.controllerName = controllerName;
		this.methodName = methodName;
		this.path = path;
		this.params = params;
	}

	public String getControllerName() {
		return controllerName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void dispatchRequest(ControllerBase controller, HttpServletRequest req, HttpServletResponse resp) throws HttpError, ServletException, IOException {
		Method method = findMethod(controller);
		dispatchInternal(controller, method, req, resp);
	}

	private void dispatchInternal(ControllerBase controller, Method method, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			} else if (paramType.isAssignableFrom(Map.class)) {
				parameterValues[i] = params;
			} else {
				throw new ServletException("Unknown type " + paramType.getName() + " for method " + methodName + " in controller " + controllerName);
			}
		}

		Response response;
		try {
			response = (Response) method.invoke(controller, parameterValues);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ServletException("Internal error processing " + controllerName + "." + methodName, e);
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

	private Method findMethod(ControllerBase controller) throws ServletException {
		Class<? extends ControllerBase> controllerClass = controller.getClass();
		for (Method method : controllerClass.getMethods()) {
			if (!method.getName().equals(methodName)) {
				continue;
			}

			if (!Response.class.isAssignableFrom(method.getReturnType())) {
				continue;
			}

			return method;
		}
		throw new ServletException("Controller " + controllerName + " does not contain method " + methodName);
	}

}
