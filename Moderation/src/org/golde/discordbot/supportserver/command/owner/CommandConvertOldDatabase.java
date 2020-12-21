package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommandDangerous;
import org.golde.discordbot.supportserver.database.old.OLDParser;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandConvertOldDatabase extends  OwnerCommandDangerous {

	public CommandConvertOldDatabase(ESSBot bot) {
		super(bot, "pleasedontfuckupthedatabase", null, "This is a dangerous command");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		OLDParser.go(bot);
		replyWarning(event.getChannel(), "No clue of this worked, check the console");
	}

}
