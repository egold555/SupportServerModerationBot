package org.golde.discordbot.supportserver.command.chatmod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.golde.discordbot.supportserver.ModerationBot;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.supportserver.util.ModLog;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

public class CommandPruneChat extends ChatModCommand {

	static final Set<Long> DENY_CHANNELS = new HashSet<Long>();
	static {
		DENY_CHANNELS.add(Channels.Logs.MODERATION_LOGS);
		DENY_CHANNELS.add(Channels.ModStuffThings.MOD_CHAT);
		DENY_CHANNELS.add(Channels.ModStuffThings.GUILD_MOD_CHAT);
		DENY_CHANNELS.add(Channels.Logs.CAPTCHA_LOGS);
		DENY_CHANNELS.add(Channels.Logs.JOIN_LOGS);
		DENY_CHANNELS.add(Channels.Logs.MESSAGE_LOGS);
	}
	
	public CommandPruneChat(@Nonnull ESSBot bot) {
		super(bot, "prune", "<amount>", "prunes the chat", "p", "prune", "cc", "clear", "purge");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		TextChannel tc = event.getTextChannel();
		//Member member = event.getMember();

		if(args.isEmpty())
		{
			replyError(tc, "You must add a number after Prune command to delete an amount of messages.");
			return;
		}
		else {

			//Parse String to int, detect it the input is valid.
			Integer msgs = 0;
			try {
				msgs = Integer.parseInt(args.get(1)); //because of the command message, thanks Crackle <3
			} 
			catch (NumberFormatException nfe) {
				replyError(tc, "Please enter a valid number.");
				return;
			}

			if(msgs <= 1 || msgs > 100) {
				replyError(tc, "Please enter a number between **2 ~ 100**.");
				return;
			}
			
			if(DENY_CHANNELS.contains(event.getTextChannel().getIdLong()) && event.getMember().getIdLong() != ModerationBot.getOwnerId()) {
				replyError(tc, SSEmojis.HAL9000 + " I'm sorry " + event.getMember().getAsMention() + ", but that channel is forbidden to be purged.");
				return;
			}
			
			MessageEmbed actionEmbed = ModLog.getActionTakenEmbed(bot, ModLog.ModAction.PRUNE, event.getAuthor(), new String[][] {
				new String[] {"Channel: ", "<#" + event.getChannel().getId() + ">"},
				new String[] {"Pruned: ", "" + msgs}
			});
			
			

			//Delete command call
			//event.getTextChannel().deleteMessageById(event.getMessage().getId()).complete();

			event.getTextChannel().getHistory().retrievePast(msgs).queue((List<Message> mess) -> {
				try {
					event.getTextChannel().deleteMessages(mess).queue(
							success ->
							replySuccess(tc, " `" + event.getArgs() + "` messages deleted."),
							error -> replyError(tc, " An Error occurred!")
							);
					ModLog.log(event.getGuild(), actionEmbed);
				} 
				catch (IllegalArgumentException iae) {
					replyError(tc, "Cannot delete messages older than 2 weeks.");
				} 
				catch (PermissionException pe) {
					throw pe;
				}
			});


		}
	}

}
