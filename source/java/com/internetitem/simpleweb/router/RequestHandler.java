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
import com.internetitem.simpleweb.controllers.RedirectController;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.utility.Params;

public class RequestHandler {
	public static void handleRequest(Params params, Router router, Map<String, ControllerInstance> controllerMap, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String path = request.getPathInfo();
			Map<String, String> matchParams = router.routeRequest(request.getMethod(), path);

			DispatchInfo dispatchInfo = new DispatchInfo();
			dispatchInfo.setRequest(request);
			dispatchInfo.setResponse(response);
			dispatchInfo.setPath(path);
			findController(dispatchInfo, controllerMap, params, matchParams);
			findMethod(dispatchInfo);
			dispatchInternal(dispatchInfo);
		} catch (HttpError e) {
			response.sendError(e.getCode(), e.getMessage());
		}
	}

	private static boolean getExposeAll(DispatchInfo dispatchInfo) {
		ControllerBase controller = dispatchInfo.getController();
		ControllerOptions options = controller.getClass().getAnnotation(ControllerOptions.class);
		if (options == null) {
			return true;
		}
		return options.exposeAll();
	}

	private static void findMethod(DispatchInfo dispatchInfo) throws ServletException {
		boolean exposeAll = getExposeAll(dispatchInfo);
		Params params = dispatchInfo.getParams();

		String defaultMethodName = dispatchInfo.getMethodName();
		String methodName = params.getValue("action");
		if (defaultMethodName != null) {
			methodName = defaultMethodName;
		} else if (methodName == null) {
			methodName = "index";
		}
		ControllerBase controller = dispatchInfo.getController();
		Method method = findJavaMethod(controller, methodName, exposeAll);
		if (method != null) {
			dispatchInfo.setMethodName(methodName);
			dispatchInfo.setMethod(method);
		} else {
			String invalidMethodName = params.getValue("invalid:action");
			method = findJavaMethod(controller, invalidMethodName, exposeAll);
			if (method == null) {
				throw new ServletException("Unable to find action [" + methodName + "] in controller [" + dispatchInfo.getControllerName() + "]");
			}
			dispatchInfo.setMethodName(invalidMethodName);
			dispatchInfo.setMethod(method);
		}
	}

	private static void findController(DispatchInfo dispatchInfo, Map<String, ControllerInstance> controllerMap, Params oldParams, Map<String, String> matchParams) throws ServletException {
		String redirect = matchParams.get("redirect");
		Params newParams;
		if (redirect != null) {
			dispatchInfo.setController(RedirectController.INSTANCE);
			dispatchInfo.setControllerName("<redirect>");
			dispatchInfo.setMethodName("index");
			newParams = oldParams;
		} else {
			String controllerName = matchParams.get("controller");
			if (controllerName == null) {
				throw new ServletException("Missing controller name mapping for path [" + dispatchInfo.getPath() + "]");
			}
			ControllerInstance controller = controllerMap.get(controllerName);
			if (controller != null) {
				dispatchInfo.setControllerName(controllerName);
				dispatchInfo.setController(controller.getController());
				newParams = oldParams.addParams(controller.getControllerParams());
			} else {
				String invalidControllerName = matchParams.get("invalid:controller");
				if (invalidControllerName == null) {
					throw new ServletException("Could not find controller [" + controllerName + "] for [" + dispatchInfo.getPath() + "]");
				}
				controller = controllerMap.get(invalidControllerName);
				if (controller == null) {
					throw new ServletException("No controller for " + controllerName);
				}
				dispatchInfo.setControllerName(invalidControllerName);
				dispatchInfo.setController(controller.getController());
				newParams = oldParams.addParams(controller.getControllerParams());
			}
		}
		Params finalParams = newParams.addParams(matchParams);
		dispatchInfo.setParams(finalParams);
	}

	private static void dispatchInternal(DispatchInfo dispatchInfo) throws HttpError, ServletException, IOException {
		ControllerBase controller = dispatchInfo.getController();
		Method method = dispatchInfo.getMethod();

		Class<?>[] parameterTypes = method.getParameterTypes();
		int numParameters = parameterTypes.length;
		Object[] parameterValues = new Object[numParameters];
		for (int i = 0; i < numParameters; i++) {
			Class<?> paramType = parameterTypes[i];
			if (paramType.isAssignableFrom(HttpServletRequest.class)) {
				parameterValues[i] = dispatchInfo.getRequest();
			} else if (paramType.isAssignableFrom(HttpServletResponse.class)) {
				parameterValues[i] = dispatchInfo.getResponse();
			} else if (paramType.isAssignableFrom(String.class)) {
				parameterValues[i] = dispatchInfo.getPath();
			} else if (paramType.isAssignableFrom(Params.class)) {
				parameterValues[i] = dispatchInfo.getParams();
			} else {
				throw new ServletException("Unknown type " + paramType.getName() + " for method " + dispatchInfo.getMethodName() + " in controller " + dispatchInfo.getControllerName());
			}
		}

		Response response;
		try {
			response = (Response) method.invoke(controller, parameterValues);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new ServletException("Internal error processing " + dispatchInfo.getControllerName() + "." + dispatchInfo.getMethodName(), e);
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
				throw new ServletException("Internal error processing " + dispatchInfo.getControllerName() + "." + dispatchInfo.getMethodName(), e);
			}
		}

		if (response != null) {
			HttpServletResponse resp = dispatchInfo.getResponse();
			resp.setContentType(response.getContentType());

			Collection<ResponseHeader> headers = response.getHeaders();
			if (headers != null) {
				for (ResponseHeader header : headers) {
					resp.setHeader(header.getName(), header.getValue());
				}
			}
			response.writeResponse(resp.getOutputStream());
		}
	}

	private static Method findJavaMethod(ControllerBase controller, String methodName, boolean exposeAll) {
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
