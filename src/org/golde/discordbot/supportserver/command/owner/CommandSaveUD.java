package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import org.golde.discordbot.supportserver.database.Database;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandSaveUD extends OwnerCommand {

	public CommandSaveUD() {
		this.name = "saveUD";
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		Database.saveToFile(Database.getUsernameCashe(), Database.USERNAME_CACHE_FILE);
		event.replySuccess("success");
		
	}

}
