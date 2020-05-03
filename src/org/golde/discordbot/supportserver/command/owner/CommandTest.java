package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

public class CommandTest extends OwnerCommandDangerous {

	private final EventWaiter waiter;

	public CommandTest(EventWaiter waiter) {
		super("test", null, "Its for testing, thats all it does");
		this.waiter = waiter;
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		

	}

}
