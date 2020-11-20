package org.golde.discordbot.website.server.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.golde.discordbot.website.WebsiteBot;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Message.Attachment;

public class ResponseHolidayImages extends AbstractJsonResponse {

	private static JsonObject cache = null;
	
	public static void start() {
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}, 5000, (1000 * 60) * 5);
		
	}
	
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
		
		MessageHistory history = g.getTextChannelById(776886937822298154L).getHistoryFromBeginning(100).complete();
		List<JsonObject> msgs = new ArrayList<JsonObject>();
		
		for(Message m : history.getRetrievedHistory()) {
			JsonObject mJson = toJson(m);
			if(mJson != null && !m.isPinned()) {
				msgs.add(mJson);
			}
		}
		
		root.addProperty("count", msgs.size());
		root.add("msgs", WebsiteBot.GSON.toJsonTree(msgs));
		return root;
	}
	
	private static JsonObject toJson(Member member) {
		
		JsonObject obj = new JsonObject();
		obj.addProperty("displayName", member.getEffectiveName());
		obj.addProperty("avatar", member.getUser().getEffectiveAvatarUrl());
		obj.addProperty("id", member.getId());
		
		return obj;
	}
	
	private static JsonObject toJson(Message msg) {
		
		if(msg.getAttachments().size() > 0) {
			
			JsonObject obj = new JsonObject();
			
			obj.add("sender", toJson(msg.getMember()));
			obj.addProperty("id", msg.getId());
			
			Attachment a = msg.getAttachments().get(0);
			if(a.isImage()) {
				JsonObject img = new JsonObject();
				img.addProperty("id", a.getId());
				img.addProperty("url", a.getProxyUrl());
				img.addProperty("width", a.getWidth());
				img.addProperty("height", a.getHeight());
				
				obj.add("image", img);
			}
			
			return obj;
		}
		
		return null;
	}

}
