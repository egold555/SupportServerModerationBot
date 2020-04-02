package org.golde.discordbot.supportserver.command.everyone;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.golde.discordbot.supportserver.command.BaseCommand;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandPing extends EveryoneCommand {

	public CommandPing() {
		super("ping", null, "checks the bot's latency", "pong");
        this.guildOnly = false;
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
	}

}
