package org.golde.discordbot.website.command.owner;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandTest extends OwnerCommand {

	public CommandTest(ESSBot bot) {
		super(bot, "test", null, "Hey, Does this work?");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		reply(event.getTextChannel(), "Hello World!");
	}
	

}
