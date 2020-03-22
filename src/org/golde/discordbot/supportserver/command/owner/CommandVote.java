package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandVote extends OwnerCommand {

	public CommandVote() {
		super("vote", "<code>", "Apply your fote to a specific poll", "v");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
	}

}
