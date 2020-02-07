package org.golde.discordbot.supportserver.util;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModLog {

	

	public static final void log(Guild guild, MessageEmbed embed) {
		TextChannel logChannel = guild.getTextChannelById(Channels.MOD_LOGS);
		logChannel.sendMessage(embed).queue();
	}

	public enum ModAction {
		KICK, BAN, SOFT_BAN, MUTE, UNMUTE, MESSAGE_DELETED, PRUNE, LOCK, UNLOCK;
	}

	//should take a list of String[] key, value. Every key value pair is a addField function
	@Deprecated
	public static final MessageEmbed getActionTakenEmbed(ModAction action, @Nonnull User user, @Nonnull Member offender, @Nonnull String reason) {

		String[][] text = new String[][] {
			null,
			null,
		};

		if(offender != null) {
			text[0] = new String[] {"Offender: ", "<@" + offender.getId() + ">"};
		}

		if(reason != null) {
			text[1] = new String[] {"Reason:", StringUtil.abbreviate(reason, 250)};
		}

		return getActionTakenEmbed(action, user, text);

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

		return eb.build();
	}

	/**
	 * Custom Embed! ~ Si1kn
	 */

	public static final MessageEmbed report( @Nonnull User user, @Nonnull Member offender, @Nonnull String reason, Date time) {


		String[][] text = new String[][] {
				null,
				null,
		};

		if(offender != null) {
			text[0] = new String[] {"Offender: ", "<@" + offender.getId() + ">"};
		}

		if(reason != null) {
			text[1] = new String[] {"Reporter:", StringUtil.abbreviate(reason, 250)};
		}

		return report(user,offender,reason,time);

	}

}
