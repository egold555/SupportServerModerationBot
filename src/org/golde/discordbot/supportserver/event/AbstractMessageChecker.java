package org.golde.discordbot.supportserver.event;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class AbstractMessageChecker extends ListenerAdapter {

	protected abstract boolean checkMessage(Member sender, String text);
	protected abstract void takeAction(Guild guild, Member target, Message msg);
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		boolean result = checkMessage(event.getMember(), event.getMessage().getContentStripped());
		if(result) {
			takeAction(event.getGuild(), event.getMember(), event.getMessage());
		}
	}

	@Override
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
		boolean result = checkMessage(event.getMember(), event.getMessage().getContentStripped());
		if(result) {
			takeAction(event.getGuild(), event.getMember(), event.getMessage());
		}
	}
	
}
