package org.golde.discordbot.supportserver.command.guildmod;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.guildmod.GuildModCommand;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.database.Database;
import org.golde.discordbot.supportserver.util.ModLog;
import org.golde.discordbot.supportserver.util.ModLog.ModAction;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandBan extends GuildModCommand {

	public static final int DEL_DAYS = 7;

	public CommandBan(@Nonnull ESSBot bot) {
		super(bot, "obliterate", "<player> [reason]", "ban a player", "b", "ban", "taticalnuke", "nuke");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();

		if(event.getArgs().isEmpty())
		{
			replyError(tc, "Please provide the name of a player to obliterate ;)");
			return;
		}
		else {

			Member target = getMember(event, args, 1);

			if (target == null) {
				replyError(tc, "I could not find that person!");
				return;
			}


			String reason = String.join(" ", args.subList(2, args.size()));

			if (!event.getMember().canInteract(target) || target.getUser().isBot() || target.getUser().isFake()) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", I'm afraid I can't let you do that." );
				return;
			}


			if(reason == null || reason.isEmpty()) {
				reason = "No reason provided.";
			}

			final String reasonFinal = reason;

			Database.addOffence(target.getIdLong(), event.getAuthor().getIdLong(), ModAction.BAN, reason);

			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(
					bot,
					ModAction.BAN, 
					event.getAuthor(), 
					new String[][] {
						new String[] {"Offender: ", "<@" + target.getId() + ">"}, 
						new String[] {"Reason:", StringUtils.abbreviate(reason, 250)}
					}
					);

			ModLog.log(event.getGuild(), actionEmbed);
			
			event.getGuild().ban(target, DEL_DAYS, String.format("Banned by: %#s, with reason: %s",
					event.getAuthor(), reasonFinal)).queue();

			target.getUser().openPrivateChannel().queue((dmChannel) ->
			{
				dmChannel.sendMessage(actionEmbed).queue((unused1) ->
				{
					
				});

			});

			tc.sendMessage(SSEmojis.TATICAL_NUKE_INCOMING + " **Tatical Nuke, incoming!** " + SSEmojis.TATICAL_NUKE_INCOMING).queue(onSuccess -> {
				replySuccess(tc, "Successfully obliterated " + target.getAsMention() + " from ESS with the reason '**" + StringUtils.abbreviate(reasonFinal, 250) + "**'!");
				tc.sendMessage("https://tenor.com/view/explosion-explode-clouds-of-smoke-gif-17216934").queueAfter(2, TimeUnit.SECONDS);
			});
			
			


		}
	}

}
