package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.supportserver.command.guildmod.old.CommandPanic;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public class LockdownKicker extends EventBase {

	public LockdownKicker(ESSBot bot) {
		super(bot);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {

		if(CommandPanic.locked) {

			event.getUser().openPrivateChannel().queue(privateChannel -> {

				privateChannel.sendMessage("Hi. We are currently under attack, so I unfortunetly can not let you into the server. "
						+ "Please try again in a few minutes.  \n\nhttps://discord.gg/M3PAyyy").queue(
								success -> {
									event.getMember().kick("[Moderation bot] Under attack!").queue();
								}, 
								fail -> {
									event.getMember().kick("[Moderation bot] Under attack!").queue();
								}
								);

			});

		}

	}

}
