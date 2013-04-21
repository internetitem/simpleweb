package com.internetitem.simpleweb.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.internetitem.simpleweb.utility.converter.StringConverter;
import com.internetitem.simpleweb.utility.converter.StringToBoolean;
import com.internetitem.simpleweb.utility.converter.StringToInteger;
import com.internetitem.simpleweb.utility.converter.StringToPrimitiveBoolean;
import com.internetitem.simpleweb.utility.converter.StringToPrimitiveInt;

public class BeanUtility {

	public static final List<StringConverter<?>> STANDARD_CONVERTERS = new ArrayList<>();
	static {
		STANDARD_CONVERTERS.add(new StringToBoolean());
		STANDARD_CONVERTERS.add(new StringToInteger());
		STANDARD_CONVERTERS.add(new StringToPrimitiveBoolean());
		STANDARD_CONVERTERS.add(new StringToPrimitiveInt());
	}

	@SuppressWarnings({ "rawtypes" })
	public static void injectParameters(Object obj, Params params, List<? extends StringConverter> converters) {
		Class<?> clazz = obj.getClass();
		Method[] methods = clazz.getMethods();
		for (String name : params.getParamNames()) {
			String rawValue = params.getValue(name);
			String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

			for (Method method : methods) {
				if (method.getName().equals(setterName)) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 1) {
						continue;
					}

					Class<?> parameterType = parameterTypes[0];
					Object value = null;
					if (parameterType.isAssignableFrom(String.class)) {
						value = rawValue;
					} else {
						for (StringConverter converter : converters) {
							Class<?> resultClass = converter.getResultClass();
							if (!resultClass.isAssignableFrom(parameterType)) {
								continue;
							}
							try {
								value = converter.convertObject(rawValue);
								break;
							} catch (Exception e) {
								// Ignore
								continue;
							}
						}
						if (value == null) {
							throw new RuntimeException("Unable to convert String to " + parameterType.getName() + " for method " + setterName + " of type " + clazz.getName());
						}
					}

					try {
						method.invoke(obj, value);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						throw new RuntimeException("Internal error invoking setter " + setterName + " on object of class " + clazz.getName() + ": " + e.getMessage(), e);
					} catch (InvocationTargetException e) {
						Throwable t = e.getTargetException();
						throw new RuntimeException("Internal error invoking setter " + setterName + " on object of class " + clazz.getName() + ": " + t.getMessage(), t);
					}
				}
			}
		}
	}

	public static <T> T createObject(String className, Class<T> clazz, Params params) throws Exception {
		Class<?> objClass = Class.forName(className);
		if (!clazz.isAssignableFrom(objClass)) {
			throw new Exception("Class " + className + " is not a " + clazz.getName());
		}
		Object obj = objClass.newInstance();
		injectParameters(obj, params, STANDARD_CONVERTERS);
		T newObj = clazz.cast(obj);
		return newObj;
	}
}
