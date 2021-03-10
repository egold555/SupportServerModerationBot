package org.golde.discordbot.website.express.routes.api;

import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.website.express.routes.Route;

import com.google.gson.JsonObject;

import express.DynExpress;
import express.http.request.Request;
import express.http.response.Response;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class RouteHomePageStats extends Route {
	
	@Override
	@DynExpress(context = "/api/stats-online")
	public void onRequest(Request req, Response res) {
		
		if(getGuild() == null) {
			sendError(req, res, 503, "Discord seems to be down. Please try again later.");
			return;
		}
		
		JsonObject data = new JsonObject();
		JsonObject guildObj = new JsonObject();
		
		int[] memberStats = getMemberStats();
		guildObj.addProperty("online", memberStats[0]);
		guildObj.addProperty("total", memberStats[1]);
		
		data.add("guild", guildObj);
		
		sendSuccess(req, res, data);
	}
	
	private int[] getMemberStats() {
		Guild g = getGuild();
		int total = 0;
		int online = 0;
		
		for(Member m : g.getMembersWithRoles(g.getRoleById(Roles.MEMBER))) {
			if(!m.getUser().isBot()) {
				if(m.getOnlineStatus() != OnlineStatus.OFFLINE && m.getOnlineStatus() != OnlineStatus.UNKNOWN) {
					online++;
				}
				total++;
			}
		}
		return new int[] {online, total};
	}
	
}
