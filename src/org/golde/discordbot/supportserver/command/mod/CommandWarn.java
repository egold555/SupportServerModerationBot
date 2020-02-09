package org.golde.discordbot.supportserver.command.mod;

import java.util.List;

import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.StringUtil;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CommandWarn extends ModCommand {

	public CommandWarn() {
		this.name = "warn";
		this.aliases = new String[] {"wn", "w"};
		this.help = "warn a player";
		this.arguments = "<player> [reason]";
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		Member member = event.getMember();

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
			String reason = String.join(" ", args.subList(2, args.size()));

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			Database.addOffence(target.getIdLong(), member.getIdLong(), ModAction.WARN, reason);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					ModAction.WARN, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtil.abbreviate(reason, 250)},
						new String[] {"Warn Count:", Database.getUser(target.getIdLong()).getAmountOfWarns() + ""}
					}
					);


			ModLog.log(event.getGuild(), actionEmbed);

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue();

			});

		}



	}

}
