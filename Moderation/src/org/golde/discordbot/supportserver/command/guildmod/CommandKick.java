package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.MiscConstants;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandKick extends GuildModCommand {

	public CommandKick(@Nonnull ESSBot bot) {
		super(bot, "kick", "<player> [reason]", "kick a player", "k");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();

		Long targetId = getMember(event, args, 1);
		
		if(args.isEmpty() || targetId == null)
		{
			replyError(tc, "Please provide the name of a player to kick!");
			return;
		}
		else {

			Member target = event.getGuild().getMemberById(targetId);


			if(target == null) {
				replyWarning(tc, "I could not find this player on the guild, but will attempt to preform the given action anyway...");
			}

			String reason = String.join(" ", args.subList(2, args.size()));

			if (target != null && !event.getMember().canInteract(target) || target.getUser().isBot() || target.getUser().isFake()) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}


			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			final String reasonFinal = reason;

			Long offenceId = Offence.addOffence(bot, new Offence(targetId, event.getAuthor().getIdLong(), ModAction.KICK, reason));

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.KICK, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + targetId + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)},
						new String[] {"Offence ID:", Long.toString(offenceId)}
					}
					);
			ModLog.log(event.getGuild(), actionEmbed);

			
			if(target != null) {
				tryToDmUser(target, actionEmbed);
				tryToDmUser(target, "You can join back with this link: " + MiscConstants.DISCORD_INVITE);

			}
			
			event.getGuild().kick(targetId.toString(), String.format("Kick by: %#s, with reason: %s",
					event.getAuthor(), reasonFinal)).queue();



			replySuccess(tc, "Successfully kicked <@" + targetId + "> for '**" + StringUtils.abbreviate(reason, 250) + "**'!");



		}
	}

}
