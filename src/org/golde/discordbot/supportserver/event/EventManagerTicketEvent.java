package org.golde.discordbot.supportserver.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.golde.discordbot.supportserver.constants.Categories;
import org.golde.discordbot.supportserver.constants.Channels;
import org.golde.discordbot.supportserver.constants.SSEmojis;
import org.golde.discordbot.supportserver.tickets.QuestionStateAbstract;
import org.golde.discordbot.supportserver.tickets.TicketManager;
import org.golde.discordbot.supportserver.tickets.qs.QuestionStateClientIsCrashing;
import org.golde.discordbot.supportserver.tickets.qs.QuestionStateErrorsInCode;
import org.golde.discordbot.supportserver.tickets.qs.QuestionStateIssueWithOtherUser;
import org.golde.discordbot.supportserver.tickets.qs.QuestionStateMCPNotDecompiling;
import org.golde.discordbot.supportserver.tickets.qs.QuestionStateOther;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class EventManagerTicketEvent extends EventBase {

	//ok so I need to come up with a better thing. Issue is there is no ascii version of these. 
	//SSEmotes should have emote classes where I can do .equals. With discord handled this!!!
	private static final String ONE = "U+31U+20e3";
	private static final String TWO = "U+32U+20e3";
	private static final String THREE = "U+33U+20e3";
	private static final String FOUR = "U+34U+20e3";
	private static final String QUESTION = "U+2753";

	static Map<Long, QuestionStateAbstract> questionState = new HashMap<Long, QuestionStateAbstract>();

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event)
	{
		TextChannel tc = event.getChannel();
		MessageReaction reaction = event.getReaction();
		ReactionEmote emote = event.getReactionEmote();
		Member member = event.getMember();
		User user = member.getUser();

		if(user.isBot() || user.isFake()) {
			return;
		}

		if(!tc.getName().startsWith("temp-t-") && tc.getIdLong() != Channels.TICKETS) {
			return;
		}





		String channelName = "temp-t-" + UUID.randomUUID().toString().substring(0, 16);
		if(can(member)) {
			if(emote.getAsCodepoints().equals(ONE)) {
				member.getGuild().getCategoryById(Categories.TEMPORARY_CHANNELS).createTextChannel(channelName).queue(success -> {
					questionState.put(member.getIdLong(), new QuestionStateMCPNotDecompiling(success, member));
				});
			}
			else if(emote.getAsCodepoints().equals(TWO)) {
				member.getGuild().getCategoryById(Categories.TEMPORARY_CHANNELS).createTextChannel(channelName).queue(success -> {
					questionState.put(member.getIdLong(), new QuestionStateErrorsInCode(success, member));
				});
			}
			else if(emote.getAsCodepoints().equals(THREE)) {
				member.getGuild().getCategoryById(Categories.TEMPORARY_CHANNELS).createTextChannel(channelName).queue(success -> {
					questionState.put(member.getIdLong(), new QuestionStateClientIsCrashing(success, member));
				});
			}
			else if(emote.getAsCodepoints().equals(FOUR)) {
				member.getGuild().getCategoryById(Categories.TEMPORARY_CHANNELS).createTextChannel(channelName).queue(success -> {
					questionState.put(member.getIdLong(), new QuestionStateIssueWithOtherUser(success, member));
				});
			}
			else if(emote.getAsCodepoints().equals(QUESTION)) {
				member.getGuild().getCategoryById(Categories.TEMPORARY_CHANNELS).createTextChannel(channelName).queue(success -> {
					questionState.put(member.getIdLong(), new QuestionStateOther(success, member));
				});
			}
		}

		//special case
		if(emote.getName().equals(SSEmojis.NEXT) && questionState.containsKey(member.getIdLong())) {
			questionState.get(member.getIdLong()).sendNextQuestion();
			//remove bot reaction

			reaction.removeReaction().queue();
		}

		//		System.out.println(emote);
		//remove user reaction
		reaction.removeReaction(user).queue();


	}

	private boolean can(Member member) {

		if(!TicketManager.alreadyHadTicket(member)) {
			if(QuestionStateAbstract.inTemp.contains(member.getIdLong())) {
				return false;
			}
		}

		return !TicketManager.alreadyHadTicket(member);
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

		if(event.getAuthor().isBot() || event.getAuthor().isFake()) {
			return;
		}

		if(!event.getChannel().getName().startsWith("temp-t-")) {
			return;
		}

		Set<Long> toRemove = new HashSet<Long>();

		for(QuestionStateAbstract qs : questionState.values()) {
			if(qs.getChannel().getIdLong() == event.getChannel().getIdLong()) {
				qs.recieved(event.getMessage());
			}
			if(qs.isFinished()) {
				toRemove.add(qs.getMemberId());
			}
		}

		questionState.keySet().removeAll(toRemove);

	}

}
