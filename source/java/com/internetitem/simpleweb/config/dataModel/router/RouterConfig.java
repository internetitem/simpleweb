package com.internetitem.simpleweb.config.dataModel.router;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.internetitem.simpleweb.utility.JsonUtility;

public class RouterConfig {
	private List<RouterHandler> handlers;
	private List<RouterMatch> matches;

	public List<RouterMatch> getMatches() {
		return matches;
	}

	public void setMatches(List<RouterMatch> matches) {
		this.matches = matches;
	}

	public List<RouterHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<RouterHandler> handlers) {
		this.handlers = handlers;
	}

	public static RouterConfig parseFromStream(Reader reader) throws IOException {
		return JsonUtility.gson.fromJson(reader, RouterConfig.class);
	}
}
