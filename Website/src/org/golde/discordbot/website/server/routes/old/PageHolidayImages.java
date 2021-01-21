package org.golde.discordbot.website.server.routes.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.golde.discordbot.website.WebsiteBot;
import org.golde.discordbot.website.server.routes.base.AbstractJsonResponse;

import com.google.gson.JsonObject;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;

public class PageHolidayImages extends AbstractJsonResponse {

//	private static JsonObject cache = null;
//	
//	public static void start() {
//		
//		new Timer().scheduleAtFixedRate(new TimerTask() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//			}
//		}, 5000, (1000 * 60) * 5);
//		
//	}
//	
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
		
		msgs.sort(new Comparator<JsonObject>() {

			@Override
			public int compare(JsonObject o1, JsonObject o2) {
				
				int o1Total = o1.get("votes").getAsJsonObject().get("total").getAsInt();
				int o2Total = o2.get("votes").getAsJsonObject().get("total").getAsInt();
				
				if(o1Total > o2Total) {
					return 1;
				}
				else if(o1Total < o2Total) {
					return -1;
				}
				else {
					return 0;
				}
			}
		});
		
		Collections.reverse(msgs);
		
		root.addProperty("count", msgs.size());
		root.add("msgs", WebsiteBot.GSON.toJsonTree(msgs));
		return root;
	}
	
	private static JsonObject toJson(Member member) {
		
		JsonObject obj = new JsonObject();
		obj.addProperty("displayName", member.getEffectiveName());
		//obj.addProperty("avatar", member.getUser().getEffectiveAvatarUrl());
		//obj.addProperty("id", member.getId());
		
		return obj;
	}
	
	private static JsonObject toJson(Message msg) {
		
		if(msg.getAttachments().size() > 0) {
			
			JsonObject obj = new JsonObject();
			
			obj.add("sender", toJson(msg.getMember()));
			//obj.addProperty("id", msg.getId());
			obj.addProperty("url", msg.getJumpUrl());
			
			int likes = 0;
			int dislikes = 0;
			for(MessageReaction mr : msg.getReactions()) {
				ReactionEmote re = mr.getReactionEmote();
				
				if(re.getName().equals("like")) {
					likes = mr.getCount();
				}
				if(re.getName().equals("dislike")) {
					dislikes = mr.getCount();
				}
			}
			
			JsonObject objVotes = new JsonObject();
			objVotes.addProperty("likes", likes);
			objVotes.addProperty("dislikes", dislikes);
			
			objVotes.addProperty("total", Math.max(0, likes - dislikes));
			
			obj.add("votes", objVotes);
			
			Attachment a = msg.getAttachments().get(0);
			if(a.isImage()) {
				JsonObject img = new JsonObject();
				//img.addProperty("id", a.getId());
				img.addProperty("url", a.getProxyUrl());
				//img.addProperty("width", a.getWidth());
				//img.addProperty("height", a.getHeight());
				
				obj.add("image", img);
			}
			
			return obj;
		}
		
		return null;
	}

}
