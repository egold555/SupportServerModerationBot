package org.golde.discordbot.supportserver.command.mod;

import java.util.List;

import org.golde.discordbot.supportserver.constants.Roles;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.StringUtil;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class CommandUnmute extends ModCommand {

	public CommandUnmute() {
		super("unmute", "<player> [reason]", "unmute a player", "um");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		//Member member = event.getMember();

		if(event.getArgs().isEmpty())
		{
			event.replyError("Please provide the name of a player to unmute!");
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

			Member selfMember = event.getGuild().getSelfMember();

			if (!selfMember.hasPermission(Permission.VOICE_MUTE_OTHERS) || !selfMember.canInteract(target) || selfMember.equals(target)) {
				event.replyError("I can't mute that user or I don't have the mute members permission");
				return;
			}

//			if(!target.canInteract(target)) {
//				event.replyError("Sorry you can not interact with that user! Please contact Eric.");
//				return;
//			}

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			Role mutedRole = Roles.MUTED.getRole();

			event.getGuild().removeRoleFromMember(target, mutedRole).queue();

			Database.addOffence(target.getIdLong(), event.getAuthor().getIdLong(), ModAction.UNMUTE, reason);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					ModAction.UNMUTE, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtil.abbreviate(reason, 250)}
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
