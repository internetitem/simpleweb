package com.internetitem.simpleweb.utility;

public interface ObjectConverter {
	Class<?> getSourceClass();

	Class<?> getResultClass();

	Object convertObject(Object obj) throws Exception;
}
