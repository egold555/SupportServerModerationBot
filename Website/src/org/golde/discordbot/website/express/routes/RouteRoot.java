package org.golde.discordbot.website.express.routes;

import express.DynExpress;
import express.http.request.Request;
import express.http.response.Response;

public class RouteRoot extends Route {

	@Override
	@DynExpress(context = "/")
	public void onRequest(Request req, Response res) {
		res.send("Hello World!");
	}

}
