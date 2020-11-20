package org.golde.discordbot.shared.command.owner;

import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandReload extends OwnerCommandDangerous {

	public CommandReload(@Nonnull ESSBot bot) {
		super(bot, "reload", null, "reloads some config files", "rl");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		final long currentTime = System.currentTimeMillis();
		bot.private_onReload();
		long timeItTook = System.currentTimeMillis() - currentTime;
		replySuccess(event.getChannel(), "Reloaded bot in " + timeItTook + "ms.");
	}

}
