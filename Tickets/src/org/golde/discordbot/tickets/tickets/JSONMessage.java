package org.golde.discordbot.tickets.tickets;

import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Getter
public class JSONMessage implements Comparable<JSONMessage>{

	private final String message;
	private final JSONMember member;
	private final long epochMillis;
	
	public JSONMessage(Message m) {
		if(m == null) {
			message = "null";
			member = new JSONMember(null);
			epochMillis = -1;
			return;
		}
		
		if(m.getContentDisplay().length() == 0 && m.getEmbeds().size() != 0) {
			MessageEmbed embed = m.getEmbeds().get(0);
			message = "[MessageEmbed]" + embed.getDescription() + "[/MessageEmbed]";
			
			
		}
		else {
			message = m.getContentDisplay();
		}
		
		member = new JSONMember(m.getMember());
		epochMillis = m.getTimeCreated().toInstant().toEpochMilli();
	}

	@Override
	public int compareTo(JSONMessage o) {
		if(epochMillis > o.epochMillis) {
			return 1;
		}
		else if(epochMillis == o.epochMillis) {
			return 0;
		}
		return -1;
	}
	
	
	
}
