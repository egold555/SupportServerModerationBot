package org.golde.discordbot.supportserver.command.everyone;

import java.util.HashMap;
import java.util.List;

import org.golde.discordbot.supportserver.command.owner.OwnerCommand;

import com.jagrosh.jdautilities.command.CommandEvent;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandCommonResponse extends OwnerCommand {

	private static String helpErrorBuilt = "";

	private static final HashMap<String, String> RESPONSES = new HashMap<String, String>();
	static {
		
		RESPONSES.put("mcplog", "Can you please send me your MCP.log file %name%? It's located in <project>/logs/mcp.log . You will need to zip it up to send it over Discord.");
		
		for(int i = 0; i < RESPONSES.size(); i++) {
			helpErrorBuilt += RESPONSES.keySet().toArray(new String[0])[i] + "\n";
		}
	}

	public CommandCommonResponse() {
		super("commonResponse", "<response id> [member]. Leave blank for a list of all of them", "Sends a pre made common response. Kind of like common-errors", "cr");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		if(args.size() < 2) {
			replyError(event.getChannel(), "Please specify a error", helpErrorBuilt);
			return;
		}

		//event.getMessage().delete().queue();

		Member reciever = getMember(event, args, 2);

		String selected = args.get(1);
		if(!RESPONSES.containsKey(selected)) {
			replyError(event.getChannel(), "Please specify a error", helpErrorBuilt);
			return;
		}
		sendError(event, RESPONSES.get(selected), event.getMember(), reciever);

	}

	private void sendError(CommandEvent event, String reply, Member sender, Member reciever) {

		if(reciever != null) {
			reply = reply.replace("%name%", reciever.getAsMention());
		}
		else {
			reply = reply.replace(" %name%", "");
		}
		
		final String finalReply = reply; //smh
		
		event.getTextChannel().createWebhook("Fake User Hook").queue(onWebHookComplete -> {

			

			WebhookClientBuilder builder = new WebhookClientBuilder(onWebHookComplete.getUrl()); // or id, token
			builder.setThreadFactory((job) -> {
				Thread thread = new Thread(job);
				thread.setName("WebHook Thread");
				thread.setDaemon(true);
				return thread;
			});
			builder.setWait(true);

			WebhookClient client = builder.build();

			WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();
			
			messageBuilder.setUsername(sender.getEffectiveName() + " (Auto Reply)");
			messageBuilder.setAvatarUrl(sender.getUser().getEffectiveAvatarUrl());
			messageBuilder.setContent(finalReply);
			
			messageBuilder.setAllowedMentions(AllowedMentions.all());
			

			WebhookMessage message = messageBuilder.build();
			client.send(message).thenAccept(onSuccess -> {

				client.close();
				onWebHookComplete.delete().queue();

			});

		});

	}

}
