package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class StaticDataResponse implements Response {

	private String encoding;
	private String contentType;
	private String data;
	private List<ResponseHeader> headers;

	public StaticDataResponse(String contentType, String data) {
		this.encoding = "UTF-8";
		this.contentType = contentType;
		this.data = data;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public String getContentType() {
		return contentType + "; charset=" + encoding;
	}

	@Override
	public void writeResponse(OutputStream stream) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(stream, encoding);
		writer.write(data);
		writer.flush();
	}

	public void setHeaders(List<ResponseHeader> headers) {
		this.headers = headers;
	}

	@Override
	public List<ResponseHeader> getHeaders() {
		return headers;
	}

	public static StaticDataResponse createTextResponse(String data) {
		return new StaticDataResponse("text/plain", data);
	}

	public static StaticDataResponse createHtmlResponse(String data) {
		return new StaticDataResponse("text/html", data);
	}

}
