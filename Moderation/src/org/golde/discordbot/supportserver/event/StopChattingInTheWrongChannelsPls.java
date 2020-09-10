package org.golde.discordbot.supportserver.event;

import org.golde.discordbot.shared.ESSBot;
import org.golde.discordbot.shared.constants.Channels;
import org.golde.discordbot.shared.event.AbstractMessageChecker;

import com.vdurmont.emoji.EmojiParser;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class StopChattingInTheWrongChannelsPls extends AbstractMessageChecker {

	public StopChattingInTheWrongChannelsPls(ESSBot bot) {
		super(bot);
	}

	@Override
	protected boolean checkMessage(Member sender, Message msg) {
		String text = msg.getContentStripped(); //Must strip everything, thanks tascord for reporting this :)
		
		if(msg.getChannel().getIdLong() == Channels.UserContributions.USER_MADE_SNIPPETS) {

			text = text.toLowerCase();
			
			if(!text.contains("name:") || !text.contains("description:")) {
				return true;
			}

		}

//		if(msg.getChannel().getIdLong() == Channels.CLIENT_INVITES) {
//
//			return !text.toLowerCase().contains("https://discord.");
//
//		}

		return false;
	}

	@Override
	protected void takeAction(Guild guild, Member target, Message msg) {
		replySuccess(msg.getChannel(), "Please don't chat in here " + EmojiParser.parseToUnicode(":smile:"), 2, success -> {
			msg.delete().queue();
		});
		
		
	}

}
