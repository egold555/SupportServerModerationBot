package org.golde.discordbot.supportserver.command.chatmod;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.event.MuteManager;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandUnmute extends ChatModCommand {

	public CommandUnmute(@Nonnull ESSBot bot) {
		super(bot, "unmute", "<player> [reason]", "unmute a player", "um");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();
		Member member = event.getMember();

		Long targetId = getMember(event, args, 1);
		
		if(args.isEmpty() || targetId == null)
		{
			replyError(tc, "Please provide the name of a player to unmute!");
			return;
		}
		else {

			Member target = event.getGuild().getMemberById(targetId);

			if(target == null) {
				replyWarning(tc, "I could not find this player on the guild, but will attempt to preform the given action anyway...");
			}

			String reason = String.join(" ", args.subList(2, args.size()));



			if (target != null && (!member.hasPermission(Permission.VOICE_MUTE_OTHERS) || !member.canInteract(target))) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}


			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			reply(tc, MuteManager.unmuteUser(bot, targetId, event.getMember().getIdLong(), reason));



		}
	}

}
