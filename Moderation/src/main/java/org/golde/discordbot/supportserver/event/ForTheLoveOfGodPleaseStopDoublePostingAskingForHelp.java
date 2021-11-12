package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.constants.Roles;
import org.golde.discordbot.shared.event.EventBase;
import org.golde.discordbot.shared.util.HashUtils;
import org.golde.discordbot.supportserver.database.ExpiredMessage;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ForTheLoveOfGodPleaseStopDoublePostingAskingForHelp extends EventBase {

	private static final long EXPIRES = 1000 * 60 * 1; //1m
	private static final int TRIGGER_LENGTH = 10;

	public ForTheLoveOfGodPleaseStopDoublePostingAskingForHelp(ESSBot bot) {
		super(bot);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		Guild g = event.getGuild();
		Member mem = event.getMember();
		TextChannel tc = event.getChannel();
		Message msg = event.getMessage();

		if(mem == null || mem.getUser() == null || mem.getUser().isBot() || event.getMessage().isWebhookMessage()) {
			return;
		}

		if(
				tc.getIdLong() == Channels.INeedHelp.HELP_WITH_ERROR_MESSAGES || 
				tc.getIdLong() == Channels.INeedHelp.JAVA_HELP || 
				tc.getIdLong() == Channels.INeedHelp.MCP_HELP || 
				tc.getIdLong() == Channels.INeedHelp.MY_CODE_ISNT_WORKING
				) {

			//if they are muted, ignore
			if(mem.getRoles().contains(g.getRoleById(Roles.MUTED))) {
				return;
			}

			//Chat moderators bypass this
			if(mem.getRoles().contains(g.getRoleById(Roles.CHAT_MODERATOR)) || mem.getRoles().contains(g.getRoleById(Roles.CODE_HELPER))) {
				return;
			}

			long userID = mem.getUser().getIdLong();
			long messageID = event.getMessageIdLong();
			String text = msg.getContentStripped();

			if(text.length() < TRIGGER_LENGTH) {
				return;
			}

			//commands
			if(text.startsWith(";") || text.startsWith(".") || text.startsWith("-") || text.startsWith(",") || text.startsWith("!")) {
				return;
			}

			String hash = HashUtils.md5(text.toLowerCase());

			try {

				boolean has = ExpiredMessage.hasUserSentThisHashBefore(bot, userID, hash);

				if(has) {
					msg.delete().queue();
					replyError(tc, "Duplicate message detected!", "You have already sent this message before, please don't send the same message to mutiple channels " + mem.getAsMention() + "!", 20);
					return;
				}
				else {
					ExpiredMessage.addMessage(bot, new ExpiredMessage(messageID, userID, hash, System.currentTimeMillis() + EXPIRES));

				}
			}
			catch(Throwable ex) {
				ex.printStackTrace();
				System.err.println("The hash: " + hash);
			}

		}

	}

}
