package org.golde.discordbot.supportserver.tickets.qs;

import org.golde.discordbot.supportserver.tickets.QuestionStateAbstract;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class QuestionStateErrorsInCode extends QuestionStateAbstract {

	public QuestionStateErrorsInCode(TextChannel channel, Member member) {
		super(channel, member);
	}

	@Override
	protected String[] getQuestions() {
		return new String[] {
				"1) Please describe what you are trying to do.",
				"2) Please describe what you expected to happen.",
				"3) Please upload a screenshot of the code that is erroring. If you don't have a screenshot tool, you can press the *PrtScr* button on your computer, and then press *CTRL + V* to paste it into Discord.",
				"4) Please screenshot your error pane in your IDE.",
				"5) If you have any additional information, please use this space."
		};
	}

}
