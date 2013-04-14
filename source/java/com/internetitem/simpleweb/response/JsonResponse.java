package com.internetitem.simpleweb.response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.internetitem.simpleweb.router.Response;
import com.internetitem.simpleweb.router.ResponseHeader;

public class JsonResponse implements Response {

	private Gson gson;
	private Object object;

	public JsonResponse(Object object) {
		this.object = object;
		gson = buildGson();
	}

	protected Gson buildGson() {
		return new GsonBuilder().create();
	}

	@Override
	public String getContentType() {
		return "application/json; charset=UTF-8";
	}

	@Override
	public void writeResponse(OutputStream stream) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
		gson.toJson(object, writer);
		writer.flush();
	}

	@Override
	public Collection<ResponseHeader> getHeaders() {
		return null;
	}

}
