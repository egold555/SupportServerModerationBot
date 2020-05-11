package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.constants.Channels;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class WebhookListener extends AbstractMessageChecker {

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentStripped();
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
			
			if(title.toLowerCase().startsWith("How to code a Minecraft PVP Client".toLowerCase())) {
				guild.getTextChannelById(Channels.ANNOUNCEMENTS).sendMessage("<a:RedAlert:665412686175010816> **Hey " + guild.getRoleById("667224721544183838").getAsMention() + ", Eric has uploaded a new video**: " + title + "\n\nWatch it here: " + link).queue();
			}
			
			
			
			//msg.getTextChannel().sendMessage("Title: " + parts[0] + "\nLink: " + parts[1]).queue();
			
			msg.delete().queue();
		}
		
		
		
	}

}
