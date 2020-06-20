package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import org.golde.discordbot.supportserver.constants.Channels;
import org.golde.discordbot.supportserver.constants.Roles;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;

public class CommandYoutube extends OwnerCommand {

	public CommandYoutube() {
		super("yt", "<title|link>", "Better then the webhook stuff");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		String raw = event.getMessage().getContentRaw();
		raw = raw.substring(4); //remove ";yt "
		
		String[] parts = raw.split("\\|");
		
		String title = parts[0];
		String link = parts[1];
		
		Guild guild = event.getGuild();
		
		if(title.toLowerCase().startsWith("How to code a Minecraft PVP Client".toLowerCase())) {
			
			guild.getRoleById(Roles.YOUTUBE_NOTIFICATIONS).getManager().setMentionable(true).queue(success -> {
				guild.getTextChannelById(Channels.ANNOUNCEMENTS).sendMessage("<a:RedAlert:665412686175010816> **Hey " + guild.getRoleById(Roles.YOUTUBE_NOTIFICATIONS).getAsMention() + ", Eric has uploaded a new video**: " + title + "\n\nWatch it here: " + link).queue(success2 -> {
					guild.getRoleById(Roles.YOUTUBE_NOTIFICATIONS).getManager().setMentionable(false).queue();
				});
			});
			
			
		}
		
	}

}
