package com.internetitem.simpleweb.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.internetitem.simpleweb.annotation.ControllerOptions;
import com.internetitem.simpleweb.annotation.WebAction;
import com.internetitem.simpleweb.router.ControllerBase;
import com.internetitem.simpleweb.router.Response;
import com.internetitem.simpleweb.router.ResponseHeader;
import com.internetitem.simpleweb.router.exception.HttpError;
import com.internetitem.simpleweb.utility.StringUtility;

@ControllerOptions(exposeAll = false)
public class StaticFileController implements ControllerBase {

	private static final Logger logger = LoggerFactory.getLogger(StaticFileController.class);

	@WebAction
	public StaticFileStreamer index(Map<String, String> pieces) throws HttpError, ServletException {
		String filename = pieces.get("file");
		String path;
		if (filename != null) {
			path = StringUtility.expandText(filename, pieces);
		} else {
			path = pieces.get("path");
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		path = path.replaceAll("/\\.\\.", "");

		InputStream istream = getClass().getResourceAsStream(path);
		if (istream == null) {
			logger.warn("Unable to find file " + path);
			throw new HttpError("File not Found", 404);
		}
		String contentType = getContentType(pieces, path);
		return new StaticFileStreamer(contentType, istream);
	}

	private String getContentType(Map<String, String> pieces, String path) {
		String contentType = pieces.get("contentType");
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
