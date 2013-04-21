package com.internetitem.simpleweb.utility.converter;

public interface StringConverter<Result> {
	Class<Result> getResultClass();

	Result convertObject(String source) throws Exception;
}
