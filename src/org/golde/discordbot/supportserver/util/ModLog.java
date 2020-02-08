package org.golde.discordbot.supportserver.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.golde.discordbot.supportserver.constants.Channels;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ModLog {

	private static HashMap<Long, Integer> DB_WARNS = new HashMap<Long, Integer>();

	private static final String EOL = System.getProperty("line.separator");

	private static void saveDatabaseToFile() {

		try (Writer writer = new FileWriter("somefile.csv")) {
			for (Entry<Long, Integer> entry : DB_WARNS.entrySet()) {
				writer.append(Long.toString(entry.getKey()))
				.append(',')
				.append(Integer.toString(entry.getValue()))
				.append(EOL);
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}

	}
	
	public static void readDatabseFromFIle() {
		
	}

	public static final void log(Guild guild, MessageEmbed embed) {
		TextChannel logChannel = guild.getTextChannelById(Channels.MOD_LOGS);
		logChannel.sendMessage(embed).queue();
	}

	public enum ModAction {
		KICK, BAN, MUTE, UNMUTE, MESSAGE_DELETED, PRUNE, LOCK, UNLOCK, WARN;
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

		eb.setFooter(new Date().toString());

		return eb.build();
	}

}
