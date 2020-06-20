package org.golde.discordbot.supportserver.tickets.qs;

import org.golde.discordbot.supportserver.tickets.QuestionStateAbstract;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class QuestionStateIssueWithOtherUser extends QuestionStateAbstract {

	public QuestionStateIssueWithOtherUser(TextChannel channel, Member member) {
		super(channel, member);
	}

	@Override
	protected String[] getQuestions() {
		return new String[] {
				"1) Who are you having this issue with?",
				"2) What is the issue you are having?",
				"3) Please paste screenshots below of your conversation.",
				"4) Would you like a moderator to add this person to this ticket to try to sort things out?",
				"5) If you have any additional information, please use this space."
		};
	}

}
