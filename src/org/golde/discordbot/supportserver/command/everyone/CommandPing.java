package org.golde.discordbot.supportserver.command.everyone;

import java.time.temporal.ChronoUnit;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandPing extends EveryoneCommand {

	private long ping = 0;
	private long websocket = 0;

	public CommandPing() {
		super("ping", null, "checks the bot's latency", "pong");
        this.guildOnly = false;
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {

		event.reply("Pinging......", m -> {
			ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
			websocket = event.getJDA().getGatewayPing();

			m.delete();
		});

		EmbedBuilder builder = new EmbedBuilder()
				.setTitle("Network Information")
				.addField("Ping: ", ping + " ms", false)
				.addField("Websocket: ", websocket + " ms", false)
				;

		event.getChannel().sendMessage(builder.build()).queue();

	}

}
