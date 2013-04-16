package com.internetitem.simpleweb.utility;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.internetitem.simpleweb.utility.converter.ObjectConverter;
import com.internetitem.simpleweb.utility.converter.StringToBoolean;
import com.internetitem.simpleweb.utility.converter.StringToInteger;
import com.internetitem.simpleweb.utility.converter.StringToPrimitiveBoolean;
import com.internetitem.simpleweb.utility.converter.StringToPrimitiveInt;

public class BeanUtility {

	public static final List<ObjectConverter<?, ?>> STANDARD_STRING_CONVERTERS = new ArrayList<>();
	static {
		STANDARD_STRING_CONVERTERS.add(new StringToBoolean());
		STANDARD_STRING_CONVERTERS.add(new StringToInteger());
		STANDARD_STRING_CONVERTERS.add(new StringToPrimitiveBoolean());
		STANDARD_STRING_CONVERTERS.add(new StringToPrimitiveInt());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void injectParameters(Object obj, Map<String, ? extends Object> map, List<? extends ObjectConverter> converters) {
		Class<?> clazz = obj.getClass();
		Method[] methods = clazz.getMethods();
		for (Entry<String, ? extends Object> entry : map.entrySet()) {
			String name = entry.getKey();
			Object rawValue = entry.getValue();
			String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

			for (Method method : methods) {
				if (method.getName().equals(setterName)) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length != 1) {
						continue;
					}

					Class<?> parameterType = parameterTypes[0];
					Object value = null;
					if (parameterType.isInstance(rawValue)) {
						value = rawValue;
					} else {
						for (ObjectConverter converter : converters) {
							Class<?> sourceClass = converter.getSourceClass();
							if (!sourceClass.isInstance(rawValue)) {
								continue;
							}
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
							throw new RuntimeException("Unable to convert " + rawValue.getClass().getName() + " to " + parameterType.getName() + " for method " + setterName + " of type " + clazz.getName());
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

	public static <T> T createObject(String className, Class<T> clazz, Map<String, String> parameters) throws Exception {
		Class<?> objClass = Class.forName(className);
		if (!clazz.isAssignableFrom(objClass)) {
			throw new Exception("Class " + className + " is not a " + clazz.getName());
		}
		Object obj = objClass.newInstance();
		injectParameters(obj, parameters, STANDARD_STRING_CONVERTERS);
		T newObj = clazz.cast(obj);
		return newObj;
	}
}
