package org.golde.discordbot.utilities.command.owner;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.constants.SSEmojis;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Guild;

public class CommandYoutube extends OwnerCommand {

	public CommandYoutube(@Nonnull ESSBot bot) {
		super(bot, "yt", "<title|link>", "Announce a new video (Replaces broken webhook stuff)");
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
				guild.getTextChannelById(Channels.Info.ANNOUNCEMENTS).sendMessage(SSEmojis.RED_ALERT + "**Hey " + guild.getRoleById(Roles.YOUTUBE_NOTIFICATIONS).getAsMention() + ", Eric has uploaded a new video**: " + title + "\n\nWatch it here: " + link).queue(success2 -> {
					guild.getRoleById(Roles.YOUTUBE_NOTIFICATIONS).getManager().setMentionable(false).queue();
				});
			});
			
			
		}
		
	}

}
