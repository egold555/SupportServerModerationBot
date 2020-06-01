package org.golde.discordbot.supportserver.command;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.golde.discordbot.supportserver.Main;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public abstract class BaseCommand extends Command {

	public static final String PREFIX = ";";
	
	protected static final Category CATEGORY_EVERYONE;
	protected static final Category CATEGORY_MODERATOR;
	protected static final Category CATEGORY_OWNER;
	protected static final Category CATEGORY_BETA;
	
	static {
		CATEGORY_EVERYONE = new Category("Everyone");
		CATEGORY_MODERATOR = new Category("Moderator");
		CATEGORY_OWNER = new Category("Founder");
		CATEGORY_BETA = new Category("Discord Beta Tester");
	}
	
	public BaseCommand(@Nonnull String nameIn, @Nullable String argsIn, @Nullable String helpIn, @Nullable String... aliasesIn) {
		
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
		return PREFIX + this.name + " " + this.arguments;
	}
	
	
	
	public enum EnumReplyType {
		SUCCESS("**Success!**", Color.GREEN), 
		WARNING("**Warning!**", Color.YELLOW), 
		ERROR("**Error!**", Color.RED), 
		NONE(null, new Color(155, 89, 182));
		
		private final String title;
		private final Color color;
		private EnumReplyType(String title, Color color) {
			this.title = title;
			this.color = color;
		}
	}
	
	private static final MessageEmbed getReplyEmbed(EnumReplyType type, String title, String desc) {
		
		EmbedBuilder builder = new EmbedBuilder();
		if(type.title == null) {
			builder.setTitle(type.title);
		}
		else {
			builder.setTitle(title);
		}
		builder.setColor(type.color);
		builder.setDescription(desc);
		builder.setTimestamp(Instant.now());
		builder.setFooter(Main.getJda().getSelfUser().getAsTag(), Main.getJda().getSelfUser().getAvatarUrl());
		return builder.build();
	}
	
	private static void reply(MessageChannel channel, EnumReplyType type, String title, String desc) {
		channel.sendMessage(getReplyEmbed(type, title, desc)).queue();
	}
	
	private static void reply(MessageChannel channel, EnumReplyType type, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		channel.sendMessage(getReplyEmbed(type, title, desc)).queue(success -> {
			success.delete().queueAfter(secondsUntilDelete, TimeUnit.SECONDS, success2 -> {
				if(finished != null) {
					finished.accept(null);
				}
				
			});
		});;
	}
	
	public static void replySuccess(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.SUCCESS, null, desc);
	}
	
	public static void replySuccess(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.SUCCESS, null, desc, secondsUntilDelete, null);
	}
	
	public static void replySuccess(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.SUCCESS, null, desc, secondsUntilDelete, finished);
	}
	
	public static void replyWarning(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.WARNING, null, desc);
	}
	
	public static void replyWarning(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.WARNING, null, desc, secondsUntilDelete, null);
	}
	
	public static void replyWarning(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.WARNING, null, desc, secondsUntilDelete, finished);
	}
	
	public static void replyError(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.ERROR, null, desc);
	}
	
	public static void replyError(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.ERROR, null, desc, secondsUntilDelete, null);
	}
	
	public static void replyError(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.ERROR, null, desc, secondsUntilDelete, finished);
	}
	
	public static void reply(MessageChannel channel, String desc) {
		reply(channel, EnumReplyType.NONE, null, desc);
	}
	
	public static void reply(MessageChannel channel, String title, String desc) {
		channel.sendMessage(getReplyEmbed(EnumReplyType.NONE, title, desc)).queue();
	}
	
	public static void reply(MessageChannel channel, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.NONE, null, desc, secondsUntilDelete, null);
	}
	
	public static void reply(MessageChannel channel, String title, String desc, int secondsUntilDelete) {
		reply(channel, EnumReplyType.NONE, title, desc, secondsUntilDelete, null);
	}
	
	public static void reply(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.NONE, null, desc, secondsUntilDelete, finished);
	}
	
	public static void reply(MessageChannel channel, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		reply(channel, EnumReplyType.NONE, title, desc, secondsUntilDelete, finished);
	}
	
	@Override
	protected void execute(CommandEvent event) {
		
		List<String> toPass = new ArrayList<String>();
		
		if(event.getArgs() != null && event.getArgs().length() > 0) {
			final String[] split = event.getMessage().getContentRaw().replaceFirst(
	                "(?i)" + Pattern.quote(PREFIX), "").split("\\s+");
			
			toPass = Arrays.asList(split);
		}
		
		//delete their command
		//event.getMessage().delete().queue();
		
		execute(event, toPass);
		
	}
	
	protected abstract void execute(CommandEvent event, List<String> args);
	
	
}
