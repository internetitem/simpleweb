package com.internetitem.simpleweb.utility.converter;

public class StringToPrimitiveBoolean implements StringConverter<Boolean> {

	@Override
	public Class<Boolean> getResultClass() {
		return Boolean.TYPE;
	}

	@Override
	public Boolean convertObject(String value) throws Exception {
		return Boolean.valueOf(value);
	}

}
