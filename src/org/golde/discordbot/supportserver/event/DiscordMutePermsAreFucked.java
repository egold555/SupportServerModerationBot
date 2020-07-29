package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.constants.Channels;
import org.golde.discordbot.supportserver.constants.Roles;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DiscordMutePermsAreFucked extends EventBase {

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
