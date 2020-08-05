package org.golde.discordbot.utilities.command.owner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;

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
		
		RESPONSES.put("jdkver", "Looks like your using JDK %PH1% %name%. You need to be using JDK 8. You can download JDK 8 here: https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot");
		
		//RESPONSES.put("test", "Test - User: %name% | PH1: %PH1% | PH2: %PH2%");
		
		for(int i = 0; i < RESPONSES.size(); i++) {
			helpErrorBuilt += RESPONSES.keySet().toArray(new String[0])[i] + "\n";
		}
	}

	public CommandCommonResponse(@Nonnull ESSBot bot) {
		super(bot, "commonResponse", "<response id> <member> [arguments]. Leave blank for a list of all of them", "Sends a pre made common response. Kind of like common-errors", "cr");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {

		if(args.size() < 3) {
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
		
		//System.out.println(args);
		int extraArgumentsAmount = args.size() - 3;
		//System.out.println(extraArgumentsAmount);
		String[] extraArguments = new String[extraArgumentsAmount];
		for(int i = 0; i < extraArgumentsAmount; i++) {
			extraArguments[i] = args.get(3 + i);
		}
		//System.out.println(Arrays.toString(extraArguments));
		sendError(event, RESPONSES.get(selected), event.getMember(), reciever, extraArguments);

	}

	private void sendError(CommandEvent event, String reply, Member sender, Member reciever, String[] extraArguments) {

		reply = reply.replace("%name%", reciever.getAsMention());
		
		for(int i = 0; i < extraArguments.length; i++) {
			reply = reply.replace("%PH" + (i+1) + "%", extraArguments[i]);
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
