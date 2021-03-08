package org.golde.discordbot.website.server.routes.base;

import java.io.InputStream;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.router.RouterNanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD.UriResource;

public abstract class AbstractRedirect extends RouterNanoHTTPD.DefaultStreamHandler {

	@Override
	public final IStatus getStatus() {
		return Status.REDIRECT;
	}

	@Override
	public NanoHTTPD.Response get(final UriResource uriResource, final Map<String, String> urlParams, final NanoHTTPD.IHTTPSession session) {
		NanoHTTPD.Response r = NanoHTTPD.newFixedLengthResponse(this.getStatus(), this.getMimeType(), "");
		r.addHeader("Location", getRedirectUrl());
		r.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		return r;
	}

	protected abstract String getRedirectUrl();

	@Override
	public final InputStream getData() {
		throw new IllegalStateException("this method should not be called in a text based nanolet");
	}

	@Override
	public final String getMimeType() {
		return "text/html";
	}

}
