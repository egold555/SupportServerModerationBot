package org.golde.discordbot.website.express.routes.api;

import org.golde.discordbot.website.express.routes.Route;

import com.google.gson.JsonObject;

import express.DynExpress;
import express.http.request.Request;
import express.http.response.Response;

public class RouteAPIRoot extends Route {

	private static final long START = System.currentTimeMillis();
	
	@Override
	@DynExpress(context = "/api")
	public void onRequest(Request req, Response res) {
		JsonObject data = new JsonObject();
		
		data.addProperty("uptime", System.currentTimeMillis() - START);
		
		sendSuccess(req, res, data);
	}

}
