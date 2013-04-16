package com.internetitem.simpleweb.utility.converter;

public class StringToPrimitiveInt implements ObjectConverter<String, Integer> {

	@Override
	public Class<String> getSourceClass() {
		return String.class;
	}

	@Override
	public Class<Integer> getResultClass() {
		return Integer.TYPE;
	}

	@Override
	public Integer convertObject(String obj) throws Exception {
		return Integer.valueOf(obj);
	}

}
