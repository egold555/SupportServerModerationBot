package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.supportserver.command.guildmod.GuildModCommand;
import org.golde.discordbot.supportserver.constants.Roles;
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

	public CommandUnmute() {
		super("unmute", "<player> [reason]", "unmute a player", "um");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();
		//Member member = event.getMember();

		if(event.getArgs().isEmpty())
		{
			replyError(tc, "Please provide the name of a player to unmute!");
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

			Member selfMember = event.getGuild().getSelfMember();

			if (!selfMember.hasPermission(Permission.VOICE_MUTE_OTHERS) || !selfMember.canInteract(target) || selfMember.equals(target)) {
				replyError(tc, "I can't mute that user or I don't have the mute members permission");
				return;
			}

//			if(!target.canInteract(target)) {
//				replyError(tc, "Sorry you can not interact with that user! Please contact Eric.");
//				return;
//			}

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			Role mutedRole = event.getGuild().getRoleById(Roles.MUTED);

			event.getGuild().removeRoleFromMember(target, mutedRole).queue();

			Database.addOffence(target.getIdLong(), event.getAuthor().getIdLong(), ModAction.UNMUTE, reason);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
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



			replySuccess(tc, "Success!");


		}
	}

}
