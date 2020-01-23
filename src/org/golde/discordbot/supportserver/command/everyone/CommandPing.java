package org.golde.discordbot.supportserver.command.everyone;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.golde.discordbot.supportserver.command.BaseCommand;

import com.jagrosh.jdautilities.command.CommandEvent;

public class CommandPing extends BaseCommand {

	public CommandPing() {
		this.name = "ping";
        this.help = "checks the bot's latency";
        this.guildOnly = false;
        this.aliases = new String[]{"pong"};
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
        });
	}

}
