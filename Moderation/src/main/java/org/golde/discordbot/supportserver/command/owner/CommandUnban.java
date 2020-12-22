package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandUnban extends OwnerCommand {

	public CommandUnban(@Nonnull ESSBot bot) {
		super(bot, "unban", "<player> [reason]", "unban a player", "ub");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();

		Long targetId = getMember(event, args, 1);
		
		if(args.isEmpty() || targetId == null)
		{
			replyError(tc, "Please provide the name of a player to unban!");
			return;
		}
		else {

			Member target = event.getGuild().getMemberById(targetId);

			if(target == null) {
				replyWarning(tc, "I could not find this player on the guild, but will attempt to preform the given action anyway...");
			}


			String reason = String.join(" ", args.subList(2, args.size()));

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			Long offenceId = Offence.addOffence(bot, new Offence(targetId, event.getMember().getIdLong(), ModAction.UNBAN, reason));
			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.UNBAN, 
					event.getMember().getUser(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + targetId + ">"}, 
						new String[] {"Reason:", reason},
						new String[] {"Offence ID:", Long.toString(offenceId)},
					}
					);
			ModLog.log(event.getGuild(), actionEmbed);
			event.getGuild().unban(Long.toString(targetId)).queue();
		}
	}

}
