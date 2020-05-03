package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import org.golde.discordbot.supportserver.event.AutoCommonError;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandReload extends OwnerCommandDangerous {

	public CommandReload() {
		super("reload", null, "reloads some config files", "rl");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		AutoCommonError.reloadDB();
		event.reply("Reloaded.");
	}

}
