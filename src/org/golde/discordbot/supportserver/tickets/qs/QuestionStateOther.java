package org.golde.discordbot.supportserver.tickets.qs;

import org.golde.discordbot.supportserver.tickets.QuestionStateAbstract;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class QuestionStateOther extends QuestionStateAbstract {

	public QuestionStateOther(TextChannel channel, Member member) {
		super(channel, member);
	}

	@Override
	protected String[] getQuestions() {
		return new String[] {
				"1) Please describe the issue below."
		};
	}

}
