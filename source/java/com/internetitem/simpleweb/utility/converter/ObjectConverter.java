package com.internetitem.simpleweb.utility.converter;

public interface ObjectConverter<Source, Result> {
	Class<Source> getSourceClass();

	Class<Result> getResultClass();

	Result convertObject(Source obj) throws Exception;
}
