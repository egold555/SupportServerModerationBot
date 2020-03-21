package org.golde.discordbot.supportserver.util;

import java.time.Instant;

import org.golde.discordbot.supportserver.Main;
import org.golde.discordbot.supportserver.constants.Channels;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ModLog {

	public static final void log(Guild guild, MessageEmbed embed) {
		TextChannel logChannel = guild.getTextChannelById(Channels.MOD_LOGS);
		logChannel.sendMessage(embed).queue();
	}

	public enum ModAction {
		KICK, BAN, MUTE, UNMUTE, MESSAGE_DELETED, PRUNE, LOCK, UNLOCK, WARN, ROLE_CHANGE, REMOVE;
	}

	public static final MessageEmbed getActionTakenEmbed(ModAction action, User mod, String[]... text) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.setTitle("Moderation action taken!");
		eb.addField("Action: ", action.name(), false);
		eb.addField("Moderator: ", "<@" + mod.getId() + ">", false);

		if(text != null) {
			for(String[] thing : text) {

				if(thing != null && thing.length == 2) {
					eb.addField(thing[0], thing[1], false);
				}

			}
		}

		eb.setTimestamp(Instant.now());
		eb.setFooter(Main.getJda().getSelfUser().getAsTag(), Main.getJda().getSelfUser().getAvatarUrl());

		return eb.build();
	}

}
