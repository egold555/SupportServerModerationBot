package org.golde.discordbot.supportserver.command.mod.wip;

import java.util.List;

import org.golde.discordbot.supportserver.command.mod.ModCommand;
import org.golde.discordbot.supportserver.util.ModLog;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CommandSoftBan extends ModCommand {

	public static final int DEL_DAYS = 7;

	public CommandSoftBan() {
		this.guildOnly = true;
		this.name = "softban";
		this.help = "soft ban a player";
		this.arguments = "<player> [reason]";
		this.aliases = new String[]{"sb"};
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		Member member = event.getMember();

		if(!isModerator(member)) {
			event.replyError("You are not a moderator!");
			return;
		}

		if(event.getArgs().isEmpty())
		{
			event.replyError("Please provide the name of a player to kick!");
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

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			final String reasonFinal = reason;

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(ModLog.ModAction.SOFT_BAN, event.getAuthor(), target, reason);
			ModLog.log(event.getGuild(), actionEmbed);

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue((unused1) ->
				{
					final String TARGET_ID = target.getId();
					event.getGuild().ban(target, DEL_DAYS, String.format("Soft-Banned by: %#s, with reason: %s",
							event.getAuthor(), reasonFinal)).queue((unused2) -> {
								event.getGuild().unban(TARGET_ID);
								event.replySuccess("Success!");
							});
				});

			});




		}
	}

}
