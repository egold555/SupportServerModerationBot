package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandUnmute extends ChatModCommand {

	public CommandUnmute(@Nonnull ESSBot bot) {
		super(bot, "unmute", "<player> [reason]", "unmute a player", "um");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();
		Member member = event.getMember();

		if(event.getArgs().isEmpty())
		{
			replyError(tc, "Please provide the name of a player to unmute!");
			return;
		}
		else {


			Member target = getMember(event, args, 1);

			if (args.isEmpty() || target == null) {
				replyError(tc, "I could not find that person!");
				return;
			}
			String reason = String.join(" ", args.subList(2, args.size()));



			if (!member.hasPermission(Permission.VOICE_MUTE_OTHERS) || !member.canInteract(target)) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}


			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			Role mutedRole = event.getGuild().getRoleById(Roles.MUTED);

			event.getGuild().removeRoleFromMember(target, mutedRole).queue();

			Database.addOffence(bot, target.getIdLong(), event.getAuthor().getIdLong(), ModAction.UNMUTE, reason);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.UNMUTE, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)}
					}
					);
			ModLog.log(event.getGuild(), actionEmbed);

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue();

			});



			replySuccess(tc, "Successfully un-muted " + target.getAsMention() + " for '**" + StringUtils.abbreviate(reason, 250) + "**'!");



		}
	}

}
