package org.golde.discordbot.supportserver.command.owner;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.VerificationLevel;
import net.dv8tion.jda.api.entities.GuildChannel;

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

		
		
		

	}

	

}
