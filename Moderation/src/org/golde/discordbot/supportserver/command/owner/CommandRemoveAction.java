package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandRemoveAction extends OwnerCommand {

	public CommandRemoveAction(@Nonnull ESSBot bot) {
		super(bot, "removeAction", "<user> <enum> [count]", "Removes a mod action from a given user", "ra");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();
		Member target = getMember(event, args, 1);

		if (args.isEmpty() || target == null) {
			replyError(tc, "I could not find that person!");
			return;
		}

		String enumValue = args.get(2).toUpperCase();

		ModAction action = null;
		try {
			action = ModAction.valueOf(enumValue);
		}
		catch(Exception e) {
			event.replyError("`" + enumValue + "` is not a valid enum. Please use KICK, BAN, MUTE, WARN");
			return;
		}

		if(action == null) {
			return;
		}

		int amount  = 1;

		if(args.size() > 2) {
			try {
				amount = Integer.parseInt(args.get(3));
			}
			catch(NumberFormatException ignored){

			}
		}

		//I need to go back to coding school and redo the fuck at out this bot
		//worst way of getting this done, but fuck it I am the only user running the command and i just dont care rn
		for(int i = 0; i < amount; i++) {
			Database.removeOffence(target.getIdLong(), action);
		}


		MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
				bot,
				ModAction.REMOVE, 
				event.getAuthor(), 
				new String[][] {
					new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
					new String[] {"Offence Removed:", enumValue},
					new String[] {"Remove Count:", String.valueOf(amount)},
					new String[] {"Current Offence Count:", Database.getUser(target.getIdLong()).getOffenceCount(action) + ""}
				}
				);


		ModLog.log(event.getGuild(), actionEmbed);

		target.getUser().openPrivateChannel().queue((dmChannel) ->
		{
			dmChannel.sendMessage(actionEmbed).queue();

		});

		event.replySuccess("Successfully removed x" + amount + " of " + enumValue + " from " + target.getAsMention());

	}

}
