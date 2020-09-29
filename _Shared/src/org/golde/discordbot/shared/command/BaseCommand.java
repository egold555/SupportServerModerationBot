package org.golde.discordbot.shared.command;

import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.util.EnumReplyType;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class BaseCommand extends Command {

	protected final ESSBot bot;

	public BaseCommand(@Nonnull ESSBot bot, @Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		this.bot = bot;
		this.name = nameIn;
		if(argsIn != null) {
			this.arguments = argsIn;
		}

		if(helpIn != null) {
			this.help = helpIn;
		}

		if(aliasesIn != null && aliasesIn.length > 0) {
			this.aliases = aliasesIn;
		}


	}

	protected String getHelpReply() {
		return bot.getPrefix() + this.name + " " + this.arguments;
	}

	/**
	 * Gets a member from a given String array and index
	 * @param evt The command event 
	 * @param args The String array that contains the member
	 * @param expecting The index of the array where we expect the user to reside
	 * @return The member at the index, or null if none is found
	 */
	protected Member getMember(CommandEvent evt, List<String> args, int expecting) {
		Guild guild = evt.getGuild();

		if(args.size()-1 < expecting) {return null;}

		try {
			String id = args.get(expecting);
			id = id.replace("<@!", "").replace(">", "");

			Member member = guild.getMemberById(id);

			if(member != null) {
				return member;
			}

		}
		catch(NumberFormatException ignored) {}

		return null;
	}
	
	protected final EmbedBuilder getReplyEmbedRaw(EnumReplyType type, String title, String desc) {

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
		return builder;
	}

	protected final MessageEmbed getReplyEmbed(EnumReplyType type, String title, String desc) {
		return getReplyEmbedRaw(type, title, desc).build();
	}
	
	protected void reply(MessageChannel channel, EmbedBuilder builder) {
		channel.sendMessage(builder.build()).queue();
	}

	private void reply(MessageChannel channel, EnumReplyType type, String title, String desc) {
		channel.sendMessage(getReplyEmbed(type, title, desc)).queue();
	}

	private void reply(MessageChannel channel, EnumReplyType type, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
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

	@Override
	protected void execute(CommandEvent event) {

		List<String> toPass = new ArrayList<String>();

		if(event.getArgs() != null && event.getArgs().length() > 0) {
			final String[] split = event.getMessage().getContentRaw().replaceFirst(
					"(?i)" + Pattern.quote(bot.getPrefix()), "").split("\\s+");

			toPass = Arrays.asList(split);
		}

		//delete their command
		//event.getMessage().delete().queue();

		execute(event, toPass);

	}

	protected abstract void execute(CommandEvent event, List<String> args);


}
