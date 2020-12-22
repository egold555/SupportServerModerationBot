package org.golde.discordbot.website.server.routes.error;

import java.util.Map;

import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class PageNotImplemented extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {
		setErrored("Route not implemented yet!", Status.NOT_IMPLEMENTED);
		return root;
	}

}
