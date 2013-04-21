package com.internetitem.simpleweb.utility.converter;

public class StringToPrimitiveInt implements StringConverter<Integer> {

	@Override
	public Class<Integer> getResultClass() {
		return Integer.TYPE;
	}

	@Override
	public Integer convertObject(String value) throws Exception {
		return Integer.valueOf(value);
	}

}
