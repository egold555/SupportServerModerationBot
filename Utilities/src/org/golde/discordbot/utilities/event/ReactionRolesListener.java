package org.golde.discordbot.utilities.event;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.db.ICanHasDatabaseFile;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionRolesListener extends EventBase implements ICanHasDatabaseFile {

	private static final HashMap<String,Long> map = new HashMap<>();

	/*
	 Format of file:
	 <message id> <emoji id / unicode> <role id>
	 */
	
	@Override
	public void reload() {
		map.clear();
		try {
			Files.readAllLines(new File("res/role-reactions.txt").toPath(), StandardCharsets.UTF_8).forEach(line ->
			{
				String[] split = line.replace("\uFEFF", "").split(" ", 3);
				try
				{
					if(!split[0].startsWith("#")) {
						long messageId = Long.parseLong(split[0].trim());
						long roleId = Long.parseLong(split[2]);
						map.put(getKey(messageId, split[1]), roleId);
					}
					
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			});
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ReactionRolesListener(ESSBot bot) {
		super(bot);
		
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		Role role = event.getGuild().getRoleById(map.getOrDefault(getKey(event.getMessageIdLong(),event.getReactionEmote()), 0L));
		if(role != null) {
			event.getGuild().addRoleToMember(event.getMember(), role).queue();
		}
		
		
		//prevent unwanted reactions on reaction role messages
		for(String key : map.keySet()) {
			String msgId = key.split("-")[0];
			if(role == null && msgId.equals(event.getMessageId()) && !event.getUser().isBot()) {
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}

	}

	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event)
	{
		Role role = event.getGuild().getRoleById(map.getOrDefault(getKey(event.getMessageIdLong(),event.getReactionEmote()), 0L));
		if(role != null) {
			event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
		}
	}


	private static String getKey(long messageId, ReactionEmote emote)
	{
		if(emote.isEmote()) {
			return getKey(messageId, emote.getEmote().getId());
		}
		else if(emote.isEmoji()) {
			return getKey(messageId, emote.getEmoji());
		}
		
		//should never get to this point
		return null;
	}

	private static String getKey(long messageId, String emoji)
	{
		return messageId+"-"+emoji;
	}

}
