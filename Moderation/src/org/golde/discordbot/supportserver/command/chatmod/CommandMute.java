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

		if(event.getArgs().isEmpty())
		{
			replyError(tc, "Please provide the name of a player to mute!");
			return;
		}
		else {

			Member target = getMember(event, args, 1);

			if (args.isEmpty() || target == null) {
				replyError(tc, "I could not find that person!");
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


			if (!member.hasPermission(Permission.VOICE_MUTE_OTHERS) || !member.canInteract(target)) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}

			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			this.reply(tc, MuteManager.muteUser(bot, target.getIdLong(), event.getMember().getIdLong(), reason, timeUntilUnmute));


		}
	}

}
