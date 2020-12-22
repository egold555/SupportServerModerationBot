package org.golde.discordbot.supportserver.command.chatmod;

import java.time.Instant;
import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.command.chatmod.ChatModCommand;

import com.jagrosh.jdautilities.command.CommandEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CommandLookup extends ChatModCommand {

	public CommandLookup(ESSBot bot) {
		super(bot, "lookup", "<user id>", "Look up a given user and avatar given the userid", "lu");
	}

	@Override
	protected void execute(CommandEvent event, List<String> args) {
		
		TextChannel tc = event.getTextChannel();
		
		if(args.isEmpty()) {
			replyError(tc, "You must give me a user ID to look up silly!");
			return;
		}
		
		long userId = 0;
		try {
			userId = Long.parseLong(args.get(1));
		}
		catch(NumberFormatException e) {
			replyError(tc, "Given argument was not a user id.");
			return;
		}
		
		Member memberTemp = bot.getGuild().getMemberById(userId);
		
		if(memberTemp == null) {
			//look the user up
			bot.getJda().retrieveUserById(userId).queue(
					newUser -> {
						displayUser(tc, newUser);
					}, 
					fail -> {
						replyError(tc, "Failed to look user id up on Discord's servers. Do they exist?");
						return;
					});
		}
		else {
			displayUser(tc, memberTemp.getUser());
		}
		
	}
	
	private void displayUser(TextChannel tc, User user) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTimestamp(Instant.now());
		builder.setFooter(bot.getJda().getSelfUser().getAsTag(), bot.getJda().getSelfUser().getAvatarUrl());

		builder.setAuthor(user.getAsTag());
		builder.setImage(user.getEffectiveAvatarUrl());


		reply(tc, builder.build());
	}

}
