package org.golde.discordbot.website.express.routes;

import express.DynExpress;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;

public class Route404 extends Route {

	@Override
	@DynExpress(context = "*")
	public void onRequest(Request req, Response res) {
		
		//for API methods, should be JSON
		if(req.getPath().contains("/api/")) {
			sendError(req, res, 404, "Page not found.");
			return;
		}
		
		//For pages, should be a 404 page.
		res.setStatus(Status._404).send("404 page not found.");
	}

}
