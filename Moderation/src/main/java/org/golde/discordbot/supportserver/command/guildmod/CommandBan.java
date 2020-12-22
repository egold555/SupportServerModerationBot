package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.util.DateUtil;
import org.golde.discordbot.supportserver.database.Offence;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandBan extends GuildModCommand {

	public static final int DEL_DAYS = 7;

	public CommandBan(@Nonnull ESSBot bot) {
		super(bot, "obliterate", "<player> [time] [reason]", "ban a player", "b", "ban", "taticalnuke", "nuke");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();

		Long targetId = getMember(event, args, 1);
		
		if(args.isEmpty() || targetId == null)
		{
			replyError(tc, "Please provide the name of a player to obliterate ;)");
			return;
		}
		else {

			Member target = event.getGuild().getMemberById(targetId);
			

			if(target == null) {
				replyWarning(tc, "I could not find this player on the guild, but will attempt to preform the given action anyway...");
			}

			String timeString = args.get(2);
			String reason;
			Long timeUntilUnban = null;

			if(timeString != null && !timeString.isEmpty() && Character.isDigit(timeString.charAt(0))) {
				timeUntilUnban = DateUtil.parseDateDiff(timeString, true);
				if(timeUntilUnban == null) {
					replyError(tc, "Invalid date/time specified!", "Please use the following format: ");
					return;
				}
				reason = String.join(" ", args.subList(3, args.size()));
			}
			else {
				reason = String.join(" ", args.subList(2, args.size()));
			}

			if (target != null && !event.getMember().canInteract(target) || target.getUser().isBot() || target.getUser().isFake()) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}


			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			final String reasonFinal = reason;

			Long offenceId = Offence.addOffence(bot, new Offence(targetId, event.getAuthor().getIdLong(), ModAction.BAN, reason, timeUntilUnban));
			
			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.BAN, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + targetId + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)},
						new String[] {"Expires:", timeUntilUnban != null ? StringUtils.abbreviate(DateUtil.formatDateDiff(timeUntilUnban), 250) : "Not specified"},
						new String[] {"Offence ID:", Long.toString(offenceId)}
					}
					);

			ModLog.log(event.getGuild(), actionEmbed);
			
			if(target != null) {
				tryToDmUser(target, actionEmbed);
			}
			
			event.getGuild().ban(targetId.toString(), DEL_DAYS, String.format("Banned by: %#s, with reason: %s",
					event.getAuthor(), reasonFinal)).queue();

			

			tc.sendMessage(SSEmojis.TATICAL_NUKE_INCOMING + " **Tatical Nuke, incoming!** " + SSEmojis.TATICAL_NUKE_INCOMING).queue(onSuccess -> {
				replySuccess(tc, "Successfully obliterated <@" + targetId + "> from ESS with the reason '**" + StringUtils.abbreviate(reasonFinal, 250) + "**'!");
				tc.sendMessage("https://tenor.com/view/explosion-explode-clouds-of-smoke-gif-17216934").queueAfter(2, TimeUnit.SECONDS);
			});
			
			


		}
	}

}
