package com.internetitem.simpleweb.utility.converter;

public class StringToPrimitiveBoolean implements ObjectConverter<String, Boolean> {

	@Override
	public Class<String> getSourceClass() {
		return String.class;
	}

	@Override
	public Class<Boolean> getResultClass() {
		return Boolean.TYPE;
	}

	@Override
	public Boolean convertObject(String obj) throws Exception {
		return Boolean.valueOf(obj);
	}

}
