package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandWarn extends ChatModCommand {

	public CommandWarn(@Nonnull ESSBot bot) {
		super(bot, "warn", "<player> [reason]", "Warn a player", "wn", "w");
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

			Member target = getMember(event, args, 1);
			
			if (args.isEmpty() || target == null) {
				replyError(tc, "I could not find that person!");
				return;
			}

			String reason = String.join(" ", args.subList(2, args.size()));

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			if (!event.getMember().canInteract(target) || target.getUser().isBot() || target.getUser().isFake()) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}

			Long offenceId = Offence.addOffence(bot, new Offence(target.getIdLong(), event.getMember().getIdLong(), ModAction.WARN, reason));

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.WARN, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)},
						//new String[] {"Warn Count:", Database.getUser(target.getIdLong()).getAmountOfWarns() + ""},
						new String[] {"Offence ID:", Long.toString(offenceId)}
					}
					);


			ModLog.log(event.getGuild(), actionEmbed);

			tryToDmUser(target, actionEmbed);

			replySuccess(tc, "Successfully warned " + target.getAsMention() + " for '**" + StringUtils.abbreviate(reason, 250) + "**'!");

		}



	}

}
