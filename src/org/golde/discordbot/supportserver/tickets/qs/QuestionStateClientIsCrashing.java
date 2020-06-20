package org.golde.discordbot.supportserver.tickets.qs;

import org.golde.discordbot.supportserver.tickets.QuestionStateAbstract;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class QuestionStateClientIsCrashing extends QuestionStateAbstract {

	public QuestionStateClientIsCrashing(TextChannel channel, Member member) {
		super(channel, member);
	}

	@Override
	protected String[] getQuestions() {
		return new String[] {
				"1) Please describe what you expected to have happen.",
				"2) Please describe what actually happened.",
				"3) If you have any errors in your IDE's Problems tab, please upload a screenshot.",
				"4) Please upload your crash report now. **You can find your crash reports in <project>/jars/crash-reports/** .",
				"5) Please upload your log file. **You can find it in <project>/jars/logs/latest.log** .",
				"6) If you have any additional information, please use this space."
		};
	}

}
