package org.golde.discordbot.supportserver.event;

import java.awt.Color;
import java.time.Instant;

import org.golde.discordbot.supportserver.database.DeletedMessage;
import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;

public class EventDeletedMessageLogger extends EventBase {

	public EventDeletedMessageLogger(ESSBot bot) {
		super(bot);
	}
	
	@Override
	public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
		
		User user = event.getAuthor();
		Message msg = event.getMessage();
		if(user.isBot() || event.getMessage().isWebhookMessage()) {
			return;
		}
		
		long msgId = event.getMessageIdLong();
		DeletedMessage dm = DeletedMessage.get(bot, msgId);
		
		if(dm == null) {
			return;
		}
		
		String newText = null;
		if(msg.getContentStripped() != null && !msg.getContentStripped().isEmpty()) {
			newText = msg.getContentStripped();
		}
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Message Edited");
		builder.addField(" ", "------------------------------------------------------", false);
		builder.addField("User:", user.getAsMention(), true);
		builder.addField("Msg ID:", "" + msgId, true);
		builder.setDescription("**Old:**```" + dm.getText() + "```\n**New:**```" + newText + "```");
		builder.setColor(Color.GREEN);
		builder.setTimestamp(Instant.now());
		builder.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());
		event.getGuild().getTextChannelById(Channels.Logs.MESSAGE_LOGS).sendMessage(builder.build()).queue();
		
		
		//System.out.println("User '" + dm.getUser() + "' updated message '" + msgId + "'.");
		//System.out.println("	OLD: " + dm.getText());
		//System.out.println("	NEW: " + newText);
		dm.updateText(msgId, newText);
		
	}
	
	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
		
		long msgId = event.getMessageIdLong();
		DeletedMessage dm = DeletedMessage.get(bot, msgId);
		if(dm == null) {
			return;
		}
		dm.setHiddenBotInstance(bot);
		
		User user = event.getJDA().getUserById(dm.getUser());
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Message Deleted");
		builder.addField(" ", "------------------------------------------------------", false);
		builder.addField("User:", user.getAsMention(), true);
		builder.addField("Msg ID:", "" + msgId, true);
		builder.setDescription("**Text:**```" + dm.getText() + "```");
		builder.setColor(Color.RED);
		builder.setTimestamp(Instant.now());
		builder.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());
		event.getGuild().getTextChannelById(Channels.Logs.MESSAGE_LOGS).sendMessage(builder.build()).queue();
		
		//System.out.println("User '" + dm.getUser() + "' deleted the message '" + dm.getText() + "'.");
		dm.delete();
		
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		User user = event.getAuthor();
		Message msg = event.getMessage();
		if(user.isBot() || event.getMessage().isWebhookMessage()) {
			return;
		}
		
		String text = null;
		long msgId = msg.getIdLong();
		long userId = user.getIdLong();
		if(msg.getContentStripped() != null && !msg.getContentStripped().isEmpty()) {
			text = msg.getContentStripped();
		}
		
		DeletedMessage.addMessage(bot, new DeletedMessage(msgId, userId, text));
		
	}

}
