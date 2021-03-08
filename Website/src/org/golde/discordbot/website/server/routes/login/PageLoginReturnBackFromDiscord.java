package org.golde.discordbot.website.server.routes.login;

import java.io.IOException;
import java.util.Map;

import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import io.mokulu.discord.oauth.OAuthDiscordAPI;
import io.mokulu.discord.oauth.model.OAuthTokensResponse;

public class PageLoginReturnBackFromDiscord extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {
		
		String code = urlParams.get("code");
		root.addProperty("code", code);

		try {
			
			final Gson gson = new Gson();
			
			OAuthTokensResponse tokens = PageLoginRedirect.oauthHandler.getTokens(code);
			String accessToken = tokens.getAccessToken();
//			String refreshToken = tokens.getRefreshToken();
//			
//			JsonObject tokensJSON = new JsonObject();
//			
//			tokensJSON.addProperty("accessToken", accessToken);
//			tokensJSON.addProperty("refreshToken", refreshToken);
//			
//			root.add("tokens", tokensJSON);
			
			JsonObject discordJSON = new JsonObject();
			OAuthDiscordAPI api = new OAuthDiscordAPI(accessToken);
			
			discordJSON.add("user", gson.toJsonTree(api.fetchUser()));
			//discordJSON.add("guilds", gson.toJsonTree(api.fetchGuilds()));
			
			root.add("discord", discordJSON);
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return root;
	}

}
