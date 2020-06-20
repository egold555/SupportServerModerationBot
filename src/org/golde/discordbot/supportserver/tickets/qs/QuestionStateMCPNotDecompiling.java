package org.golde.discordbot.supportserver.tickets.qs;

import org.golde.discordbot.supportserver.tickets.QuestionStateAbstract;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class QuestionStateMCPNotDecompiling extends QuestionStateAbstract {

	public QuestionStateMCPNotDecompiling(TextChannel channel, Member member) {
		super(channel, member);
	}

	@Override
	protected String[] getQuestions() {
		return new String[] {
				"1) Please upload your MCP.log file. It is located in <project>/logs/mcp.log . You will need to zip it up in order to send it over Discord due to Discord file upload size limitation.",
				"2) If you have any additional information, please use this space."
		};
	}

}
