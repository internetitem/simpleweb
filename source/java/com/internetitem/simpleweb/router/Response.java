package com.internetitem.simpleweb.router;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public interface Response {
	String getContentType();

	void writeResponse(OutputStream stream) throws IOException;

	Collection<ResponseHeader> getHeaders();
}
