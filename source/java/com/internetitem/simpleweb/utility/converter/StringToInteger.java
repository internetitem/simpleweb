package com.internetitem.simpleweb.utility.converter;

public class StringToInteger implements StringConverter<Integer> {

	@Override
	public Class<Integer> getResultClass() {
		return Integer.class;
	}

	@Override
	public Integer convertObject(String value) throws Exception {
		return Integer.valueOf(value);
	}

}
