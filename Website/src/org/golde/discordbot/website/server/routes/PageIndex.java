package org.golde.discordbot.website.server.routes;

import java.util.Map;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.util.VersionUtil;
import org.golde.discordbot.website.server.ESSWebServer;
import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;

public class PageIndex extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {
		root.addProperty("git", VersionUtil.getHashShort());
		root.addProperty("uptime", System.currentTimeMillis() - ESSWebServer.start);
		root.add("routes", ESSBot.GSON.toJsonTree(ESSWebServer.routes));
		return root;
	}

}
