package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandWarn extends ChatModCommand {

	public CommandWarn() {
		super("warn", "<player> [reason]", "Warn a player", "wn", "w");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		Member member = event.getMember();
		TextChannel tc = event.getTextChannel();

		if(event.getArgs().isEmpty())
		{
			event.replyError("Please provide the name of a player to mute!");
			return;
		}
		else {

			List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

			if (args.isEmpty() || mentionedMembers.isEmpty()) {
				replyError(tc, "Missing arguments");
				return;
			}

			Member target = mentionedMembers.get(0);
			String reason = String.join(" ", args.subList(2, args.size()));

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}
			
			if (!event.getMember().canInteract(target) || event.getMember().equals(target) || target.getUser().isBot() || target.getUser().isFake()) {
		           replyError(tc, "I'm sorry ~~Dave~~ " + event.getMember().getAsMention() + ", I'm afraid I can't do that" );
		            return;
		        }

			Database.addOffence(target.getIdLong(), member.getIdLong(), ModAction.WARN, reason);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					ModAction.WARN, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)},
						new String[] {"Warn Count:", Database.getUser(target.getIdLong()).getAmountOfWarns() + ""}
					}
					);


			ModLog.log(event.getGuild(), actionEmbed);

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue();

			});
			
			replySuccess(tc, "Success!");

		}



	}

}
