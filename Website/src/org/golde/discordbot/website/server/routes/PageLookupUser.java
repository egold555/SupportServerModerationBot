package org.golde.discordbot.website.server.routes;

import java.util.Map;

import org.golde.discordbot.website.WebsiteBot;
import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import net.dv8tion.jda.api.entities.User;

public class PageLookupUser extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {

		if(urlParams == null || urlParams.isEmpty() || !urlParams.containsKey("id")) {
			setErrored("Missing 'id' url paramater", Status.BAD_REQUEST);
			return root;
		}

		if(
				WebsiteBot.getInstance() == null || 
				WebsiteBot.getInstance().getGuild() == null || 
				WebsiteBot.getInstance().getJda().isUnavailable(WebsiteBot.getInstance().getGuild().getIdLong())
				) {
			setErrored("Unable to ping guild", Status.INTERNAL_ERROR);
			return root;
		}

		User user = null;
		try {
			user = WebsiteBot.getInstance().getJda().retrieveUserById(urlParams.get("id")).complete();
		}
		catch(Throwable e) {
			
		}

		if(user == null) {
			setErrored("Unable to find user.", Status.NOT_FOUND);
			return root;
		}

		root.addProperty("username", user.getName());
		root.addProperty("avatar", user.getEffectiveAvatarUrl());

		return root;
	}

}
