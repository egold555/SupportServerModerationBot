package org.golde.discordbot.shared.event;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.util.EnumReplyType;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class EventBase extends ListenerAdapter {

	protected final ESSBot bot;
	public EventBase(@Nonnull ESSBot bot) {
		this.bot = bot;
	}
	
	protected static void tryToDmUser(Member member, MessageEmbed embed) {
		tryToDmUser(member, embed, null);
	}
	protected static void tryToDmUser(Member member, MessageEmbed embed, Runnable onFinishedTrying) {

		member.getUser().openPrivateChannel().queue((dmChannel) ->
		{
			dmChannel.sendMessage(embed).queue();
			if(onFinishedTrying != null) {
				onFinishedTrying.run();
			}
		}, fail -> {
			if(onFinishedTrying != null) {
				onFinishedTrying.run();
			}
		});
	}
	
	protected final MessageEmbed getReplyEmbed(EnumReplyType type, String title, String desc) {

		EmbedBuilder builder = new EmbedBuilder();
		if(title == null) {
			builder.setTitle(type.prefix + type.title);
		}
		else {
			builder.setTitle(type.prefix + title);
		}
		builder.setColor(type.color);
		builder.setDescription(desc);
		builder.setTimestamp(Instant.now());
		builder.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());
		return builder.build();
	}

	private void reply(MessageChannel channel, EnumReplyType type, String title, String desc) {
		channel.sendMessage(getReplyEmbed(type, title, desc)).queue();
	}

	private void reply(MessageChannel channel, EnumReplyType type, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		if(secondsUntilDelete > 0) {
			desc += "\n\n*This message will automatically self destruct in " + secondsUntilDelete + " seconds.*";
		}
		channel.sendMessage(getReplyEmbed(type, title, desc)).queue(success -> {
			try {
				success.delete().queueAfter(secondsUntilDelete, TimeUnit.SECONDS, success2 -> {
					if(finished != null) {
						finished.accept(null);
					}

				}, failed -> {
					if(finished != null) {
						finished.accept(null);
					}
				});
			}
			catch(Exception e) {
				if(finished != null) {
					finished.accept(null);
				}
			}
		});;
	}

	public void replySuccess(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.SUCCESS, null, desc);
	}

	public void replySuccess(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.SUCCESS, null, desc, secondsUntilDelete, null);
	}

	public void replySuccess(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.SUCCESS, null, desc, secondsUntilDelete, finished);
	}

	public void replySuccess(MessageChannel channel, String title, String desc) {
		reply(channel, EnumReplyType.SUCCESS, title, desc);
	}

	public void replySuccess(MessageChannel channel, String title, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.SUCCESS, title, desc, secondsUntilDelete, null);
	}

	public void replySuccess(MessageChannel channel, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.SUCCESS, title, desc, secondsUntilDelete, finished);
	}

	public void replyWarning(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.WARNING, null, desc);
	}

	public void replyWarning(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.WARNING, null, desc, secondsUntilDelete, null);
	}

	public void replyWarning(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.WARNING, null, desc, secondsUntilDelete, finished);
	}

	public void replyWarning(MessageChannel channel, String title, String desc) {
		reply(channel, EnumReplyType.WARNING, title, desc);
	}

	public void replyWarning(MessageChannel channel, String title, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.WARNING, title, desc, secondsUntilDelete, null);
	}

	public void replyWarning(MessageChannel channel, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.WARNING, title, desc, secondsUntilDelete, finished);
	}

	public void replyError(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.ERROR, null, desc);
	}

	public void replyError(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.ERROR, null, desc, secondsUntilDelete, null);
	}

	public void replyError(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.ERROR, null, desc, secondsUntilDelete, finished);
	}

	public void replyError(MessageChannel channel, String title, String desc) {
		reply(channel, EnumReplyType.ERROR, title, desc);
	}

	public void replyError(MessageChannel channel, String title, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.ERROR, title, desc, secondsUntilDelete, null);
	}

	public void replyError(MessageChannel channel, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.ERROR, title, desc, secondsUntilDelete, finished);
	}

	public void reply(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.NONE, null, desc);
	}

	public void reply(MessageChannel channel, String title, String desc) {
		channel.sendMessage(getReplyEmbed(EnumReplyType.NONE, title, desc)).queue();
	}

	public void reply(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.NONE, null, desc, secondsUntilDelete, null);
	}

	public void reply(MessageChannel channel, String title, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.NONE, title, desc, secondsUntilDelete, null);
	}

	public void reply(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.NONE, null, desc, secondsUntilDelete, finished);
	}

	public void reply(MessageChannel channel, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.NONE, title, desc, secondsUntilDelete, finished);
	}
	
}
