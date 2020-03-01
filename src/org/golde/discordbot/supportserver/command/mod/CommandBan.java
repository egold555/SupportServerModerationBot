package org.golde.discordbot.supportserver.command.mod;

import java.util.List;

import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.StringUtil;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CommandBan extends ModCommand {

	public static final int DEL_DAYS = 7;

	public CommandBan() {
		this.guildOnly = true;
		this.name = "ban";
		this.help = "ban a player";
		this.arguments = "<player> [reason]";
		this.aliases = new String[]{"b"};
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		Member member = event.getMember();

		if(event.getArgs().isEmpty())
		{
			event.replyError("Please provide the name of a player to ban!");
			return;
		}
		else {

			Member selfMember = event.getGuild().getSelfMember();
			List<Member> mentionedMembers = event.getMessage().getMentionedMembers();

			if (args.isEmpty() || mentionedMembers.isEmpty()) {
				event.replyError("Missing arguments");
				return;
			}

			Member target = mentionedMembers.get(0);
			String reason = String.join(" ", args.subList(2, args.size()));

			if (!selfMember.hasPermission(Permission.BAN_MEMBERS) || !selfMember.canInteract(target) || selfMember.equals(target)) {
				event.replyError("I can't ban that user or I don't have the ban members permission");
				return;
			}
			
			if(!member.canInteract(target)) {
				event.replyError("Sorry you can not interact with that user! Please contact Eric.");
				return;
			}

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			final String reasonFinal = reason;

			Database.addOffence(target.getIdLong(), event.getAuthor().getIdLong(), ModAction.BAN, reason);
			
			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					ModAction.BAN, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtil.abbreviate(reason, 250)}
					}
					);
			
			ModLog.log(event.getGuild(), actionEmbed);

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue((unused1) ->
				{
					event.getGuild().ban(target, DEL_DAYS, String.format("Banned by: %#s, with reason: %s",
							event.getAuthor(), reasonFinal)).queue();
				});

			});

			event.replySuccess("Success!");


		}
	}

}
