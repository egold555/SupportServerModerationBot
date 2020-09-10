package org.golde.discordbot.supportserver.util;

import java.time.Instant;

import org.golde.discordbot.supportserver.ModerationBot;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ModLog {

	public static final void log(Guild guild, MessageEmbed embed) {
		TextChannel logChannel = guild.getTextChannelById(Channels.Logs.MODERATION_LOGS);
		logChannel.sendMessage(embed).queue();
	}

	public enum ModAction {
		KICK, BAN, MUTE, UNMUTE, MESSAGE_DELETED, PRUNE, LOCK, UNLOCK, WARN, ROLE_CHANGE, REMOVE, TICKET_CLOSE;
	}

	public static final MessageEmbed getActionTakenEmbed(ESSBot bot, ModAction action, User mod, String[]... text) {
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
		eb.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());

		return eb.build();
	}

}
