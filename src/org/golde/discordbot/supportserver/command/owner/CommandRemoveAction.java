package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CommandRemoveAction extends OwnerCommand {

	public CommandRemoveAction() {
		super("removeAction", "<user> <enum>", "Removes a mod action from a given user", "ra");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		//Member member = event.getMember();

		if(event.getArgs().isEmpty())
		{
			event.replyError("Please provide the name of a player to mute!");
			return;
		}
		else {

			List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

			if (args.isEmpty() || mentionedMembers.isEmpty()) {
				event.replyError("Missing arguments");
				return;
			}

			Member target = mentionedMembers.get(0);
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
			
			Database.removeOffence(target.getIdLong(), action);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					ModAction.REMOVE, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Offence Removed:", enumValue},
						new String[] {"Current Offence Count:", Database.getUser(target.getIdLong()).getOffenceCount(action) + ""}
					}
					);


			ModLog.log(event.getGuild(), actionEmbed);

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue();

			});
			
			event.replySuccess("Success!");
		}
	}

}
