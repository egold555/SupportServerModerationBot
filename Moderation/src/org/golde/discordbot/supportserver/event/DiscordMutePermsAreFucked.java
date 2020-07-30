package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordMutePermsAreFucked extends EventBase {

	public DiscordMutePermsAreFucked(ESSBot bot) {
		super(bot);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		if(event.getAuthor().isBot() || event.getAuthor().isFake()) {
			return;
		}
		
		if(event.getMember().getRoles().contains(event.getGuild().getRoleById(Roles.MUTED))) {
			if(event.getChannel().getIdLong() != Channels.REDEEM_YOUR_MUTE) {
				event.getMessage().delete().queue();
				replyError(event.getChannel(), "You are muted, you can not speak.", 2);
			}
		}
	}
	
}
