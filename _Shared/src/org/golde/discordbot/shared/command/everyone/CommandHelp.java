package org.golde.discordbot.shared.command.everyone;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class CommandHelp extends EveryoneCommand {

	public CommandHelp(@Nonnull ESSBot bot) {
		super(bot, "help", null, "What are all the commands again?!");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		StringBuilder builder = new StringBuilder();
		Category category = null;

		for(Command command : event.getClient().getCommands()) {
			if(can(event.getMember(), command.getCategory().getName())) {

				if(!Objects.equals(category, command.getCategory())){
					category = command.getCategory();
					builder.append("\n\n  __").append(category.getName()).append("__:");
				}

				builder.append("\n**").append("        •   ").append(event.getClient().getPrefix()).append(command.getName())
				.append(command.getArguments()==null ? "**" : " "+command.getArguments()+"**")
				.append(" - ").append(command.getHelp());
			}
		}
		builder.append("\n\nDo not include <> nor [] - <> means required and [] means optional."
				+ "\nFor additional help, contact **Eric Golde#3352**");


		reply(event.getChannel(), "__**Commands**:__", builder.toString());
	}

	private boolean can(Member member, String name) {

		if(name.equals("Everyone")) {
			return true;
		}

		for(Role r : member.getRoles()) {
			if(r.getName().equals(name)) {
				return true;
			}
		}

		return false;

	}

}
