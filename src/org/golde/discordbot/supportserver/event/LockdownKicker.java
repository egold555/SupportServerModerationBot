package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.command.mod.CommandPanic;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LockdownKicker extends ListenerAdapter {

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
