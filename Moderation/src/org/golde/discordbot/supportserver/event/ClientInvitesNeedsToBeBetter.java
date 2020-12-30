package org.golde.discordbot.supportserver.event;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.database.Database;

import net.dv8tion.jda.api.entities.Invite.InviteType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.entities.InviteImpl;

public class ClientInvitesNeedsToBeBetter extends EventBase implements ICanHasDatabaseFile {

	public ClientInvitesNeedsToBeBetter(ESSBot bot) {
		super(bot);
	}

	private static final Pattern pattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?");

	/*
	 RANT:
	 
	 Discord should use UUID's for snowflake ids. JSON (and others) can not store longs in exact presision. 
	 
	 */
	static Set<String> alreadyUsedServers = new HashSet<String>();
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		//would need to cache all messages, I am not willing atm to do that
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		User user = event.getAuthor();
		Message m = event.getMessage();
		TextChannel tc = event.getChannel();
		if(tc.getIdLong() != Channels.UserContributions.CLIENT_INVITES) {
			return;
		}
		
		if(user.isBot() || user.isFake()) {
			return;
		}

		String msg = m.getContentRaw();

		Matcher matcher = pattern.matcher(msg);

		if(matcher.find()) {

			String matches = matcher.group();

			if(matches.startsWith("https://discord.gg/") || matches.startsWith("https://discordapp.com/invite/")) {
				matches = matches.replace("https://discord.gg/", "").replace("https://discordapp.com/invite/", "");

				parseInvite(matches, m);

			}
			else {
				replyError(tc, "Please only post a client invite and a description in this channel. Chatting isn't allowed.", 20);
				m.delete().queue();
			}
		}
		else {
			replyError(tc, "Please only post a client invite and a description in this channel. Chatting isn't allowed.", 20);
			m.delete().queue();
		}
	}

	private void parseInvite(String matches, Message m) {

		System.out.println(matches);
		
		InviteImpl.resolve(bot.getJda(), matches, true).queue(success -> {

			if(success.getType() == InviteType.GUILD) {
				net.dv8tion.jda.api.entities.Invite.Guild guildGot = success.getGuild();
				String guildGotId = guildGot.getId();
				
				if(alreadyUsedServers.contains(guildGotId)) {
					m.delete().queue();
					replyError(m.getChannel(), "An invite for \"" + guildGot.getName() + "\" has already been posted in this channel! If you think this is a bug, please contact Eric.", 20);
				}
				else {
					alreadyUsedServers.add(guildGotId);
					Database.saveToFile(alreadyUsedServers, "client-invites");
				}
			}

		});;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void reload() {
		alreadyUsedServers.clear();
		alreadyUsedServers = (Set<String>)Database.jsonFile2JavaObject("client-invites", Set.class);
	}

}
