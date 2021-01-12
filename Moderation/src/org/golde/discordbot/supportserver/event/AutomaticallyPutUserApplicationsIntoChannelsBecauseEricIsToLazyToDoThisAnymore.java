package org.golde.discordbot.supportserver.event;

import java.util.List;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Categories;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.EventBase;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

//yeah its a long class name ikik. 
public class AutomaticallyPutUserApplicationsIntoChannelsBecauseEricIsToLazyToDoThisAnymore extends EventBase {

	public AutomaticallyPutUserApplicationsIntoChannelsBecauseEricIsToLazyToDoThisAnymore(ESSBot bot) {
		super(bot);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		TextChannel tc = event.getChannel();
		Message msg = event.getMessage();
		User user = event.getAuthor();
		Guild g = event.getGuild();

		if(user.isBot() || user.isFake()) {
			if(tc.getIdLong() == Channels.Logs.NEW_APPLICATIONS || tc.getIdLong() == Channels.Logs.UNBAN_APPEAL) {
				List<MessageEmbed> embeds = msg.getEmbeds();

				if(embeds.size() > 0) {
					try {
						processNewApplicationEmbed(g, embeds.get(0));
					}
					catch(Throwable e) {
						g.getTextChannelById(Channels.ModStuffThings.MOD_CHAT).sendMessage("<@199652118100049921> Hey! Something bad happened when processing a webhook! Check the recent applications. \n\n```" + e.getMessage() + "```").queue();
						e.printStackTrace();
					}
				}

			}

		}

	}

	private void processNewApplicationEmbed(Guild g, MessageEmbed embed) throws Throwable {

		StringBuilder sb = new StringBuilder();
		String userName = null;
		String applyingFor = null;

		for(Field f : embed.getFields()) {

			sb.append("**" + f.getName() + "**\n");

			if(f.getName().toLowerCase().contains("username")) {
				userName = fieldToUsername(g, f.getValue());
				sb.append(fieldToMention(g, f.getValue()) + "\n\n");
			}
			else if(f.getName().contains("banned you")) {
				sb.append(fieldToMention(g, f.getValue()) + "\n\n");
			}
			else {
				sb.append(f.getValue() + "\n\n");
			}

			if(f.getName().contains("apply for")) {
				applyingFor = f.getValue().replaceAll("[a-z]", "" ).replace(" ", "");

			}

			if(f.getName().contains("Ban")) {
				applyingFor = "UA";

			}


		}
		
		if(userName == null || applyingFor == null) {
			throw new Exception("userName or applyingFor variable was null");
		}


		Category category = g.getCategoryById(Categories.APPLICATIONS);
		String channelName = applyingFor + "-" + userName.split("#")[0];

		g.createTextChannel(channelName).setParent(category).queue(theChannel -> {
			g.modifyTextChannelPositions(category).selectPosition(theChannel).moveUp(1).queue();
			theChannel.sendMessage(sb.toString()).queue();
		});


	}

	private String fieldToMention(Guild g, String fieldValue) {

		User user = getUser(g, fieldValue);
		if(user == null) {
			return fieldValue;
		}
		else {
			return user.getAsMention();
		}

	}
	
	private String fieldToUsername(Guild g, String fieldValue) {

		User user = getUser(g, fieldValue);
		if(user == null) {
			return fieldValue;
		}
		else {
			return user.getName();
		}

	}

	private User getUser(Guild g, String fieldValue) {

		fieldValue = fieldValue.replace(" #", "#");

		try {
			return bot.getJda().getUserByTag(fieldValue);
		}
		catch(Exception e) {
			return null;
		}

	}

}
