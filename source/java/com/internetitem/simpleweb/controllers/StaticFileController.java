package com.internetitem.simpleweb.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Collection;

import javax.servlet.ServletException;

import com.internetitem.simpleweb.annotation.ControllerOptions;
import com.internetitem.simpleweb.annotation.WebAction;
import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.router.Response;
import com.internetitem.simpleweb.router.ResponseHeader;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.utility.Params;

@ControllerOptions(exposeAll = false)
public class StaticFileController implements ControllerBase {

	@WebAction
	public StaticFileStreamer index(Params params) throws HttpError, ServletException, IOException {
		String filename = params.getEvaluatedValue("file");
		String path;
		if (filename != null) {
			path = filename;
		} else {
			path = params.getValue("path");
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		path = path.replaceAll("/\\.\\.", "");

		File file = new File(path);
		if (!file.isFile()) {
			throw new HttpError("File Not Found", 404);
		}
		InputStream istream = new FileInputStream(path);

		String contentType = getContentType(params, path);
		return new StaticFileStreamer(contentType, istream);
	}

	private String getContentType(Params params, String path) {
		String contentType = params.getValue("contentType");
		if (contentType != null) {
			return contentType;
		}

		return URLConnection.guessContentTypeFromName(path);
	}

	private class StaticFileStreamer implements Response {

		private String contentType;
		private InputStream inputStream;

		public StaticFileStreamer(String contentType, InputStream inputStream) {
			this.contentType = contentType;
			this.inputStream = inputStream;
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public void writeResponse(OutputStream outputStream) throws IOException {
			int numRead;
			byte[] buf = new byte[4096];
			while ((numRead = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, numRead);
			}
		}

		@Override
		public Collection<ResponseHeader> getHeaders() {
			return null;
		}

	}
}
