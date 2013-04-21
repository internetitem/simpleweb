package com.internetitem.simpleweb.utility.converter;

public class StringToBoolean implements StringConverter<Boolean> {

	@Override
	public Class<Boolean> getResultClass() {
		return Boolean.class;
	}

	@Override
	public Boolean convertObject(String value) throws Exception {
		return Boolean.valueOf(value);
	}

}
