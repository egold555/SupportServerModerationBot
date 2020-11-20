package org.golde.discordbot.website.server.routes.stats;

import java.util.Map;

import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.website.WebsiteBot;
import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class PageGeneralStats extends AbstractJsonResponse {

	@Override
	public JsonObject getResponse(Map<String, String> urlParams, IHTTPSession session, JsonObject root) {
		
		if(
				WebsiteBot.getInstance() == null || 
				WebsiteBot.getInstance().getGuild() == null || 
				WebsiteBot.getInstance().getJda().isUnavailable(WebsiteBot.getInstance().getGuild().getIdLong())
		) {
			setErrored("Unable to ping guild");
			return root;
		}
		
		Guild g = WebsiteBot.getInstance().getGuild();
		
		JsonObject objMembers = new JsonObject();
		objMembers.addProperty("total", getTotalMemberCount(g));
		objMembers.addProperty("online", getOnlineMemberCount(g));
		root.add("members", objMembers);
		
		JsonObject objGuild = new JsonObject();
		JsonObject objGuildBoost = new JsonObject();
		
		objGuildBoost.addProperty("tier", g.getBoostTier().name());
		objGuildBoost.addProperty("count", g.getBoostCount());
		
		objGuild.add("boost", objGuildBoost);
		root.add("guild", objGuild);
		
		return root;
	}
	
	private int getTotalMemberCount(Guild g) {
		int toReturn = 0;
		
		for(Member m : g.getMembersWithRoles(g.getRoleById(Roles.MEMBER))) {
			if(!m.getUser().isBot() && !m.getUser().isFake()) {
				toReturn++;
			}
		}
		return toReturn;
	}
	
	private int getOnlineMemberCount(Guild g) {
		int toReturn = 0;
		
		for(Member m : g.getMembersWithRoles(g.getRoleById(Roles.MEMBER))) {
			if(!m.getUser().isBot() && !m.getUser().isFake()) {
				if(m.getOnlineStatus() != OnlineStatus.OFFLINE && m.getOnlineStatus() != OnlineStatus.UNKNOWN) {
					toReturn++;
				}
			}
		}
		return toReturn;
	}

}
