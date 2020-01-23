package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.entities.Guild;

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
		
		Guild g = event.getGuild();
		
		//event.getTextChannel().createPermissionOverride(Role)
		
		g.getCategories().get(0);
		
	}

}
