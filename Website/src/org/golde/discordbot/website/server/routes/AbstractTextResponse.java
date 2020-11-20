package org.golde.discordbot.website.server.routes;

import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD;

public abstract class AbstractTextResponse extends RouterNanoHTTPD.DefaultHandler {

	@Override
	public IStatus getStatus() {
		return Status.OK;
	}

	@Override
	public String getMimeType() {
		return "text/html";
	}

}
