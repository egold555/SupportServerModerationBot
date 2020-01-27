package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

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

		
		final long role_ = 667224721544183838L;
		
		Guild g = event.getGuild();
		
		Role role = g.getRoleById(role_);
		
		role.getManager().setMentionable(true).queue(success1 -> {
			
			g.getTextChannelById(604769552416374807L).sendMessage("Hello! This is a test announcement. Only I can ping " + role.getAsMention() + " !").queue(success2 -> {
				
				role.getManager().setMentionable(false).queue();
			
			});
			
				
			
		});		

	}

	

}
