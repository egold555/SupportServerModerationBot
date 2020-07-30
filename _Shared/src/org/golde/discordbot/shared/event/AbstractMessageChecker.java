package org.golde.discordbot.shared.event;

import org.golde.discordbot.shared.ESSBot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;

public abstract class AbstractMessageChecker extends EventBase {

	public AbstractMessageChecker(ESSBot bot) {
		super(bot);
	}
	protected abstract boolean checkMessage(Member sender, Message msg);
	protected abstract void takeAction(Guild guild, Member target, Message msg);

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		//omg
		if(event.getAuthor().isBot() || event.getAuthor().isFake()) {
			return;
		}

		boolean result = checkMessage(event.getMember(), event.getMessage());
		if(result) {
			takeAction(event.getGuild(), event.getMember(), event.getMessage());
		}
	}

	@Override
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {

		//omg
		if(event.getAuthor().isBot() || event.getAuthor().isFake()) {
			return;
		}

		boolean result = checkMessage(event.getMember(), event.getMessage());
		if(result) {
			takeAction(event.getGuild(), event.getMember(), event.getMessage());
		}
	}

}
