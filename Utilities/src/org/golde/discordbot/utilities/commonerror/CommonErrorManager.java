package org.golde.discordbot.utilities.commonerror;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.util.EnumReplyType;
import org.golde.discordbot.shared.util.FileUtil;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class CommonErrorManager {

	@Getter private static List<CommonError> commonErrors = new ArrayList<CommonError>();

	public static void reload() {
		commonErrors.clear();
		commonErrors.addAll(FileUtil.loadArrayFromFile("common-errors", CommonError[].class));
	}

	public static void sendCEMessage(ESSBot bot, TextChannel tc, CommonError ce) {
		sendCEMessage(bot, tc, ce, null);
	}
	
	public static void sendCEMessage(ESSBot bot, TextChannel tc, CommonError ce, String[] extraArguments) {

		String desc = ce.getDetailedDesc();
		if(extraArguments != null) {
			for(int i = 0; i < extraArguments.length; i++) {
				desc = desc.replace("%A" + (i+1) + "%", extraArguments[i]);
			}
		}
		

		if(ce.getCmdArgs() != null && extraArguments.length != ce.getCmdArgs().length) {
			reply(tc, getReplyEmbedRaw(bot, EnumReplyType.ERROR, "Missing arguments", "This common error is missing the required extra arguments: " + Arrays.toString(ce.getCmdArgs())));
			return;
		}

		MessageEmbed embed = getReplyEmbed(bot,
				EnumReplyType.SUCCESS, 
				"This error has been answered before", 
				desc
				);

		MessageAction theMsg = tc.sendMessage(embed);
		MessageAction attachmentMessage = tc.sendMessage(" ");

		if(ce.getFileAttachments() != null && ce.getFileAttachments().length > 0) {
			for(String fileName : ce.getFileAttachments()) {



				File file = new File("res/common-error-attachments/" + fileName);
				System.out.println(file.exists() + " - " + file.getAbsolutePath());
				if(file.exists()) {

					attachmentMessage.addFile(file);

				}
			}

		}

		theMsg.queue(onSuccess -> {
			if(ce.getFileAttachments() != null && ce.getFileAttachments().length > 0) {
				attachmentMessage.queue();
			}

		});

	}

	protected static final EmbedBuilder getReplyEmbedRaw(ESSBot bot, EnumReplyType type, String title, String desc) {

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

	protected static final MessageEmbed getReplyEmbed(ESSBot bot, EnumReplyType type, String title, String desc) {
		return getReplyEmbedRaw(bot, type, title, desc).build();
	}

	protected static void reply(MessageChannel channel, EmbedBuilder builder) {
		channel.sendMessage(builder.build()).queue();
	}
	
}
