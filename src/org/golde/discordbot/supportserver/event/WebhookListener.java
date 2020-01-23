package org.golde.discordbot.supportserver.event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class WebhookListener extends AbstractMessageChecker {

	@Override
	protected boolean checkMessage(Member sender, String text) {
		
		//Webhook
		if(sender == null) {
			if(text.startsWith("yt|")) {
				return true;
			}
		}
		
		return false;
		
	}

	/*
	 <a:RedAlert:665412686175010816>
<a:confetti:665412685877477386>
	 */
	
	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		
		String raw = msg.getContentRaw();
		if(raw.startsWith("yt|")) {
			raw = raw.replace("yt|", "");
			String[] parts = raw.split("\\|");
			
			String title = parts[0];
			String link = parts[1];
			
			if(true/*title.toLowerCase().startsWith("How to code a Minecraft PVP Client".toLowerCase())*/) {
				guild.getTextChannelById("667231090465046539").sendMessage("<a:RedAlert:665412686175010816> **Hey " + guild.getRoleById("667224721544183838").getAsMention() + ", Eric has uploaded a new video**: " + title + "\n\nWatch it here: " + link).queue();
			}
			
			
			
			//msg.getTextChannel().sendMessage("Title: " + parts[0] + "\nLink: " + parts[1]).queue();
			
			msg.delete().queue();
		}
		
		
		
	}

}
