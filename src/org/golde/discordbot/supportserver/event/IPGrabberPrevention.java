package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.Roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class IPGrabberPrevention extends AbstractMessageChecker {

	private static final String[] BAD_URLS = {
			"ps3cfw.com",
			"whatstheirip.com",
			"blasze.tk",
			"blasze.com",
			"iplogger.org",
			/*Grabify*/
			"grabify.link",
			"bmwforum.co",
			"leancoding.co",
			"spottyfly.com",
			"stopify.co",
			"yoütu.be",
			"discörd.com",
			"minecräft.com",
			"freegiftcards.co",
			"disçordapp.com",
			"fortnight.space",
			"fortnitechat.site",
			"joinmy.site",
			"crabrave.pw",
			"curiouscat.club",
			"catsnthings.fun",
			"mypic.icu",
			"yourtube.site",
			"youtubeshort.watch",
			"catsnthing.com",
			"youtubeshort.pro"
	};


	

	@Override
	protected boolean checkMessage(Member sender, String text) {

		for(String bad : BAD_URLS) {
			if(text.toLowerCase().contains(bad.toLowerCase())) {
				return true;
			}
		}

		return false;

	}

	//target is null if its a webhook
	//Fix
	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {

		//delete their message
		msg.delete().queue();

		//mute them
		Role mutedRole = Roles.MUTED.getRole();

		guild.addRoleToMember(target, mutedRole).queue();

		MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(ModLog.ModAction.MUTE, guild.getSelfMember().getUser(), target, "IP grabber link");
		ModLog.log(guild, actionEmbed);

		target.getUser().openPrivateChannel().queue((dmChannel) ->
		{
			dmChannel.sendMessage(actionEmbed).queue();

		});



	}

}
