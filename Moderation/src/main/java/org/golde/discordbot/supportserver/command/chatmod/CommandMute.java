package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.util.DateUtil;
import org.golde.discordbot.supportserver.event.MuteManager;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandMute extends ChatModCommand {

	public CommandMute(@Nonnull ESSBot bot) {
		super(bot, "mute", "<player> [time] [reason]", "mute a player", "m");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();
		Member member = event.getMember();
		
		Long targetId = getMember(event, args, 1);

		if(args.isEmpty() || targetId == null)
		{
			replyError(tc, "Please provide the name of a player to mute!");
			return;
		}
		else {
			
			Member target = event.getGuild().getMemberById(targetId);


			if(target == null) {
				replyWarning(tc, "I could not find this player on the guild, but will attempt to preform the given action anyway...");
				replyError(tc, "The user must be online to preform this command.");
				return;
			}


			String timeString = args.get(2);
			String reason;
			Long timeUntilUnmute = null;

			if(timeString != null && !timeString.isEmpty() && Character.isDigit(timeString.charAt(0))) {
				timeUntilUnmute = DateUtil.parseDateDiff(timeString, true);
				if(timeUntilUnmute == null) {
					replyError(tc, "Invalid date/time specified!", "Please use the following format: ");
					return;
				}
				reason = String.join(" ", args.subList(3, args.size()));
			}
			else {
				reason = String.join(" ", args.subList(2, args.size()));
			}


			if (target != null && (!member.hasPermission(Permission.VOICE_MUTE_OTHERS) || !member.canInteract(target))) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			this.reply(tc, MuteManager.muteUser(bot, targetId, event.getMember().getIdLong(), reason, timeUntilUnmute));


		}
	}

}
