package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.SimpleUser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

public class CommandTest extends OwnerCommand {

	private final EventWaiter waiter;

	public CommandTest(EventWaiter waiter) {
		this.waiter = waiter;
		this.name = "test";
		this.help = "Its for testing, thats all it does";
		this.requiredRole = "Founder";
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		SimpleUser user = Database.getUser(8895);

//		user.setLastKnownUsername("HIIII#1234");
//		user.addOffence(new Offence(ModAction.BAN, 5678L, "Tesdasdasdst"));
		
		String json = gson.toJson(Database.getAllUsers());
		
		System.out.println(json);
		
		
	}

}
