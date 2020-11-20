package org.golde.discordbot.website.server.routes;

import java.util.Map;

import org.golde.discordbot.website.WebsiteBot;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import net.dv8tion.jda.api.entities.Guild;

public class ResponseDoesUserExist extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {
		
		Guild g = WebsiteBot.getInstance().getGuild();
		
		
		
		return root;
	}

}
