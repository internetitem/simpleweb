package com.internetitem.simpleweb.utility.converter;

public class StringToInteger implements ObjectConverter<String, Integer> {

	@Override
	public Class<String> getSourceClass() {
		return String.class;
	}

	@Override
	public Class<Integer> getResultClass() {
		return Integer.class;
	}

	@Override
	public Integer convertObject(String obj) throws Exception {
		return Integer.valueOf(obj);
	}

}
