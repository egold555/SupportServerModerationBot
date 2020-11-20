package org.golde.discordbot.website.server.routes;

public class PageIndex extends AbstractTextResponse {
	
	@Override
	public String getText() {
		return "Hello World. You have reached the index page!";
	}

}
