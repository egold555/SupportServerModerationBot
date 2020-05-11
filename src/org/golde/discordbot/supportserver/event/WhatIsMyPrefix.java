package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.supportserver.command.BaseCommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class WhatIsMyPrefix extends AbstractMessageChecker {

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentStripped();
		return (text.startsWith("@Support Server Bot") && text.length() < 20);
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		msg.getTextChannel().sendMessage("My prefix is **" + BaseCommand.PREFIX + "** .").queue();;
	}

}
