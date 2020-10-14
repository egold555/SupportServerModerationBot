package org.golde.discordbot.utilities.command.everyone;

import java.awt.Color;
import java.io.File;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.everyone.EveryoneCommand;
import org.golde.discordbot.shared.constants.SSEmojis;
import org.golde.discordbot.shared.util.EnumReplyType;
import org.golde.discordbot.utilities.commonerror.CommonError;
import org.golde.discordbot.utilities.commonerror.CommonErrorManager;

import com.jagrosh.jdautilities.command.CommandEvent;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedTitle;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class CommandCommonError extends EveryoneCommand {

	private static String helpErrorBuilt = "";
	
	public static void reloadAfterCommonError() {
		helpErrorBuilt = "";
		
		for(CommonError ce : CommonErrorManager.getCommonErrors()) {
			if(ce.getShortCodes() != null && ce.getCmdDesc() != null) {
				String keyList = Arrays.toString(ce.getShortCodes());

				helpErrorBuilt += "`" + keyList + "` - _" + ce.getCmdDesc() + "_\n";
			}
			
		}
	}
	
	public CommandCommonError(@Nonnull ESSBot bot) {
		super(bot, "commonerror", "<error>", "Prints a link to a common error. Leave <error> blank for a list of common errors", "ce", "ec");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {


		//Member member = event.getMember();

		if(args.size() < 2) {
			replyError(event.getChannel(), "Please specify a error", helpErrorBuilt, 20);
			return;
		}
		
		String error = args.get(1);
		
		for(CommonError ce : CommonErrorManager.getCommonErrors()) {
			if(ce.getShortCodes() != null) {
				for(String key : ce.getShortCodes()) {
					if(key != null) {
						if(error.equalsIgnoreCase(key)) {
							
							int extraArgumentsAmount = args.size() - 2;
							String[] extraArguments = new String[extraArgumentsAmount];
							for(int i = 0; i < extraArgumentsAmount; i++) {
								extraArguments[i] = args.get(2 + i);
							}
							
							sendCEMessage(event.getTextChannel(), event.getMember(), ce, extraArguments);
							return;
						}
					}
				}
			}
			
		}
		
		replyError(event.getChannel(), "Common Error not found", helpErrorBuilt, 20);

	}
	
	private void sendCEMessage(TextChannel tc, Member sender, CommonError ce, String[] extraArguments) {
		
		String desc = ce.getDetailedDesc();
		for(int i = 0; i < extraArguments.length; i++) {
			desc = desc.replace("%A" + (i+1) + "%", extraArguments[i]);
		}
		
		if(ce.getCmdArgs() != null && extraArguments.length != ce.getCmdArgs().length) {
			replyError(tc, "Missing arguments", "This common error is missing the required extra arguments: " + Arrays.toString(ce.getCmdArgs()));
			return;
		}
		
		
		
		
		
		if(ce.getFakeUser()) {
			sendAsWebhook(tc, sender, ce, desc);
		}
		else {
			CommonErrorManager.sendCEMessage(bot, tc, ce, extraArguments);
		}
		
		
	}
	
	private void sendAsWebhook(TextChannel tc, Member sender, CommonError ce, String desc) {
		tc.createWebhook(sender.getEffectiveName() + " (Common Error Command)").queue(onWebHookComplete -> {

			

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
			WebhookMessageBuilder fileMessageBuilder = new WebhookMessageBuilder();
			
			messageBuilder.setUsername(sender.getEffectiveName() + " (Common Error Command)");
			messageBuilder.setAvatarUrl(sender.getUser().getEffectiveAvatarUrl());
			
			messageBuilder.setContent(desc);
			
			messageBuilder.setAllowedMentions(AllowedMentions.none());
			

			WebhookMessage message = messageBuilder.build();
			client.send(message).thenAccept(onSuccess -> {
				
				if(ce.getFileAttachments() != null && ce.getFileAttachments().length > 0) {
					for(String fileName : ce.getFileAttachments()) {
						
						
						
						File file = new File("res/common-error-attachments/" + fileName);
						System.out.println(file.exists() + " - " + file.getAbsolutePath());
						if(file.exists()) {
							
							fileMessageBuilder.addFile(file);
							
						}
					}
					
					client.send(fileMessageBuilder.build()).thenAccept(onSuccess2 -> {
						client.close();
						onWebHookComplete.delete().queue();
					});
					
				}
				else {
					client.close();
					onWebHookComplete.delete().queue();
				}

				

			});

		});
	}

}
