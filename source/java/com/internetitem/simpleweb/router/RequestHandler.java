package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.internetitem.simpleweb.annotation.ControllerOptions;
import com.internetitem.simpleweb.annotation.WebAction;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.router.exception.HttpRedirect;

public class RequestHandler {
	public static void handleRequest(Router router, Map<String, ControllerBase> controllerMap, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String path = request.getPathInfo();
			ControllerDispatcher dispatcher = router.routeRequest(request.getMethod(), path);
			Map<String, String> params = dispatcher.getParams();

			ControllerBase controller = getController(controllerMap, params, path);
			boolean exposeAll = getExposeAll(controller);
			String controllerName = params.get("internal:controller");

			Method method = getMethod(controller, params, controllerName, path, exposeAll);
			String methodName = params.get("internal:method");

			dispatchInternal(controller, method, path, dispatcher.getParams(), controllerName, methodName, request, response);
		} catch (HttpError e) {
			response.sendError(e.getCode(), e.getMessage());
		} catch (HttpRedirect e) {
			response.sendRedirect(e.getNewUrl());
		}
	}

	private static boolean getExposeAll(ControllerBase controller) {
		ControllerOptions options = controller.getClass().getAnnotation(ControllerOptions.class);
		if (options == null) {
			return true;
		}
		return options.exposeAll();
	}

	private static Method getMethod(ControllerBase controller, Map<String, String> params, String controllerName, String path, boolean exposeAll) throws ServletException {
		String methodName = params.get("action");
		if (methodName == null) {
			methodName = "index";
		}
		Method method = findMethod(controller, methodName, exposeAll);

		if (method != null) {
			params.put("internal:method", methodName);
			return method;
		} else {
			String invalidMethodName = params.get("invalid:action");
			method = findMethod(controller, invalidMethodName, exposeAll);
			if (method == null) {
				throw new ServletException("Unable to find action [" + methodName + "] in controller [" + controllerName + "]");
			}
			params.put("internal:method", invalidMethodName);
			return method;
		}
	}

	private static ControllerBase getController(Map<String, ControllerBase> controllerMap, Map<String, String> params, String path) throws ServletException {
		String controllerName = params.get("controller");
		if (controllerName == null) {
			throw new ServletException("Missing controller name mapping for path [" + path + "]");
		}
		ControllerBase controller = controllerMap.get(controllerName);
		if (controller != null) {
			params.put("internal:controller", controllerName);
			return controller;
		} else {
			String invalidControllerName = params.get("invalid:controller");
			if (invalidControllerName == null) {
				throw new ServletException("Could not find controller [" + controllerName + "] for [" + path + "]");
			}
			controller = controllerMap.get(invalidControllerName);
			if (controller == null) {
				throw new ServletException("No controller for " + controllerName);
			}
			params.put("internal:controller", invalidControllerName);
			return controller;
		}
	}

	private static void dispatchInternal(ControllerBase controller, Method method, String path, Map<String, String> params, String controllerName, String methodName, HttpServletRequest req, HttpServletResponse resp) throws HttpError, ServletException, IOException {
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
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ServletException("Internal error processing " + controllerName + "." + methodName, e);
		} catch (InvocationTargetException e) {
			Throwable target = e.getTargetException();
			try {
				throw target;
			} catch (IOException e2) {
				throw e2;
			} catch (ServletException e2) {
				throw e2;
			} catch (HttpError e2) {
				throw e2;
			} catch (Throwable t) {
				throw new ServletException("Internal error processing " + controllerName + "." + methodName, e);
			}
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

	private static Method findMethod(ControllerBase controller, String methodName, boolean exposeAll) {
		Class<? extends ControllerBase> controllerClass = controller.getClass();
		for (Method method : controllerClass.getMethods()) {
			if (!method.getName().equals(methodName)) {
				continue;
			}

			if (!Response.class.isAssignableFrom(method.getReturnType())) {
				continue;
			}

			if (!isMethodExposed(method, exposeAll)) {
				continue;
			}

			return method;
		}
		return null;
	}

	private static boolean isMethodExposed(Method method, boolean exposeAll) {
		if (exposeAll) {
			return true;
		}
		WebAction webMethod = method.getAnnotation(WebAction.class);
		return (webMethod != null);
	}
}
