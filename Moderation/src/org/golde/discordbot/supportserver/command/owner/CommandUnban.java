package org.golde.discordbot.supportserver.command.owner;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.command.owner.OwnerCommand;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.ModerationBot;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.event.MuteManager;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandUnban extends OwnerCommand {

	public CommandUnban(@Nonnull ESSBot bot) {
		super(bot, "unban", "<player> [reason]", "unban a player", "ub");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();

		if(event.getArgs().isEmpty())
		{
			replyError(tc, "Please provide the name of a player to unban!");
			return;
		}
		else {


			long target = Long.parseLong(args.get(1));

			String reason = String.join(" ", args.subList(2, args.size()));

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			Long offenceId = Offence.addOffence(bot, new Offence(target, event.getMember().getIdLong(), ModAction.UNBAN, reason));
			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.UNBAN, 
					event.getMember().getUser(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target + ">"}, 
						new String[] {"Reason:", reason},
						new String[] {"Offence ID:", Long.toString(offenceId)},
					}
					);
			ModLog.log(event.getGuild(), actionEmbed);
			event.getGuild().unban(Long.toString(target)).queue();
		}
	}

}
