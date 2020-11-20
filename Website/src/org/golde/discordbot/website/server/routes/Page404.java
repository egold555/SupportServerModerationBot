package org.golde.discordbot.website.server.routes;

import java.util.Map;

import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Page404 extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {
		setErrored("Route not found", Status.NOT_FOUND);
		return root;
	}

}
