package org.golde.discordbot.utilities.command.owner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.owner.OwnerCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandAddReaction extends OwnerCommand {

	public CommandAddReaction(@Nonnull ESSBot bot) {
		super(bot, "addReaction", "<msg id> <emoji...>", "Makes the bot add reaction(s) to a message");
	}
	
	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		TextChannel tc = event.getTextChannel();
		Message message = event.getMessage();
		String rawMsg = args.get(1);
		

		//https://stackoverflow.com/questions/58348847/how-to-get-emojis-from-a-message
		String content = message.getContentRaw();
		List<String> emojis = EmojiParser.extractEmojis(content);
		List<String> customEmoji = message.getEmotes().stream()
		        .map((emote) -> emote.getName() + ":" + emote.getId())
		        .collect(Collectors.toList());

		// Create merged list
		List<String> merged = new ArrayList<>();
		merged.addAll(emojis);
		merged.addAll(customEmoji);

		// Sort based on index in message to preserve order
		merged.sort(Comparator.comparingInt(content::indexOf));
		
		//System.out.println(merged.toString());
		
		tc.retrieveMessageById(rawMsg).queue(onSuccess -> {

			for(String em : merged) {
				onSuccess.addReaction(em).queue();
			}
			
			
			tc.sendMessage("Successfully reacted with " + merged.size() + " emotes for message " + rawMsg).queue( onSuccessMsg -> {
				
				onSuccessMsg.delete().queueAfter(5, TimeUnit.SECONDS);
				
			});
			

		});
		
		
		
	}

}
