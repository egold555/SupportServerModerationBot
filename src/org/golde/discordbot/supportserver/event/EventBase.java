package org.golde.discordbot.supportserver.event;

import java.util.function.Consumer;

import org.golde.discordbot.supportserver.command.BaseCommand;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class EventBase extends ListenerAdapter {

	protected static final void replySuccess(MessageChannel channel, String desc) {
		BaseCommand.replySuccess(channel, desc);
	}
	
	protected static final void replySuccess(MessageChannel channel, String desc, int secondsUntilDelete) {
		BaseCommand.replySuccess(channel, desc, secondsUntilDelete);
	}
	
	protected static final void replySuccess(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		BaseCommand.replySuccess(channel, desc, secondsUntilDelete, finished);
	}
	
	protected static final void replyWarning(MessageChannel channel, String desc) {
		BaseCommand.replyWarning(channel, desc);
	}
	
	protected static final void replyWarning(MessageChannel channel, String desc, int secondsUntilDelete) {
		BaseCommand.replyWarning(channel, desc, secondsUntilDelete);
	}
	
	protected static final void replyWarning(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		BaseCommand.replyWarning(channel, desc, secondsUntilDelete, finished);
	}
	
	protected static final void replyError(MessageChannel channel, String desc) {
		BaseCommand.replyError(channel, desc);
	}
	
	protected static final void replyError(MessageChannel channel, String desc, int secondsUntilDelete) {
		BaseCommand.replyError(channel, desc, secondsUntilDelete);
	}
	
	protected static final void replyError(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		BaseCommand.replyError(channel, desc, secondsUntilDelete, finished);
	}
	
	protected static final void reply(MessageChannel channel, String title, String desc) {
		BaseCommand.reply(channel, title, desc);
	}
	
	protected static final void reply(MessageChannel channel, String desc) {
		BaseCommand.reply(channel, desc);
	}
	
	protected static final void reply(MessageChannel channel, String title, String desc, int secondsUntilDelete) {
		BaseCommand.reply(channel, title, desc, secondsUntilDelete);
	}
	
	protected static final void reply(MessageChannel channel, String desc, int secondsUntilDelete) {
		BaseCommand.reply(channel, desc, secondsUntilDelete);
	}
	
	protected static final void reply(MessageChannel channel, String title, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		BaseCommand.reply(channel, title, desc, secondsUntilDelete, finished);
	}
	
	protected static final void reply(MessageChannel channel, String desc, int secondsUntilDelete, Consumer<Void> finished) {
		BaseCommand.reply(channel, desc, secondsUntilDelete, finished);
	}
	
}
